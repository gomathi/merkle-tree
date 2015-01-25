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

// Dummy HashTrees
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
	public void rebuildHashTree(long treeId, long fullRebuildPeriod)
			throws IOException {

	}

	@Override
	public void rebuildHashTree(long treeId, boolean fullRebuild)
			throws IOException {

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
