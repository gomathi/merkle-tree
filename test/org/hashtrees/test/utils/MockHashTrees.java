package org.hashtrees.test.utils;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hashtrees.HashTrees;
import org.hashtrees.HashTreesListener;
import org.hashtrees.SyncDiffResult;
import org.hashtrees.SyncType;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;

public class MockHashTrees implements HashTrees {

	@Override
	public void sPut(Map<ByteBuffer, ByteBuffer> keyValuePairs)
			throws Exception {

	}

	@Override
	public void sRemove(List<ByteBuffer> keys) throws Exception {

	}

	@Override
	public List<SegmentHash> getSegmentHashes(long treeId, List<Integer> nodeIds)
			throws Exception {
		return Collections.emptyList();
	}

	@Override
	public SegmentHash getSegmentHash(long treeId, int nodeId) throws Exception {
		return new SegmentHash();
	}

	@Override
	public List<SegmentData> getSegment(long treeId, int segId)
			throws Exception {
		return Collections.emptyList();
	}

	@Override
	public SegmentData getSegmentData(long treeId, int segId, ByteBuffer key)
			throws Exception {
		return new SegmentData();
	}

	@Override
	public void deleteTreeNodes(long treeId, List<Integer> nodeIds)
			throws Exception {

	}

	@Override
	public void hPut(ByteBuffer key, ByteBuffer value) throws Exception {

	}

	@Override
	public void hRemove(ByteBuffer key) throws Exception {

	}

	@Override
	public SyncDiffResult synch(long treeId, HashTrees remoteTree,
			SyncType syncType) throws Exception {
		return new SyncDiffResult(0, 0, 0);
	}

	@Override
	public SyncDiffResult synch(long treeId, HashTrees remoteTree)
			throws Exception {
		return new SyncDiffResult(0, 0, 0);
	}

	@Override
	public void rebuildHashTree(long treeId, long fullRebuildPeriod)
			throws Exception {

	}

	@Override
	public void rebuildHashTree(long treeId, boolean fullRebuild)
			throws Exception {

	}

	@Override
	public void addListener(HashTreesListener listener) {

	}

	@Override
	public void removeListener(HashTreesListener listener) {

	}

}
