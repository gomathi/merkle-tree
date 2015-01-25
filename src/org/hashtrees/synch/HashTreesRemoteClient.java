package org.hashtrees.synch;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.thrift.TException;
import org.hashtrees.HashTrees;
import org.hashtrees.HashTreesObserver;
import org.hashtrees.SyncDiffResult;
import org.hashtrees.SyncType;
import org.hashtrees.thrift.generated.HashTreesSyncInterface;
import org.hashtrees.thrift.generated.KeyValue;
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
	public void sPut(List<KeyValue> keyValuePairs) throws IOException {
		try {
			remoteTree.sPut(keyValuePairs);
		} catch (TException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void sRemove(List<ByteBuffer> keys) throws IOException {
		try {
			remoteTree.sRemove(keys);
		} catch (TException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<SegmentHash> getSegmentHashes(long treeId, List<Integer> nodeIds)
			throws IOException {
		try {
			return remoteTree.getSegmentHashes(treeId, nodeIds);
		} catch (TException e) {
			throw new IOException(e);
		}
	}

	@Override
	public SegmentHash getSegmentHash(long treeId, int nodeId)
			throws IOException {
		try {
			return remoteTree.getSegmentHash(treeId, nodeId);
		} catch (TException e) {
			throw new IOException(e);
		}
	}

	@Override
	public List<SegmentData> getSegment(long treeId, int segId)
			throws IOException {
		try {
			return remoteTree.getSegment(treeId, segId);
		} catch (TException e) {
			throw new IOException(e);
		}
	}

	@Override
	public SegmentData getSegmentData(long treeId, int segId, ByteBuffer key)
			throws IOException {
		try {
			return remoteTree.getSegmentData(treeId, segId, key);
		} catch (TException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void deleteTreeNode(long treeId, int nodeId) throws IOException {
		try {
			remoteTree.deleteTreeNode(treeId, nodeId);
		} catch (TException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void hPut(ByteBuffer key, ByteBuffer value) throws IOException {
		throw new IOException("Remote tree does not support this operation.");
	}

	@Override
	public void hRemove(ByteBuffer key) throws IOException {
		throw new IOException("Remote tree does not support this operation.");
	}

	@Override
	public void rebuildHashTree(long treeId, long fullRebuildPeriod)
			throws IOException {
		throw new IOException("Remote tree does not support this operation.");
	}

	@Override
	public void rebuildHashTree(long treeId, boolean fullRebuild)
			throws IOException {
		throw new IOException("Remote tree does not support this operation.");
	}

	@Override
	public SyncDiffResult synch(long treeId, HashTrees remoteTree,
			SyncType syncType) throws IOException {
		throw new IOException("Remote tree does not support this operation.");
	}

	@Override
	public SyncDiffResult synch(long treeId, HashTrees remoteTree)
			throws IOException {
		throw new IOException("Remote tree does not support this operation.");
	}

	@Override
	public void addObserver(HashTreesObserver observer) {
		throw new RuntimeException(
				"Remote tree does not support this operation.");
	}

	@Override
	public void removeObserver(HashTreesObserver observer) {
		throw new RuntimeException(
				"Remote tree does not support this operation.");
	}
}
