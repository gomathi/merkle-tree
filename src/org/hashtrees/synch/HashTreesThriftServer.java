package org.hashtrees.synch;

import java.nio.ByteBuffer;
import java.util.List;

import org.apache.thrift.TException;
import org.hashtrees.HashTrees;
import org.hashtrees.thrift.generated.HashTreesSyncInterface;
import org.hashtrees.thrift.generated.KeyValue;
import org.hashtrees.thrift.generated.RebuildHashTreeRequest;
import org.hashtrees.thrift.generated.RebuildHashTreeResponse;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;
import org.hashtrees.thrift.generated.ServerName;

/**
 * Just wraps up {@link HashTrees} and provides a view as
 * {@link HashTreesSyncInterface.Iface}. This is used by Thrift server.
 * 
 */
public class HashTreesThriftServer implements HashTreesSyncInterface.Iface {

	private final HashTrees hashTrees;
	private final HashTreesSyncCallsObserver syncCallsObserver;
	private final HashTreesSynchListProvider syncListProvider;

	public HashTreesThriftServer(final HashTrees hashTree,
			final HashTreesSyncCallsObserver syncCallsObserver,
			final HashTreesSynchListProvider syncListProvider) {
		this.hashTrees = hashTree;
		this.syncCallsObserver = syncCallsObserver;
		this.syncListProvider = syncListProvider;
	}

	@Override
	public void sPut(List<KeyValue> keyValuePairs) throws TException {
		try {
			hashTrees.sPut(keyValuePairs);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public void sRemove(List<ByteBuffer> keys) throws TException {
		try {
			hashTrees.sRemove(keys);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public List<SegmentHash> getSegmentHashes(long treeId, List<Integer> nodeIds)
			throws TException {
		try {
			return hashTrees.getSegmentHashes(treeId, nodeIds);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public SegmentHash getSegmentHash(long treeId, int nodeId)
			throws TException {
		try {
			return hashTrees.getSegmentHash(treeId, nodeId);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public List<SegmentData> getSegment(long treeId, int segId)
			throws TException {
		try {
			return hashTrees.getSegment(treeId, segId);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public SegmentData getSegmentData(long treeId, int segId, ByteBuffer key)
			throws TException {
		try {
			return hashTrees.getSegmentData(treeId, segId, key);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public void deleteTreeNodes(long treeId, List<Integer> nodeIds)
			throws TException {
		try {
			hashTrees.deleteTreeNodes(treeId, nodeIds);
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
	public List<ServerName> getServerNameListFor(long treeId) throws TException {
		return syncListProvider.getServerNameListFor(treeId);
	}
}
