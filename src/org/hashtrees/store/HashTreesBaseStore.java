package org.hashtrees.store;

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
	public boolean setDirtySegment(long treeId, int segId) {
		return getDirtySegmentsHolder(treeId).set(segId);
	}

	@Override
	public List<Integer> getDirtySegments(long treeId) {
		return getDirtySegmentsHolder(treeId).getAllSetBits();
	}

	@Override
	public void clearDirtySegment(long treeId, int segId) {
		getDirtySegmentsHolder(treeId).clear(segId);
	}
}
