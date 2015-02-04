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
package org.hashtrees.test.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import org.hashtrees.HashTrees;
import org.hashtrees.HashTreesObserver;
import org.hashtrees.SyncDiffResult;
import org.hashtrees.SyncType;
import org.hashtrees.thrift.generated.KeyValue;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;

/**
 * Defined to use in unit testing. Subclasses can override only the methods that
 * is interested in.
 * 
 */
public class MockHashTrees implements HashTrees {

	@Override
	public void sRemove(List<ByteBuffer> keys) throws IOException {

	}

	@Override
	public List<SegmentHash> getSegmentHashes(long treeId, List<Integer> nodeIds)
			throws IOException {
		return Collections.emptyList();
	}

	@Override
	public SegmentHash getSegmentHash(long treeId, int nodeId)
			throws IOException {
		return new SegmentHash();
	}

	@Override
	public List<SegmentData> getSegment(long treeId, int segId)
			throws IOException {
		return Collections.emptyList();
	}

	@Override
	public SegmentData getSegmentData(long treeId, int segId, ByteBuffer key)
			throws IOException {
		return new SegmentData();
	}

	@Override
	public void hPut(ByteBuffer key, ByteBuffer value) throws IOException {

	}

	@Override
	public void hRemove(ByteBuffer key) throws IOException {

	}

	@Override
	public SyncDiffResult synch(long treeId, HashTrees remoteTree,
			SyncType syncType) throws IOException {
		return new SyncDiffResult(0, 0);
	}

	@Override
	public SyncDiffResult synch(long treeId, HashTrees remoteTree)
			throws IOException {
		return new SyncDiffResult(0, 0);
	}

	@Override
	public int rebuildHashTree(long treeId, long fullRebuildPeriod)
			throws IOException {
		return 0;
	}

	@Override
	public int rebuildHashTree(long treeId, boolean fullRebuild)
			throws IOException {
		return 0;
	}

	@Override
	public void addObserver(HashTreesObserver listener) {

	}

	@Override
	public void removeObserver(HashTreesObserver listener) {

	}

	@Override
	public void sPut(List<KeyValue> keyValuePairs) throws IOException {

	}

	@Override
	public void deleteTreeNode(long treeId, int nodeId) throws IOException {

	}

}
