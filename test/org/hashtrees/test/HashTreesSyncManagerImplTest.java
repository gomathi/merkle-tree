package org.hashtrees.test;

import static org.hashtrees.test.HashTreesImplTestUtils.DEFAULT_SEG_DATA_BLOCKS_COUNT;
import static org.hashtrees.test.HashTreesImplTestUtils.TREE_ID_PROVIDER;
import static org.hashtrees.test.HashTreesImplTestUtils.generateInMemoryStore;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.hashtrees.HashTreesConstants;
import org.hashtrees.store.HashTreeSyncManagerStore;
import org.hashtrees.store.HashTreesMemStore;
import org.hashtrees.store.HashTreesStore;
import org.hashtrees.synch.HashTreesSyncManagerImpl;
import org.hashtrees.test.HashTreesImplTestObj.HTSynchEvent;
import org.hashtrees.test.HashTreesImplTestUtils.StoreImplTest;
import org.hashtrees.thrift.generated.ServerName;
import org.junit.Test;

public class HashTreesSyncManagerImplTest {

	private static void waitForTheEvent(BlockingQueue<HTSynchEvent> events,
			HTSynchEvent expectedEvent, long maxWaitTime)
			throws InterruptedException {
		HTSynchEvent event = null;
		long startTime = System.currentTimeMillis();
		while (true) {
			event = events.poll(1000, TimeUnit.MILLISECONDS);
			if (event == expectedEvent)
				break;
			else if (event == null) {
				long diff = System.currentTimeMillis() - startTime;
				if (diff > maxWaitTime)
					break;
			}
		}
		Assert.assertNotNull(event);
		Assert.assertEquals(event, expectedEvent);
	}

	private static class HashTreeSyncManagerComponents {
		volatile HashTreesStore htStore;
		volatile HashTreesSyncManagerImpl syncMgrImpl;
		volatile StoreImplTest storeImplTest;
	}

	private static HashTreeSyncManagerComponents createHashTreeSyncManager(
			BlockingQueue<HTSynchEvent> events, int portNo,
			long fullRebuildTimeInterval, long intBWSynchAndRebuild,
			int noOfBGThreads) {
		HashTreesMemStore inMemoryStore = generateInMemoryStore();
		HashTreesStore htStore = inMemoryStore;
		HashTreeSyncManagerStore syncMgrStore = inMemoryStore;

		StoreImplTest store = new StoreImplTest();

		HashTreesImplTestObj hTree = new HashTreesImplTestObj(
				DEFAULT_SEG_DATA_BLOCKS_COUNT, htStore, store, events);
		HashTreesSyncManagerImpl syncManager = new HashTreesSyncManagerImpl(
				hTree, TREE_ID_PROVIDER, syncMgrStore, "localhost", portNo,
				fullRebuildTimeInterval, intBWSynchAndRebuild, noOfBGThreads);
		store.setHashTree(hTree);

		HashTreeSyncManagerComponents components = new HashTreeSyncManagerComponents();
		components.htStore = htStore;
		components.syncMgrImpl = syncManager;
		components.storeImplTest = store;

		return components;
	}

	@Test
	public void testSegmentUpdate() throws InterruptedException {
		BlockingQueue<HTSynchEvent> events = new ArrayBlockingQueue<HTSynchEvent>(
				1000);
		HashTreeSyncManagerComponents components = createHashTreeSyncManager(
				events, HashTreesConstants.DEFAULT_HASH_TREE_SERVER_PORT_NO,
				30 * 1000, 3000000, 10);
		HashTreesSyncManagerImpl syncManager = components.syncMgrImpl;
		HashTreesStore hashTreesStore = components.htStore;

		hashTreesStore.setLastFullyTreeBuiltTimestamp(1,
				System.currentTimeMillis());
		syncManager.init();
		waitForTheEvent(events, HTSynchEvent.UPDATE_SEGMENT, 30000);
		syncManager.shutdown();
	}

	@Test
	public void testFullTreeUpdate() throws InterruptedException {
		BlockingQueue<HTSynchEvent> events = new ArrayBlockingQueue<HTSynchEvent>(
				1000);
		HashTreeSyncManagerComponents components = createHashTreeSyncManager(
				events, HashTreesConstants.DEFAULT_HASH_TREE_SERVER_PORT_NO,
				30 * 1000, 3000000, 10);
		HashTreesSyncManagerImpl syncManager = components.syncMgrImpl;

		syncManager.init();
		waitForTheEvent(events, HTSynchEvent.UPDATE_FULL_TREE, 10000);
		syncManager.shutdown();
	}

	@Test
	public void testSynch() throws Exception {
		BlockingQueue<HTSynchEvent> localEvents = new ArrayBlockingQueue<HTSynchEvent>(
				10000);
		HashTreeSyncManagerComponents componentsLocal = createHashTreeSyncManager(
				localEvents,
				HashTreesConstants.DEFAULT_HASH_TREE_SERVER_PORT_NO, 3000, 300,
				10);
		HashTreesSyncManagerImpl localSyncManager = componentsLocal.syncMgrImpl;
		componentsLocal.storeImplTest.put(
				HashTreesImplTestUtils.randomByteBuffer(),
				HashTreesImplTestUtils.randomByteBuffer());

		BlockingQueue<HTSynchEvent> remoteEvents = new ArrayBlockingQueue<HTSynchEvent>(
				10000);
		HashTreeSyncManagerComponents componentsRemote = createHashTreeSyncManager(
				remoteEvents, 8999, 3000, 300, 10);
		HashTreesSyncManagerImpl remoteSyncManager = componentsRemote.syncMgrImpl;

		remoteSyncManager.init();
		localSyncManager.addServerToSyncList(new ServerName("localhost", 8999));
		localSyncManager.init();

		waitForTheEvent(localEvents, HTSynchEvent.SYNCH, 10000);
		waitForTheEvent(remoteEvents, HTSynchEvent.SYNCH_INITIATED, 10000);
		localSyncManager.shutdown();
		remoteSyncManager.shutdown();
	}
}
