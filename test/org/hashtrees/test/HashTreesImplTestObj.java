package org.hashtrees.test;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.hashtrees.DefaultSegIdProviderImpl;
import org.hashtrees.HashTrees;
import org.hashtrees.HashTreesIdProvider;
import org.hashtrees.HashTreesImpl;
import org.hashtrees.storage.HashTreesStorage;
import org.hashtrees.storage.Storage;
import org.hashtrees.test.HashTreesImplTestUtils.HashTreeIdProviderTest;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;

public class HashTreesImplTestObj extends HashTreesImpl {

	private static final HashTreesIdProvider treeIdProvider = new HashTreeIdProviderTest();
	private final BlockingQueue<HashTreesImplTestEvent> events;

	public HashTreesImplTestObj(final int noOfSegments,
			final HashTreesStorage htStorage, final Storage storage,
			BlockingQueue<HashTreesImplTestEvent> events) {
		super(noOfSegments, treeIdProvider, new DefaultSegIdProviderImpl(
				noOfSegments), htStorage, storage);
		this.events = events;
	}

	@Override
	public void sPut(Map<ByteBuffer, ByteBuffer> keyValuePairs)
			throws Exception {
		super.sPut(keyValuePairs);
		events.put(HashTreesImplTestEvent.SYNCH_INITIATED);
	}

	@Override
	public void sRemove(List<ByteBuffer> keys) throws Exception {
		super.sRemove(keys);
		events.put(HashTreesImplTestEvent.SYNCH_INITIATED);
	}

	@Override
	public List<SegmentHash> getSegmentHashes(long treeId, List<Integer> nodeIds) {
		return super.getSegmentHashes(treeId, nodeIds);
	}

	@Override
	public SegmentHash getSegmentHash(long treeId, int nodeId) {
		return super.getSegmentHash(treeId, nodeId);
	}

	@Override
	public List<SegmentData> getSegment(long treeId, int segId) {
		return super.getSegment(treeId, segId);
	}

	@Override
	public SegmentData getSegmentData(long treeId, int segId, ByteBuffer key) {
		return super.getSegmentData(treeId, segId, key);
	}

	@Override
	public void deleteTreeNodes(long treeId, List<Integer> nodeIds)
			throws Exception {
		super.deleteTreeNodes(treeId, nodeIds);
	}

	@Override
	public void hPut(ByteBuffer key, ByteBuffer value) {
		super.hPut(key, value);
	}

	@Override
	public void hRemove(ByteBuffer key) {
		super.hRemove(key);
	}

	@Override
	public boolean synch(long treeId, HashTrees remoteTree) throws Exception {
		boolean result = super.synch(treeId, remoteTree);
		events.add(HashTreesImplTestEvent.SYNCH);
		return result;
	}

	@Override
	public void rebuildHashTrees(boolean fullRebuild) {
		super.rebuildHashTrees(fullRebuild);
	}

	@Override
	public void rebuildHashTree(long treeId, boolean fullRebuild) {
		super.rebuildHashTree(treeId, fullRebuild);
		if (!fullRebuild)
			events.add(HashTreesImplTestEvent.UPDATE_SEGMENT);
		else
			events.add(HashTreesImplTestEvent.UPDATE_FULL_TREE);
	}

}