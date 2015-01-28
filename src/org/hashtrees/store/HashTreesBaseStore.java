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
 */package org.hashtrees.store;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.hashtrees.HashTrees;
import org.hashtrees.util.AtomicBitSet;

/**
 * In memory store, only stores the dirty segments. Tries to serve read calls
 * from memory. When it thinks it might not have the latest data in memory, it
 * calls the subclass to return the actual values, and caches them in memory for
 * the next calls.
 * 
 * Also updates dirtySegments with result from {@link #markSegments(long, List)}
 * . So {@link HashTrees} does not have to take care of it.
 * 
 */
public abstract class HashTreesBaseStore implements HashTreesStore {

	private final ConcurrentMap<Long, AtomicBitSet> treeIdAndDirtySegmentMap = new ConcurrentHashMap<Long, AtomicBitSet>();

	private AtomicBitSet initializeDirtySegments(long treeId)
			throws IOException {
		AtomicBitSet dirtySegmentsBitSet = new AtomicBitSet();
		List<Integer> dirtySegments = getDirtySegmentsInternal(treeId);
		for (int dirtySegment : dirtySegments)
			dirtySegmentsBitSet.set(dirtySegment);
		List<Integer> markedSegments = getMarkedSegments(treeId);
		for (int markedSegment : markedSegments)
			dirtySegmentsBitSet.set(markedSegment);
		return dirtySegmentsBitSet;
	}

	private AtomicBitSet getDirtySegmentsHolder(long treeId) throws IOException {
		if (!treeIdAndDirtySegmentMap.containsKey(treeId))
			treeIdAndDirtySegmentMap.putIfAbsent(treeId,
					initializeDirtySegments(treeId));
		return treeIdAndDirtySegmentMap.get(treeId);
	}

	@Override
	public boolean setDirtySegment(long treeId, int segId) throws IOException {
		boolean prevValue = getDirtySegmentsHolder(treeId).set(segId);
		if (!prevValue)
			setDirtySegmentInternal(treeId, segId);
		return prevValue;
	}

	@Override
	public List<Integer> getDirtySegments(long treeId) throws IOException {
		return getDirtySegmentsHolder(treeId).getAllSetBits();
	}

	@Override
	public boolean clearDirtySegment(long treeId, int segId) throws IOException {
		boolean prevValue = getDirtySegmentsHolder(treeId).clear(segId);
		if (prevValue)
			clearDirtySegmentInternal(treeId, segId);
		return prevValue;
	}

	protected abstract void setDirtySegmentInternal(long treeId, int segId)
			throws IOException;

	protected abstract void clearDirtySegmentInternal(long treeId, int segId)
			throws IOException;

	protected abstract List<Integer> getDirtySegmentsInternal(long treeId)
			throws IOException;
}
