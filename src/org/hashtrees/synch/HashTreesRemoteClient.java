package org.hashtrees.synch;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.hashtrees.HashTrees;
import org.hashtrees.HashTreesObserver;
import org.hashtrees.SyncDiffResult;
import org.hashtrees.SyncType;
import org.hashtrees.thrift.generated.HashTreesSyncInterface;
import org.hashtrees.thrift.generated.KeyValue;
import org.hashtrees.thrift.generated.RebuildHashTreeRequest;
import org.hashtrees.thrift.generated.RebuildHashTreeResponse;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;
import org.hashtrees.thrift.generated.ServerName;

/**
 * A {@link HashTrees} implementation that wraps up
 * {@link HashTreesSyncInterface.Iface} client and forwards the calls to the
 * remote tree.
 * 
 */
public class HashTreesRemoteClient implements HashTrees {

	private final HTSyncClientPool clientPool;

	public HashTreesRemoteClient(final ServerName sn) {
		this.clientPool = HTSyncClientPool.getThriftClientPool(sn);
	}

	@Override
	public void sPut(List<KeyValue> keyValuePairs) throws IOException {
		HashTreesSyncInterface.Client remoteTree = null;
		try {
			remoteTree = clientPool.borrowObject();
			remoteTree.sPut(keyValuePairs);
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			clientPool.returnObject(remoteTree);
		}
	}

	@Override
	public void sRemove(List<ByteBuffer> keys) throws IOException {
		HashTreesSyncInterface.Client remoteTree = null;
		try {
			remoteTree = clientPool.borrowObject();
			remoteTree.sRemove(keys);
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			clientPool.returnObject(remoteTree);
		}
	}

	@Override
	public List<SegmentHash> getSegmentHashes(long treeId, List<Integer> nodeIds)
			throws IOException {
		HashTreesSyncInterface.Client remoteTree = null;
		try {
			remoteTree = clientPool.borrowObject();
			return remoteTree.getSegmentHashes(treeId, nodeIds);
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			clientPool.returnObject(remoteTree);
		}
	}

	@Override
	public SegmentHash getSegmentHash(long treeId, int nodeId)
			throws IOException {
		HashTreesSyncInterface.Client remoteTree = null;
		try {
			remoteTree = clientPool.borrowObject();
			return remoteTree.getSegmentHash(treeId, nodeId);
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			clientPool.returnObject(remoteTree);
		}
	}

	@Override
	public List<SegmentData> getSegment(long treeId, int segId)
			throws IOException {
		HashTreesSyncInterface.Client remoteTree = null;
		try {
			remoteTree = clientPool.borrowObject();
			return remoteTree.getSegment(treeId, segId);
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			clientPool.returnObject(remoteTree);
		}
	}

	@Override
	public SegmentData getSegmentData(long treeId, int segId, ByteBuffer key)
			throws IOException {
		HashTreesSyncInterface.Client remoteTree = null;
		try {
			remoteTree = clientPool.borrowObject();
			return remoteTree.getSegmentData(treeId, segId, key);
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			clientPool.returnObject(remoteTree);
		}
	}

	@Override
	public void deleteTreeNode(long treeId, int nodeId) throws IOException {
		HashTreesSyncInterface.Client remoteTree = null;
		try {
			remoteTree = clientPool.borrowObject();
			remoteTree.deleteTreeNode(treeId, nodeId);
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			clientPool.returnObject(remoteTree);
		}
	}

	public void submitRebuildResponse(RebuildHashTreeResponse response)
			throws IOException {
		HashTreesSyncInterface.Client remoteTree = null;
		try {
			remoteTree = clientPool.borrowObject();
			remoteTree.submitRebuildResponse(response);
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			clientPool.returnObject(remoteTree);
		}
	}

	public void submitRebuildRequest(RebuildHashTreeRequest request)
			throws IOException {
		HashTreesSyncInterface.Client remoteTree = null;
		try {
			remoteTree = clientPool.borrowObject();
			remoteTree.submitRebuildRequest(request);
		} catch (Exception e) {
			throw new IOException(e);
		} finally {
			clientPool.returnObject(remoteTree);
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
	public int rebuildHashTree(long treeId, long fullRebuildPeriod)
			throws IOException {
		throw new IOException("Remote tree does not support this operation.");
	}

	@Override
	public int rebuildHashTree(long treeId, boolean fullRebuild)
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
	public void addObserver(HashTreesObserver observer) throws IOException {
		throw new IOException("Remote tree does not support this operation.");
	}

	@Override
	public void removeObserver(HashTreesObserver observer) throws IOException {
		throw new IOException("Remote tree does not support this operation.");
	}
}
