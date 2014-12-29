package org.hashtrees.test;

import static org.hashtrees.test.HashTreesImplTestUtils.DEFAULT_SEG_DATA_BLOCKS_COUNT;
import static org.hashtrees.test.HashTreesImplTestUtils.generateInMemoryStore;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.hashtrees.HashTreesConstants;
import org.hashtrees.HashTreesIdProvider;
import org.hashtrees.store.HashTreesMemStore;
import org.hashtrees.store.HashTreesStore;
import org.hashtrees.store.Store;
import org.hashtrees.synch.HashTreeSyncManagerStore;
import org.hashtrees.synch.HashTreesSyncManagerImpl;
import org.hashtrees.test.HashTreesImplTestUtils.HashTreeIdProviderTest;
import org.hashtrees.test.HashTreesImplTestUtils.StorageImplTest;
import org.hashtrees.thrift.generated.ServerName;
import org.junit.Test;

public class HashTreesSyncManagerImplTest {

	private static final HashTreesIdProvider TREE_ID_PROVIDER = new HashTreeIdProviderTest();

	private static void waitForTheEvent(
			BlockingQueue<HashTreesImplTestEvent> events,
			HashTreesImplTestEvent expectedEvent, long maxWaitTime)
			throws InterruptedException {
		HashTreesImplTestEvent event = null;
		long cntr = System.currentTimeMillis();
		while (true) {
			event = events.poll(1000, TimeUnit.MILLISECONDS);
			if (event == expectedEvent)
				break;
			else if (event == null) {
				long diff = System.currentTimeMillis() - cntr;
				if (diff > maxWaitTime)
					break;
			}
		}
		Assert.assertNotNull(event);
		Assert.assertEquals(event, expectedEvent);
	}

	@Test
	public void testSegmentUpdate() throws InterruptedException {
		HashTreesMemStore inMemoryStore = generateInMemoryStore();
		HashTreesStore htStorage = inMemoryStore;
		HashTreeSyncManagerStore syncMgrStore = inMemoryStore;
		htStorage.setLastFullyTreeBuiltTimestamp(1, System.currentTimeMillis());
		BlockingQueue<HashTreesImplTestEvent> events = new ArrayBlockingQueue<HashTreesImplTestEvent>(
				1000);
		Store storage = new StorageImplTest();
		HashTreesImplTestObj hTree = new HashTreesImplTestObj(
				DEFAULT_SEG_DATA_BLOCKS_COUNT, htStorage, storage, events);
		HashTreesSyncManagerImpl syncManager = new HashTreesSyncManagerImpl(
				hTree, TREE_ID_PROVIDER, syncMgrStore, "localhost",
				HashTreesConstants.DEFAULT_HASH_TREE_SERVER_PORT_NO, 30 * 1000,
				3000000, 10);
		syncManager.init();
		waitForTheEvent(events, HashTreesImplTestEvent.UPDATE_SEGMENT, 30000);
		syncManager.shutdown();
	}

	@Test
	public void testFullTreeUpdate() throws InterruptedException {
		HashTreesMemStore inMemoryStore = generateInMemoryStore();
		HashTreesStore htStorage = inMemoryStore;
		HashTreeSyncManagerStore syncMgrStore = inMemoryStore;
		BlockingQueue<HashTreesImplTestEvent> events = new ArrayBlockingQueue<HashTreesImplTestEvent>(
				1000);
		Store storage = new StorageImplTest();
		HashTreesImplTestObj hTree = new HashTreesImplTestObj(
				DEFAULT_SEG_DATA_BLOCKS_COUNT, htStorage, storage, events);
		HashTreesSyncManagerImpl syncManager = new HashTreesSyncManagerImpl(
				hTree, TREE_ID_PROVIDER, syncMgrStore, "localhost",
				HashTreesConstants.DEFAULT_HASH_TREE_SERVER_PORT_NO, 30 * 1000,
				30000000, 10);
		syncManager.init();
		waitForTheEvent(events, HashTreesImplTestEvent.UPDATE_FULL_TREE, 10000);
		syncManager.shutdown();
	}

	// @Test
	public void testSynch() throws Exception {
		HashTreesMemStore localInMemStore = generateInMemoryStore();
		HashTreesStore localHTStorage = localInMemStore;
		HashTreeSyncManagerStore localSyncMgrStore = localInMemStore;

		HashTreesMemStore remoteInMemStore = generateInMemoryStore();
		HashTreeSyncManagerStore remoteSyncMgrStore = remoteInMemStore;
		HashTreesStore remoteHTStorage = remoteInMemStore;

		BlockingQueue<HashTreesImplTestEvent> localEvents = new ArrayBlockingQueue<HashTreesImplTestEvent>(
				1000);
		BlockingQueue<HashTreesImplTestEvent> remoteEvents = new ArrayBlockingQueue<HashTreesImplTestEvent>(
				1000);
		StorageImplTest localStorage = new StorageImplTest();
		HashTreesImplTestObj localHTree = new HashTreesImplTestObj(
				DEFAULT_SEG_DATA_BLOCKS_COUNT, localHTStorage, localStorage,
				localEvents);
		localStorage.setHashTree(localHTree);
		StorageImplTest remoteStorage = new StorageImplTest();
		HashTreesImplTestObj remoteHTree = new HashTreesImplTestObj(
				DEFAULT_SEG_DATA_BLOCKS_COUNT, remoteHTStorage, remoteStorage,
				remoteEvents);
		remoteStorage.setHashTree(remoteHTree);
		localStorage.put(HashTreesImplTestUtils.randomByteBuffer(),
				HashTreesImplTestUtils.randomByteBuffer());
		HashTreesSyncManagerImpl localSyncManager = new HashTreesSyncManagerImpl(
				localHTree, TREE_ID_PROVIDER, localSyncMgrStore, "localhost",
				HashTreesConstants.DEFAULT_HASH_TREE_SERVER_PORT_NO, 3000, 300,
				10);

		HashTreesSyncManagerImpl remoteSyncManager = new HashTreesSyncManagerImpl(
				remoteHTree, TREE_ID_PROVIDER, remoteSyncMgrStore, "localhost",
				8999, 3000, 300, 10);

		remoteSyncManager.init();
		localSyncManager.addServerToSyncList(new ServerName("localhost", 8999));
		localSyncManager.init();

		waitForTheEvent(localEvents, HashTreesImplTestEvent.SYNCH,
				10000000000000L);
		waitForTheEvent(remoteEvents, HashTreesImplTestEvent.SYNCH_INITIATED,
				10000);
		localSyncManager.shutdown();
		remoteSyncManager.shutdown();
	}
}
