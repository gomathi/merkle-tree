package org.hashtrees.storage;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.hashtrees.util.AtomicBitSet;

public abstract class HashTreesBaseStore implements HashTreesStore {

	private final ConcurrentMap<Long, AtomicBitSet> treeIdAndDirtySegmentMap = new ConcurrentHashMap<Long, AtomicBitSet>();

	private AtomicBitSet getDirtySegmentsHolder(long treeId) {
		if (!treeIdAndDirtySegmentMap.containsKey(treeId))
			treeIdAndDirtySegmentMap.putIfAbsent(treeId, new AtomicBitSet());
		return treeIdAndDirtySegmentMap.get(treeId);
	}

	@Override
	public void setDirtySegment(long treeId, int segId) {
		getDirtySegmentsHolder(treeId).set(segId);
	}

	@Override
	public List<Integer> clearAndGetDirtySegments(long treeId) {
		return getDirtySegmentsHolder(treeId).clearAndGetAllSetBits();
	}
}
