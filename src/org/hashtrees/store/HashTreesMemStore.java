package org.hashtrees.store;

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

/**
 * In memory implementation of {@link HashTreesStore}.
 * 
 */
@ThreadSafe
public class HashTreesMemStore extends HashTreesBaseStore {

	private final ConcurrentMap<Long, HashTreeMemStore> treeIdAndIndHashTree = new ConcurrentHashMap<>();

	private static class HashTreeMemStore {
		private final ConcurrentMap<Integer, ByteBuffer> segmentHashes = new ConcurrentSkipListMap<Integer, ByteBuffer>();
		private final ConcurrentMap<Integer, ConcurrentSkipListMap<ByteBuffer, ByteBuffer>> segDataBlocks = new ConcurrentHashMap<Integer, ConcurrentSkipListMap<ByteBuffer, ByteBuffer>>();
		private final AtomicLong lastRebuiltTS = new AtomicLong(0);
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
				return new SegmentData(intKey, value);
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
		hTreeStore.segDataBlocks.get(segId).put(key, digest);
	}

	@Override
	public void deleteSegmentData(long treeId, int segId, ByteBuffer key) {
		HashTreeMemStore indPartition = getIndHTree(treeId);
		Map<ByteBuffer, ByteBuffer> segDataBlock = indPartition.segDataBlocks
				.get(segId);
		if (segDataBlock != null)
			segDataBlock.remove(key);
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
			result.add(new SegmentData(entry.getKey(), entry.getValue()));
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
		return new Iterator<SegmentData>() {

			Iterator<Map.Entry<ByteBuffer, ByteBuffer>> itr = null;

			@Override
			public boolean hasNext() {
				if (itr == null || !itr.hasNext()) {
					while (dataBlocksItr.hasNext()) {
						itr = dataBlocksItr.next().getValue().entrySet()
								.iterator();
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
				return new SegmentData(entry.getKey(), entry.getValue());
			}
		};
	}

	@Override
	public void putSegmentHash(long treeId, int nodeId, ByteBuffer digest) {
		HashTreeMemStore indPartition = getIndHTree(treeId);
		ByteBuffer intDigest = ByteBuffer.wrap(digest.array());
		indPartition.segmentHashes.put(nodeId, intDigest);
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
	public Iterator<Long> getAllTreeIds() {
		return treeIdAndIndHashTree.keySet().iterator();
	}

	@Override
	public void deleteTree(long treeId) {
		treeIdAndIndHashTree.remove(treeId);
	}

	@Override
	public void markSegments(long treeId, List<Integer> segIds) {
		// Intended to be reused across process restarts. This is in memory
		// store, and does not persist anything. Implementing anything won't
		// help here.
	}

	@Override
	public void unmarkSegments(long treeId, List<Integer> segIds) {
		// Intended to be reused across process restarts. This is in memory
		// store, and does not persist anything. Implementing anything won't
		// help here.
	}

	@Override
	public List<Integer> getMarkedSegments(long treeId) {
		// Intended to be reused across process restarts. This is in memory
		// store, and does not persist anything. Implementing anything won't
		// help here.
		return Collections.emptyList();
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
