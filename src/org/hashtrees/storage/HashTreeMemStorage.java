package org.hashtrees.storage;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.concurrent.ThreadSafe;

import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;

/**
 * In memory implementation of {@link HashTreeStorage} used only for unit
 * testing.
 * 
 */
@ThreadSafe
public class HashTreeMemStorage implements HashTreeStorage {

	private final int noOfSegDataBlocks;
	private final ConcurrentMap<Long, IndHashTreeMemStorage> treeIdAndIndHashTree = new ConcurrentHashMap<Long, IndHashTreeMemStorage>();

	public HashTreeMemStorage(int noOfSegDataBlocks) {
		this.noOfSegDataBlocks = noOfSegDataBlocks;
	}

	private IndHashTreeMemStorage getIndHTree(long treeId) {
		if (!treeIdAndIndHashTree.containsKey(treeId))
			treeIdAndIndHashTree.putIfAbsent(treeId, new IndHashTreeMemStorage(
					noOfSegDataBlocks));
		return treeIdAndIndHashTree.get(treeId);
	}

	@Override
	public void putSegmentData(long treeId, int segId, ByteBuffer key,
			ByteBuffer digest) {
		getIndHTree(treeId).putSegmentData(segId, key, digest);
	}

	@Override
	public void deleteSegmentData(long treeId, int segId, ByteBuffer key) {
		getIndHTree(treeId).deleteSegmentData(segId, key);
	}

	@Override
	public List<SegmentData> getSegment(long treeId, int segId) {
		return getIndHTree(treeId).getSegment(segId);
	}

	@Override
	public void putSegmentHash(long treeId, int nodeId, ByteBuffer digest) {
		getIndHTree(treeId).putSegmentHash(nodeId, digest);
	}

	@Override
	public List<SegmentHash> getSegmentHashes(long treeId,
			Collection<Integer> nodeIds) {
		return getIndHTree(treeId).getSegmentHashes(nodeIds);
	}

	@Override
	public void setDirtySegment(long treeId, int segId) {
		getIndHTree(treeId).setDirtySegment(segId);
	}

	@Override
	public List<Integer> clearAndGetDirtySegments(long treeId) {
		return getIndHTree(treeId).clearAndGetDirtySegments();
	}

	@Override
	public void deleteTree(long treeId) {
		treeIdAndIndHashTree.remove(treeId);
	}

	@Override
	public SegmentData getSegmentData(long treeId, int segId, ByteBuffer key) {
		return getIndHTree(treeId).getSegmentData(segId, key);
	}

	@Override
	public SegmentHash getSegmentHash(long treeId, int nodeId) {
		return getIndHTree(treeId).getSegmentHash(nodeId);
	}

	@Override
	public void clearAllSegments(long treeId) {
		getIndHTree(treeId).clearDirtySegments();
	}

	@Override
	public void setLastFullyTreeBuiltTimestamp(long treeId, long timestamp) {
		getIndHTree(treeId).setLastFullyRebuiltTimestamp(timestamp);
	}

	@Override
	public long getLastFullyTreeReBuiltTimestamp(long treeId) {
		return getIndHTree(treeId).getLastTreeFullyRebuiltTimestamp();
	}

	@Override
	public void setLastHashTreeUpdatedTimestamp(long treeId, long timestamp) {
		getIndHTree(treeId).setLastHashTreeUpdatedTimestamp(timestamp);
	}

	@Override
	public long getLastHashTreeUpdatedTimestamp(long treeId) {
		return getIndHTree(treeId).getLastHashTreeUpdatedTimestamp();
	}

	@Override
	public Iterator<Long> getAllTreeIds() {
		return treeIdAndIndHashTree.keySet().iterator();
	}
}
