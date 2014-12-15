package org.hashtrees.synch;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.hashtrees.HashTrees;
import org.hashtrees.thrift.generated.HashTreeSyncInterface;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;
import org.hashtrees.thrift.generated.ServerName;

/**
 * Just wraps up {@link HashTrees} and provides a view as
 * {@link HashTreeSyncInterface.Iface}. This is used by Thrift server.
 * 
 */
public class HashTreesThriftServer implements HashTreeSyncInterface.Iface {

	private final HashTrees hashTree;
	private final HashTreesSyncManagerImpl htSyncManager;

	public HashTreesThriftServer(final HashTrees hashTree,
			final HashTreesSyncManagerImpl htSyncManager) {
		this.hashTree = hashTree;
		this.htSyncManager = htSyncManager;
	}

	@Override
	public String ping() throws TException {
		return "ping";
	}

	@Override
	public void sPut(Map<ByteBuffer, ByteBuffer> keyValuePairs)
			throws TException {
		try {
			hashTree.sPut(keyValuePairs);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public void sRemove(List<ByteBuffer> keys) throws TException {
		try {
			hashTree.sRemove(keys);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public List<SegmentHash> getSegmentHashes(long treeId, List<Integer> nodeIds)
			throws TException {
		try {
			return hashTree.getSegmentHashes(treeId, nodeIds);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public SegmentHash getSegmentHash(long treeId, int nodeId)
			throws TException {
		try {
			return hashTree.getSegmentHash(treeId, nodeId);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public List<SegmentData> getSegment(long treeId, int segId)
			throws TException {
		try {
			return hashTree.getSegment(treeId, segId);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public SegmentData getSegmentData(long treeId, int segId, ByteBuffer key)
			throws TException {
		try {
			return hashTree.getSegmentData(treeId, segId, key);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public void deleteTreeNodes(long treeId, List<Integer> nodeIds)
			throws TException {
		try {
			hashTree.deleteTreeNodes(treeId, nodeIds);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public void rebuildHashTree(ServerName sn, long treeId, long tokenNo,
			long expFullRebuildTimeInt) throws TException {
		try {
			htSyncManager.onRebuildHashTreeRequest(sn, treeId, tokenNo,
					expFullRebuildTimeInt);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public void postRebuildHashTreeResponse(ServerName sn, long treeId,
			long tokenNo) throws TException {
		try {
			htSyncManager.onRebuildHashTreeResponse(sn, tokenNo, treeId);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

}
