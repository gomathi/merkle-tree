package org.hashtrees.store;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.ThreadSafe;

import org.hashtrees.thrift.generated.RemoteTreeInfo;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;

/**
 * In memory implementation of {@link HashTreesStore}.
 * 
 */
@ThreadSafe
public class HashTreesMemStore extends HashTreesBaseStore implements
		HashTreesManagerStore {

	private final ConcurrentMap<Long, HashTreeMemStore> treeIdAndIndHashTree = new ConcurrentHashMap<>();
	private final ConcurrentMap<Long, ConcurrentSkipListSet<RemoteTreeInfo>> servers = new ConcurrentSkipListMap<>();

	private static class HashTreeMemStore {
		private final ConcurrentMap<Integer, ByteBuffer> segmentHashes = new ConcurrentSkipListMap<Integer, ByteBuffer>();
		private final ConcurrentMap<Integer, ConcurrentSkipListMap<ByteBuffer, ByteBuffer>> segDataBlocks = new ConcurrentHashMap<Integer, ConcurrentSkipListMap<ByteBuffer, ByteBuffer>>();
		private final AtomicLong fullyRebuiltTreeTs = new AtomicLong(0);
	}

	private HashTreeMemStore getIndHTree(long treeId) {
		if (!treeIdAndIndHashTree.containsKey(treeId))
			treeIdAndIndHashTree.putIfAbsent(treeId, new HashTreeMemStore());
		return treeIdAndIndHashTree.get(treeId);
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

	@Override
	public void putSegmentHash(long treeId, int nodeId, ByteBuffer digest) {
		HashTreeMemStore indPartition = getIndHTree(treeId);
		ByteBuffer intDigest = ByteBuffer.wrap(digest.array());
		indPartition.segmentHashes.put(nodeId, intDigest);
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
	public void deleteTree(long treeId) {
		treeIdAndIndHashTree.remove(treeId);
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
	public SegmentHash getSegmentHash(long treeId, int nodeId) {
		HashTreeMemStore indPartition = getIndHTree(treeId);
		ByteBuffer hash = indPartition.segmentHashes.get(nodeId);
		if (hash == null)
			return null;
		return new SegmentHash(nodeId, hash);
	}

	@Override
	public void setLastFullyTreeBuiltTimestamp(long treeId, long timestamp) {
		HashTreeMemStore indTree = getIndHTree(treeId);
		setValueIfNewValueIsGreater(indTree.fullyRebuiltTreeTs, timestamp);
	}

	@Override
	public long getLastFullyTreeBuiltTimestamp(long treeId) {
		long value = getIndHTree(treeId).fullyRebuiltTreeTs.get();
		return value;
	}

	@Override
	public Iterator<Long> getAllTreeIds() {
		return treeIdAndIndHashTree.keySet().iterator();
	}

	private static void setValueIfNewValueIsGreater(AtomicLong val, long value) {
		long oldValue = val.get();
		while (oldValue < value) {
			if (val.compareAndSet(oldValue, value))
				break;
			oldValue = val.get();
		}
	}

	private ConcurrentSkipListSet<RemoteTreeInfo> getRemoteTreeList(long treeId) {
		if (!servers.containsKey(treeId))
			servers.putIfAbsent(treeId,
					new ConcurrentSkipListSet<RemoteTreeInfo>());
		return servers.get(treeId);
	}

	@Override
	public void addToSyncList(RemoteTreeInfo rTree) {
		getRemoteTreeList(rTree.treeId).add(rTree);
	}

	@Override
	public void removeFromSyncList(RemoteTreeInfo rTree) {
		getRemoteTreeList(rTree.treeId).remove(rTree);
	}

	@Override
	public List<RemoteTreeInfo> getSyncList(long treeId) {
		return new ArrayList<RemoteTreeInfo>(getRemoteTreeList(treeId));
	}

	@Override
	public void stop() {
		// No actions to do.
	}

	@Override
	public void markSegmentsForRebuild(long treeId, List<Integer> segIds) {

	}

	@Override
	public void unmarkSegmentsForRebuild(long treeId, List<Integer> segIds) {

	}
}
