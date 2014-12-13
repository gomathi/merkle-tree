package org.hashtrees.test;

import static org.hashtrees.test.HashTreeImplTestUtils.DEFAULT_SEG_DATA_BLOCKS_COUNT;
import static org.hashtrees.test.HashTreeImplTestUtils.generateInMemoryStore;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.hashtrees.HashTreeConstants;
import org.hashtrees.HashTreeIdProvider;
import org.hashtrees.storage.HashTreeStorage;
import org.hashtrees.storage.Storage;
import org.hashtrees.synch.HashTreeSyncManagerImpl;
import org.hashtrees.test.HashTreeImplTestUtils.HashTreeIdProviderTest;
import org.hashtrees.test.HashTreeImplTestUtils.StorageImplTest;
import org.hashtrees.thrift.generated.ServerName;
import org.junit.Test;

public class HashTreeSyncManagerImplTest {

	private static final HashTreeIdProvider treeIdProvider = new HashTreeIdProviderTest();

	private static void waitForTheEvent(
			BlockingQueue<HashTreeImplTestEvent> events,
			HashTreeImplTestEvent expectedEvent, long maxWaitTime)
			throws InterruptedException {
		HashTreeImplTestEvent event = null;
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
		HashTreeStorage htStorage = generateInMemoryStore(DEFAULT_SEG_DATA_BLOCKS_COUNT);
		htStorage.setLastFullyTreeBuiltTimestamp(1, System.currentTimeMillis());
		BlockingQueue<HashTreeImplTestEvent> events = new ArrayBlockingQueue<HashTreeImplTestEvent>(
				1000);
		Storage storage = new StorageImplTest();
		HashTreeImplTestObj hTree = new HashTreeImplTestObj(
				DEFAULT_SEG_DATA_BLOCKS_COUNT, htStorage, storage, events);
		HashTreeSyncManagerImpl syncManager = new HashTreeSyncManagerImpl(
				hTree, treeIdProvider, "localhost",
				HashTreeConstants.DEFAULT_HASH_TREE_SERVER_PORT_NO, 30 * 1000,
				3000000, 10);
		syncManager.init();
		waitForTheEvent(events, HashTreeImplTestEvent.UPDATE_SEGMENT, 30000);
		syncManager.shutdown();
	}

	@Test
	public void testFullTreeUpdate() throws InterruptedException {
		HashTreeStorage htStorage = generateInMemoryStore(DEFAULT_SEG_DATA_BLOCKS_COUNT);
		BlockingQueue<HashTreeImplTestEvent> events = new ArrayBlockingQueue<HashTreeImplTestEvent>(
				1000);
		Storage storage = new StorageImplTest();
		HashTreeImplTestObj hTree = new HashTreeImplTestObj(
				DEFAULT_SEG_DATA_BLOCKS_COUNT, htStorage, storage, events);
		HashTreeSyncManagerImpl syncManager = new HashTreeSyncManagerImpl(
				hTree, treeIdProvider, "localhost",
				HashTreeConstants.DEFAULT_HASH_TREE_SERVER_PORT_NO, 30 * 1000,
				30000000, 10);
		syncManager.init();
		waitForTheEvent(events, HashTreeImplTestEvent.UPDATE_FULL_TREE, 10000);
		syncManager.shutdown();
	}

	// @Test
	public void testSynch() throws Exception {
		HashTreeStorage localHTStorage = generateInMemoryStore(DEFAULT_SEG_DATA_BLOCKS_COUNT);
		HashTreeStorage remoteHTStorage = generateInMemoryStore(DEFAULT_SEG_DATA_BLOCKS_COUNT);
		BlockingQueue<HashTreeImplTestEvent> localEvents = new ArrayBlockingQueue<HashTreeImplTestEvent>(
				1000);
		BlockingQueue<HashTreeImplTestEvent> remoteEvents = new ArrayBlockingQueue<HashTreeImplTestEvent>(
				1000);
		StorageImplTest localStorage = new StorageImplTest();
		HashTreeImplTestObj localHTree = new HashTreeImplTestObj(
				DEFAULT_SEG_DATA_BLOCKS_COUNT, localHTStorage, localStorage,
				localEvents);
		localStorage.setHashTree(localHTree);
		StorageImplTest remoteStorage = new StorageImplTest();
		HashTreeImplTestObj remoteHTree = new HashTreeImplTestObj(
				DEFAULT_SEG_DATA_BLOCKS_COUNT, remoteHTStorage, remoteStorage,
				remoteEvents);
		remoteStorage.setHashTree(remoteHTree);
		localStorage.put(HashTreeImplTestUtils.randomByteBuffer(),
				HashTreeImplTestUtils.randomByteBuffer());
		HashTreeSyncManagerImpl localSyncManager = new HashTreeSyncManagerImpl(
				localHTree, treeIdProvider, "localhost",
				HashTreeConstants.DEFAULT_HASH_TREE_SERVER_PORT_NO, 3000, 300,
				10);

		HashTreeSyncManagerImpl remoteSyncManager = new HashTreeSyncManagerImpl(
				remoteHTree, treeIdProvider, "localhost", 8999, 3000, 300, 10);

		remoteSyncManager.init();
		localSyncManager.addServerToSyncList(new ServerName("localhost", 8999));
		localSyncManager.init();

		waitForTheEvent(localEvents, HashTreeImplTestEvent.SYNCH,
				10000000000000L);
		waitForTheEvent(remoteEvents, HashTreeImplTestEvent.SYNCH_INITIATED,
				10000);
		localSyncManager.shutdown();
		remoteSyncManager.shutdown();
	}
}
