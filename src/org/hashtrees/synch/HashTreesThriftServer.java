package org.hashtrees.synch;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.hashtrees.HashTrees;
import org.hashtrees.thrift.generated.HashTreesSyncInterface;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;
import org.hashtrees.thrift.generated.ServerName;

/**
 * Just wraps up {@link HashTrees} and provides a view as
 * {@link HashTreesSyncInterface.Iface}. This is used by Thrift server.
 * 
 */
public class HashTreesThriftServer implements HashTreesSyncInterface.Iface {

	private final HashTrees hashTree;
	private final HashTreesSyncCallsObserver syncCallsObserver;

	public HashTreesThriftServer(final HashTrees hashTree,
			final HashTreesSyncCallsObserver syncCallsObserver) {
		this.hashTree = hashTree;
		this.syncCallsObserver = syncCallsObserver;
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
			syncCallsObserver.onRebuildHashTreeRequest(sn, treeId, tokenNo,
					expFullRebuildTimeInt);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public void postRebuildHashTreeResponse(ServerName sn, long treeId,
			long tokenNo) throws TException {
		try {
			syncCallsObserver.onRebuildHashTreeResponse(sn, treeId, tokenNo);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public void addServerToSyncList(ServerName sn) throws TException {
		syncCallsObserver.addServerToSyncList(sn);
	}

	@Override
	public void removeServerFromSyncList(ServerName sn) throws TException {
		syncCallsObserver.removeServerFromSyncList(sn);
	}

}
