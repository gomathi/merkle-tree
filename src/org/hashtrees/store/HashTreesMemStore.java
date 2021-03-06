/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.hashtrees.store;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.ThreadSafe;

import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;
import org.hashtrees.util.AtomicBitSet;

/**
 * In memory implementation of {@link HashTreesStore}.
 * 
 */
@ThreadSafe
public class HashTreesMemStore extends HashTreesBaseStore {

	private final ConcurrentMap<Long, HashTreeMemStore> treeIdAndIndHashTree = new ConcurrentHashMap<>();

	private static class HashTreeMemStore {
		private final ConcurrentMap<Integer, ByteBuffer> segmentHashes = new ConcurrentSkipListMap<Integer, ByteBuffer>();
		private final ConcurrentSkipListMap<Integer, ConcurrentSkipListMap<ByteBuffer, ByteBuffer>> segDataBlocks = new ConcurrentSkipListMap<Integer, ConcurrentSkipListMap<ByteBuffer, ByteBuffer>>();
		private final AtomicLong lastRebuiltTS = new AtomicLong(0);
		private final AtomicBitSet markedSegments = new AtomicBitSet();
	}

	private HashTreeMemStore getIndHTree(long treeId) {
		if (!treeIdAndIndHashTree.containsKey(treeId))
			treeIdAndIndHashTree.putIfAbsent(treeId, new HashTreeMemStore());
		return treeIdAndIndHashTree.get(treeId);
	}

	@Override
	public SegmentData getSegmentData(long treeId, int segId, ByteBuffer key) {
		ConcurrentSkipListMap<ByteBuffer, ByteBuffer> segDataBlock = getIndHTree(treeId).segDataBlocks
				.get(segId);
		if (segDataBlock != null) {
			ByteBuffer value = segDataBlock.get(key);
			if (value != null) {
				ByteBuffer intKey = ByteBuffer.wrap(key.array());
				return new SegmentData(segId, intKey, value);
			}
		}
		return null;
	}

	@Override
	public void putSegmentData(long treeId, int segId, ByteBuffer key,
			ByteBuffer digest) {
		HashTreeMemStore hTreeStore = getIndHTree(treeId);
		if (!hTreeStore.segDataBlocks.containsKey(segId))
			hTreeStore.segDataBlocks.putIfAbsent(segId,
					new ConcurrentSkipListMap<ByteBuffer, ByteBuffer>());
		hTreeStore.segDataBlocks.get(segId).put(key.duplicate(),
				digest.duplicate());
	}

	@Override
	public void deleteSegmentData(long treeId, int segId, ByteBuffer key) {
		HashTreeMemStore indPartition = getIndHTree(treeId);
		Map<ByteBuffer, ByteBuffer> segDataBlock = indPartition.segDataBlocks
				.get(segId);
		if (segDataBlock != null)
			segDataBlock.remove(key.duplicate());
	}

	@Override
	public List<SegmentData> getSegment(long treeId, int segId) {
		HashTreeMemStore indPartition = getIndHTree(treeId);
		ConcurrentMap<ByteBuffer, ByteBuffer> segDataBlock = indPartition.segDataBlocks
				.get(segId);
		if (segDataBlock == null)
			return Collections.emptyList();
		List<SegmentData> result = new ArrayList<SegmentData>();
		for (Map.Entry<ByteBuffer, ByteBuffer> entry : segDataBlock.entrySet())
			result.add(new SegmentData(segId, entry.getKey(), entry.getValue()));
		return result;
	}

	/**
	 * Iterator returned by this method is not thread safe.
	 */
	@Override
	public Iterator<SegmentData> getSegmentDataIterator(long treeId) {
		final HashTreeMemStore memStore = getIndHTree(treeId);
		final Iterator<Map.Entry<Integer, ConcurrentSkipListMap<ByteBuffer, ByteBuffer>>> dataBlocksItr = memStore.segDataBlocks
				.entrySet().iterator();
		return convertMapEntriesToSegmentData(dataBlocksItr);
	}

	@Override
	public Iterator<SegmentData> getSegmentDataIterator(long treeId,
			int fromSegId, int toSegId) throws IOException {
		final HashTreeMemStore memStore = getIndHTree(treeId);
		final Iterator<Map.Entry<Integer, ConcurrentSkipListMap<ByteBuffer, ByteBuffer>>> dataBlocksItr = memStore.segDataBlocks
				.subMap(fromSegId, true, toSegId, true).entrySet().iterator();
		return convertMapEntriesToSegmentData(dataBlocksItr);
	}

	private static Iterator<SegmentData> convertMapEntriesToSegmentData(
			final Iterator<Map.Entry<Integer, ConcurrentSkipListMap<ByteBuffer, ByteBuffer>>> dataBlocksItr) {
		return new Iterator<SegmentData>() {

			volatile int segId;
			volatile Iterator<Map.Entry<ByteBuffer, ByteBuffer>> itr = null;

			@Override
			public boolean hasNext() {
				if (itr == null || !itr.hasNext()) {
					while (dataBlocksItr.hasNext()) {
						Map.Entry<Integer, ConcurrentSkipListMap<ByteBuffer, ByteBuffer>> entry = dataBlocksItr
								.next();
						segId = entry.getKey();
						itr = entry.getValue().entrySet().iterator();
						if (itr.hasNext())
							break;
					}
				}
				return (itr != null) && itr.hasNext();
			}

			@Override
			public SegmentData next() {
				if (itr == null || !itr.hasNext())
					throw new NoSuchElementException(
							"No more elements exist to return.");
				Map.Entry<ByteBuffer, ByteBuffer> entry = itr.next();
				return new SegmentData(segId, entry.getKey(), entry.getValue());
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public void putSegmentHash(long treeId, int nodeId, ByteBuffer digest) {
		HashTreeMemStore indPartition = getIndHTree(treeId);
		indPartition.segmentHashes.put(nodeId, digest.duplicate());
	}

	@Override
	public SegmentHash getSegmentHash(long treeId, int nodeId) {
		HashTreeMemStore indPartition = getIndHTree(treeId);
		ByteBuffer hash = indPartition.segmentHashes.get(nodeId);
		if (hash == null)
			return null;
		return new SegmentHash(nodeId, hash);
	}

	@Override
	public List<SegmentHash> getSegmentHashes(long treeId,
			Collection<Integer> nodeIds) {
		List<SegmentHash> result = new ArrayList<SegmentHash>();
		for (int nodeId : nodeIds) {
			ByteBuffer hash = getIndHTree(treeId).segmentHashes.get(nodeId);
			if (hash != null)
				result.add(new SegmentHash(nodeId, hash));
		}
		return result;
	}

	@Override
	public void setCompleteRebuiltTimestamp(long treeId, long timestamp) {
		HashTreeMemStore indTree = getIndHTree(treeId);
		indTree.lastRebuiltTS.set(timestamp);
	}

	@Override
	public long getCompleteRebuiltTimestamp(long treeId) {
		long value = getIndHTree(treeId).lastRebuiltTS.get();
		return value;
	}

	@Override
	public void deleteTree(long treeId) {
		treeIdAndIndHashTree.remove(treeId);
	}

	@Override
	public void markSegments(long treeId, List<Integer> segIds) {
		for (int segId : segIds)
			getIndHTree(treeId).markedSegments.set(segId);
	}

	@Override
	public void unmarkSegments(long treeId, List<Integer> segIds) {
		getIndHTree(treeId).markedSegments.clearBits(segIds);
	}

	@Override
	public List<Integer> getMarkedSegments(long treeId) {
		return getIndHTree(treeId).markedSegments.getAllSetBits();
	}

	@Override
	public void stop() {
		// Nothing to stop.
	}

	@Override
	public void start() {
		// Nothing to initialize
	}

	@Override
	protected void setDirtySegmentInternal(long treeId, int segId) {
		// Nothing to do.
	}

	@Override
	protected void clearDirtySegmentInternal(long treeId, int segId) {
		// Nothing to do.
	}

	@Override
	protected List<Integer> getDirtySegmentsInternal(long treeId) {
		return Collections.emptyList();
	}
}
