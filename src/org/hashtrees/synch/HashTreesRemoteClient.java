package org.hashtrees.synch;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.hashtrees.HashTrees;
import org.hashtrees.thrift.generated.HashTreeSyncInterface;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;

/**
 * A {@link HashTrees} implementation that wraps up
 * {@link HashTreeSyncInterface.Iface} client and forwards the calls to the
 * remote tree. Clients who use this class will assume that they talk to a local
 * Java object, but in fact they are talking to a remote Java object.
 * 
 */
public class HashTreesRemoteClient implements HashTrees {

	private final HashTreeSyncInterface.Iface remoteTree;

	public HashTreesRemoteClient(final HashTreeSyncInterface.Iface remoteTree) {
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
	public boolean synch(long treeId, HashTrees remoteTree) throws TException {
		throw new UnsupportedOperationException(
				"Remote tree does not support this operation.");
	}

	@Override
	public void rebuildHashTrees(boolean fullRebuild) {
		throw new UnsupportedOperationException(
				"Remote tree does not support this operation.");
	}

	@Override
	public void rebuildHashTree(long treeId, boolean fullRebuild) {
		throw new UnsupportedOperationException(
				"Remote tree does not support this operation.");
	}

	@Override
	public long getLastFullyRebuiltTimeStamp(long treeId) {
		throw new UnsupportedOperationException(
				"Remote tree does not support this operation.");
	}

	@Override
	public boolean enableNonblockingOperations() {
		throw new UnsupportedOperationException(
				"Remote tree does not support this operation.");
	}

	@Override
	public boolean disableNonblockingOperations() {
		throw new UnsupportedOperationException(
				"Remote tree does not support this operation.");
	}

	@Override
	public void stop() {
		throw new UnsupportedOperationException(
				"Remote tree does not support this operation.");
	}

	@Override
	public boolean isNonBlockingCallsEnabled() {
		throw new UnsupportedOperationException(
				"Remote tree does not support this operation.");
	}

	@Override
	public boolean enableNonblockingOperations(int maxElementsToQue) {
		throw new UnsupportedOperationException(
				"Remote tree does not support this operation.");

	}

}
