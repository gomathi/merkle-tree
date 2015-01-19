package org.hashtrees.synch;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.hashtrees.HashTrees;
import org.hashtrees.SyncType;
import org.hashtrees.SyncDiffResult;
import org.hashtrees.thrift.generated.HashTreesSyncInterface;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;

/**
 * A {@link HashTrees} implementation that wraps up
 * {@link HashTreesSyncInterface.Iface} client and forwards the calls to the
 * remote tree.
 * 
 */
public class HashTreesRemoteClient implements HashTrees {

	private final HashTreesSyncInterface.Iface remoteTree;

	public HashTreesRemoteClient(final HashTreesSyncInterface.Iface remoteTree) {
		this.remoteTree = remoteTree;
	}

	@Override
	public void sPut(Map<ByteBuffer, ByteBuffer> keyValuePairs)
			throws TException {
		remoteTree.sPut(keyValuePairs);
	}

	@Override
	public void sRemove(List<ByteBuffer> keys) throws TException {
		remoteTree.sRemove(keys);
	}

	@Override
	public List<SegmentHash> getSegmentHashes(long treeId, List<Integer> nodeIds)
			throws TException {
		return remoteTree.getSegmentHashes(treeId, nodeIds);
	}

	@Override
	public SegmentHash getSegmentHash(long treeId, int nodeId)
			throws TException {
		return remoteTree.getSegmentHash(treeId, nodeId);
	}

	@Override
	public List<SegmentData> getSegment(long treeId, int segId)
			throws TException {
		return remoteTree.getSegment(treeId, segId);
	}

	@Override
	public SegmentData getSegmentData(long treeId, int segId, ByteBuffer key)
			throws TException {
		return remoteTree.getSegmentData(treeId, segId, key);
	}

	@Override
	public void deleteTreeNodes(long treeId, List<Integer> nodeIds)
			throws TException {
		remoteTree.deleteTreeNodes(treeId, nodeIds);
	}

	@Override
	public void hPut(ByteBuffer key, ByteBuffer value) {
		throw new UnsupportedOperationException(
				"Remote tree does not support this operation.");
	}

	@Override
	public void hRemove(ByteBuffer key) {
		throw new UnsupportedOperationException(
				"Remote tree does not support this operation.");
	}

	@Override
	public void rebuildHashTree(long treeId, long fullRebuildPeriod)
			throws Exception {
		throw new UnsupportedOperationException(
				"Remote tree does not support this operation.");
	}

	@Override
	public void rebuildHashTree(long treeId, boolean fullRebuild)
			throws Exception {
		throw new UnsupportedOperationException(
				"Remote tree does not support this operation.");
	}

	@Override
	public void rebuildAllTrees(boolean fullRebuild) throws Exception {
		throw new UnsupportedOperationException(
				"Remote tree does not support this operation.");
	}

	@Override
	public void rebuildAllTrees(long fullRebuildPeriod) throws Exception {
		throw new UnsupportedOperationException(
				"Remote tree does not support this operation.");
	}

	@Override
	public SyncDiffResult synch(long treeId, HashTrees remoteTree,
			SyncType syncType) throws Exception {
		throw new UnsupportedOperationException(
				"Remote tree does not support this operation.");
	}

	@Override
	public SyncDiffResult synch(long treeId, HashTrees remoteTree)
			throws Exception {
		throw new UnsupportedOperationException(
				"Remote tree does not support this operation.");
	}
}
