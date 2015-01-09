package org.hashtrees.synch;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.hashtrees.HashTrees;
import org.hashtrees.thrift.generated.HashTreesSyncInterface;
import org.hashtrees.thrift.generated.RebuildHashTreeRequest;
import org.hashtrees.thrift.generated.RebuildHashTreeResponse;
import org.hashtrees.thrift.generated.RemoteTreeInfo;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;

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
	public void submitRebuildRequest(RebuildHashTreeRequest request)
			throws TException {
		try {
			syncCallsObserver.onRebuildHashTreeRequest(request);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public void submitRebuildResponse(RebuildHashTreeResponse response)
			throws TException {
		try {
			syncCallsObserver.onRebuildHashTreeResponse(response);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public void addToSyncList(RemoteTreeInfo rTree) throws TException {
		syncCallsObserver.addToSyncList(rTree);
	}

	@Override
	public void removeFromSyncList(RemoteTreeInfo rTree) throws TException {
		syncCallsObserver.removeFromSyncList(rTree);
	}

	@Override
	public List<RemoteTreeInfo> getSyncList() throws TException {
		return syncCallsObserver.getSyncList();
	}
}
