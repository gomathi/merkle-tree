package org.hashtrees.test.utils;

import static org.hashtrees.test.utils.HashTreesImplTestUtils.TREE_ID_PROVIDER;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.hashtrees.HashTrees;
import org.hashtrees.HashTreesImpl;
import org.hashtrees.ModuloSegIdProvider;
import org.hashtrees.SyncDiffResult;
import org.hashtrees.SyncType;
import org.hashtrees.store.HashTreesStore;
import org.hashtrees.store.Store;

public class HashTreesImplTestObj extends HashTreesImpl {

	private final BlockingQueue<HTSynchEvent> events;

	public static enum HTSynchEvent {
		UPDATE_SEGMENT, UPDATE_FULL_TREE, SYNCH, SYNCH_INITIATED
	}

	public HashTreesImplTestObj(final int noOfSegments,
			final HashTreesStore htStore, final Store store,
			BlockingQueue<HTSynchEvent> events) {
		super(noOfSegments, TREE_ID_PROVIDER, new ModuloSegIdProvider(
				noOfSegments), htStore, store);
		this.events = events;
	}

	@Override
	public void sPut(Map<ByteBuffer, ByteBuffer> keyValuePairs)
			throws Exception {
		super.sPut(keyValuePairs);
		events.put(HTSynchEvent.SYNCH_INITIATED);
	}

	@Override
	public void sRemove(List<ByteBuffer> keys) throws Exception {
		super.sRemove(keys);
		events.put(HTSynchEvent.SYNCH_INITIATED);
	}

	@Override
	public SyncDiffResult synch(long treeId, HashTrees remoteTree)
			throws Exception {
		SyncDiffResult result = super.synch(treeId, remoteTree);
		events.add(HTSynchEvent.SYNCH);
		return result;
	}

	@Override
	public SyncDiffResult synch(long treeId, HashTrees remoteTree,
			SyncType syncType) throws Exception {
		SyncDiffResult result = super.synch(treeId, remoteTree, syncType);
		events.add(HTSynchEvent.SYNCH);
		return result;
	}

	@Override
	public void rebuildHashTree(long treeId, boolean fullRebuild) {
		super.rebuildHashTree(treeId, fullRebuild);
		if (!fullRebuild)
			events.add(HTSynchEvent.UPDATE_SEGMENT);
		else
			events.add(HTSynchEvent.UPDATE_FULL_TREE);
	}

}