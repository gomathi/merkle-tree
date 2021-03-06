/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.hashtrees.test;

import static org.hashtrees.test.utils.HashTreesImplTestUtils.DEFAULT_SEG_DATA_BLOCKS_COUNT;
import static org.hashtrees.test.utils.HashTreesImplTestUtils.TREE_ID_PROVIDER;
import static org.hashtrees.test.utils.HashTreesImplTestUtils.generateInMemoryStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;

import junit.framework.Assert;

import org.hashtrees.SyncDiffResult;
import org.hashtrees.SyncType;
import org.hashtrees.manager.EmptySyncListProvider;
import org.hashtrees.manager.HashTreesManager;
import org.hashtrees.manager.HashTreesManagerObserver;
import org.hashtrees.manager.HashTreesSynchAuthenticator;
import org.hashtrees.manager.HashTreesSynchListProvider;
import org.hashtrees.manager.SynchNotAllowedException;
import org.hashtrees.store.HashTreesMemStore;
import org.hashtrees.store.HashTreesStore;
import org.hashtrees.store.SimpleMemStore;
import org.hashtrees.test.utils.HashTreesConstants;
import org.hashtrees.test.utils.HashTreesImplTestObj;
import org.hashtrees.test.utils.HashTreesImplTestObj.HTSynchEvent;
import org.hashtrees.test.utils.HashTreesImplTestUtils;
import org.hashtrees.test.utils.MockHashTrees;
import org.hashtrees.thrift.generated.ServerName;
import org.junit.Test;

public class HashTreesManagerTest {

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
		volatile HashTreesManager htMgr;
		volatile SimpleMemStore simpleMemStore;
	}

	private static HashTreeSyncManagerComponents createHashTreeSyncManager(
			HashTreesSynchListProvider htSyncListProvider,
			BlockingQueue<HTSynchEvent> events, int portNo,
			long fullRebuildTimeInterval, long schedPeriod) {
		HashTreesMemStore inMemoryStore = generateInMemoryStore();
		HashTreesStore htStore = inMemoryStore;

		SimpleMemStore store = new SimpleMemStore();

		HashTreesImplTestObj hTree = new HashTreesImplTestObj(
				DEFAULT_SEG_DATA_BLOCKS_COUNT, false, 10, htStore, store,
				events);
		HashTreesManager syncManager = new HashTreesManager.Builder(
				"localhost", portNo, hTree, TREE_ID_PROVIDER,
				htSyncListProvider)
				.setFullRebuildPeriod(fullRebuildTimeInterval)
				.schedule(schedPeriod).build();
		store.registerHashTrees(hTree);

		HashTreeSyncManagerComponents components = new HashTreeSyncManagerComponents();
		components.htStore = htStore;
		components.htMgr = syncManager;
		components.simpleMemStore = store;

		return components;
	}

	@Test
	public void testSegmentUpdate() throws InterruptedException, IOException {
		BlockingQueue<HTSynchEvent> events = new ArrayBlockingQueue<HTSynchEvent>(
				1000);
		HashTreeSyncManagerComponents components = createHashTreeSyncManager(
				new EmptySyncListProvider(), events,
				HashTreesConstants.DEFAULT_HASH_TREE_SERVER_PORT_NO, 30 * 1000,
				3000000);
		HashTreesManager syncManager = components.htMgr;
		HashTreesStore hashTreesStore = components.htStore;

		try {
			hashTreesStore.setCompleteRebuiltTimestamp(1,
					System.currentTimeMillis());
			syncManager.start();
			waitForTheEvent(events, HTSynchEvent.UPDATE_SEGMENT, 10000);
		} finally {
			syncManager.stop();
		}
	}

	@Test
	public void testFullTreeUpdate() throws InterruptedException {
		BlockingQueue<HTSynchEvent> events = new ArrayBlockingQueue<HTSynchEvent>(
				1000);
		HashTreeSyncManagerComponents components = createHashTreeSyncManager(
				new EmptySyncListProvider(), events,
				HashTreesConstants.DEFAULT_HASH_TREE_SERVER_PORT_NO, 30 * 1000,
				3000000);
		HashTreesManager syncManager = components.htMgr;

		try {
			syncManager.start();
			waitForTheEvent(events, HTSynchEvent.UPDATE_FULL_TREE, 10000);
		} finally {
			syncManager.stop();
		}
	}

	@Test
	public void testSynch() throws IOException, InterruptedException {
		final List<ServerName> syncList = Collections
				.synchronizedList(new ArrayList<ServerName>());
		BlockingQueue<HTSynchEvent> localEvents = new ArrayBlockingQueue<HTSynchEvent>(
				10000);
		HashTreeSyncManagerComponents componentsLocal = createHashTreeSyncManager(
				new HashTreesSynchListProvider() {

					@Override
					public List<ServerName> getServerNameListFor(long treeId) {
						return syncList;
					}
				}, localEvents,
				HashTreesConstants.DEFAULT_HASH_TREE_SERVER_PORT_NO, 3000, 300);
		HashTreesManager localSyncManager = componentsLocal.htMgr;
		componentsLocal.simpleMemStore.put(
				HashTreesImplTestUtils.randomBytes(),
				HashTreesImplTestUtils.randomBytes());

		BlockingQueue<HTSynchEvent> remoteEvents = new ArrayBlockingQueue<HTSynchEvent>(
				10000);
		HashTreeSyncManagerComponents componentsRemote = createHashTreeSyncManager(
				new EmptySyncListProvider(), remoteEvents, 8999, 3000, 300);
		HashTreesManager remoteSyncManager = componentsRemote.htMgr;

		try {
			remoteSyncManager.start();
			ServerName rTreeInfo = new ServerName("localhost", 8999);
			syncList.add(rTreeInfo);
			localSyncManager.start();

			waitForTheEvent(localEvents, HTSynchEvent.SYNCH, 10000);
			waitForTheEvent(remoteEvents, HTSynchEvent.SYNCH_INITIATED, 10000);
		} finally {
			localSyncManager.stop();
			remoteSyncManager.stop();
		}
	}

	@Test(expected = SynchNotAllowedException.class)
	public void testSynchAuthentication() throws IOException,
			SynchNotAllowedException {
		HashTreesManager manager = new HashTreesManager(10, 0, 0, true, true,
				null, null, null, null, new HashTreesSynchAuthenticator() {

					@Override
					public boolean canSynch(ServerName source, ServerName dest) {
						return false;
					}
				}, SyncType.UPDATE);
		manager.synch(null, 1);
	}

	@Test
	public void testObservers() throws InterruptedException {
		final List<ServerName> servers = Collections
				.synchronizedList(new ArrayList<ServerName>());
		servers.add(new ServerName("localhost", 8999));
		HashTreesManager localManager = new HashTreesManager.Builder(
				"localhost",
				HashTreesConstants.DEFAULT_HASH_TREE_SERVER_PORT_NO,
				new MockHashTrees(), TREE_ID_PROVIDER,
				new HashTreesSynchListProvider() {

					@Override
					public List<ServerName> getServerNameListFor(long treeId) {
						return servers;
					}
				}).setFullRebuildPeriod(3000).schedule(300).build();

		HashTreesManager remoteManager = new HashTreesManager.Builder(
				"localhost", 8999, new MockHashTrees(), TREE_ID_PROVIDER,
				new EmptySyncListProvider()).build();

		final AtomicIntegerArray receivedCalls = new AtomicIntegerArray(2);
		final CountDownLatch receivedCallsLatch = new CountDownLatch(2);

		try {
			localManager.addObserver(new HashTreesManagerObserver() {

				@Override
				public void preSync(long treeId, ServerName remoteServerName) {
					receivedCalls.set(0, 1);
					receivedCallsLatch.countDown();
				}

				@Override
				public void postSync(long treeId, ServerName remoteServerName,
						SyncDiffResult result, boolean synced) {
					receivedCalls.set(1, 1);
					receivedCallsLatch.countDown();
				}

			});

			remoteManager.start();
			localManager.start();
			receivedCallsLatch.await(10, TimeUnit.SECONDS);
			for (int i = 0; i < 2; i++)
				Assert.assertEquals(1, receivedCalls.get(i));
		} finally {
			remoteManager.stop();
			localManager.stop();
		}

	}
}
