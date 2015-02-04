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
import static org.hashtrees.test.utils.HashTreesImplTestUtils.DEFAULT_TREE_ID;
import static org.hashtrees.test.utils.HashTreesImplTestUtils.ROOT_NODE;
import static org.hashtrees.test.utils.HashTreesImplTestUtils.SEG_ID_PROVIDER;
import static org.hashtrees.test.utils.HashTreesImplTestUtils.TREE_ID_PROVIDER;
import static org.hashtrees.test.utils.HashTreesImplTestUtils.createHashTree;
import static org.hashtrees.test.utils.HashTreesImplTestUtils.generateInMemoryAndPersistentStores;
import static org.hashtrees.test.utils.HashTreesImplTestUtils.generateInMemoryStore;
import static org.hashtrees.test.utils.HashTreesImplTestUtils.generatePersistentStore;
import static org.hashtrees.test.utils.HashTreesImplTestUtils.randomByteBuffer;
import static org.hashtrees.test.utils.HashTreesImplTestUtils.randomBytes;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicIntegerArray;

import org.apache.commons.codec.binary.Hex;
import org.apache.thrift.transport.TTransportException;
import org.hashtrees.HashTrees;
import org.hashtrees.HashTreesImpl;
import org.hashtrees.HashTreesObserver;
import org.hashtrees.SimpleTreeIdProvider;
import org.hashtrees.SyncDiffResult;
import org.hashtrees.manager.EmptySyncListProvider;
import org.hashtrees.manager.HashTreesManager;
import org.hashtrees.manager.HashTreesRemoteClient;
import org.hashtrees.manager.HashTreesSynchListProvider;
import org.hashtrees.store.HashTreesMemStore;
import org.hashtrees.store.HashTreesPersistentStore;
import org.hashtrees.store.HashTreesStore;
import org.hashtrees.store.SimpleMemStore;
import org.hashtrees.store.Store;
import org.hashtrees.test.utils.HashTreesConstants;
import org.hashtrees.test.utils.HashTreesImplTestUtils;
import org.hashtrees.test.utils.HashTreesImplTestUtils.HTreeComponents;
import org.hashtrees.thrift.generated.KeyValue;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;
import org.hashtrees.thrift.generated.ServerName;
import org.hashtrees.util.ByteUtils;
import org.hashtrees.util.NonBlockingQueuingTask.QueueReachedMaxCapacityException;
import org.junit.Assert;
import org.junit.Test;

class HashTreesImplTest {

	private static byte[] generateBytesFrom(int... values) {
		byte[] result = new byte[values.length * ByteUtils.SIZEOF_INT];
		ByteBuffer bb = ByteBuffer.wrap(result);
		for (int value : values)
			bb.putInt(value);
		return result;
	}

	private static ByteBuffer generateRandomKeyWithPrefix(int prefixValue) {
		byte[] randomBytes = randomBytes();
		byte[] key = new byte[ByteUtils.SIZEOF_INT + randomBytes.length];
		ByteBuffer bb = ByteBuffer.wrap(key);
		bb.putInt(prefixValue);
		bb.put(randomBytes);
		return bb;
	}

	@Test
	public void testPut() throws IOException {

		HashTreesStore[] stores = generateInMemoryAndPersistentStores();

		try {
			for (HashTreesStore store : stores) {
				int segId = 1;

				HTreeComponents components = createHashTree(
						DEFAULT_SEG_DATA_BLOCKS_COUNT, false, TREE_ID_PROVIDER,
						SEG_ID_PROVIDER, store);
				HashTrees testTree = components.hTree;
				HashTreesStore testTreeStore = components.hTStore;

				ByteBuffer key = generateRandomKeyWithPrefix(segId);
				ByteBuffer value = ByteBuffer.wrap(randomBytes());
				testTree.hPut(key, value);
				ByteBuffer expectedDigest = ByteBuffer.wrap(ByteUtils
						.sha1(value.array()));

				SegmentData actualKeyAndDigest = testTree.getSegmentData(
						SimpleTreeIdProvider.TREE_ID, segId, key);
				Assert.assertNotNull(actualKeyAndDigest);
				Assert.assertTrue(Arrays.equals(key.array(),
						actualKeyAndDigest.getKey()));
				Assert.assertTrue(Arrays.equals(expectedDigest.array(),
						actualKeyAndDigest.getDigest()));

				List<Integer> dirtySegs = testTreeStore
						.getDirtySegments(SimpleTreeIdProvider.TREE_ID);
				Assert.assertEquals(1, dirtySegs.size());
				Assert.assertEquals(1, dirtySegs.get(0).intValue());
			}
		} finally {
			HashTreesImplTestUtils.closeStores(stores);
		}
	}

	@Test
	public void testRemove() throws IOException {

		HashTreesStore[] stores = generateInMemoryAndPersistentStores();
		int segId = 2;

		try {
			for (HashTreesStore store : stores) {
				HTreeComponents components = createHashTree(
						DEFAULT_SEG_DATA_BLOCKS_COUNT, false, TREE_ID_PROVIDER,
						SEG_ID_PROVIDER, store);
				HashTrees testTree = components.hTree;
				HashTreesStore testTreeStore = components.hTStore;

				ByteBuffer key = generateRandomKeyWithPrefix(segId);
				ByteBuffer value = ByteBuffer.wrap(randomBytes());
				testTree.hPut(key, value);
				testTree.hRemove(key);

				SegmentData segData = testTree.getSegmentData(
						SimpleTreeIdProvider.TREE_ID, segId, key);
				Assert.assertNull(segData);

				List<Integer> dirtySegs = testTreeStore
						.getDirtySegments(SimpleTreeIdProvider.TREE_ID);
				Assert.assertEquals(1, dirtySegs.size());
				Assert.assertEquals(2, dirtySegs.get(0).intValue());
			}
		} finally {
			HashTreesImplTestUtils.closeStores(stores);
		}
	}

	@Test
	public void testCompleteRebuild() throws IOException {
		int rootNodeId = 0;
		int nodeId = 2;
		int segId = 1;
		int noOfSegments = 2;
		HashTreesStore[] stores = generateInMemoryAndPersistentStores();

		try {
			for (HashTreesStore store : stores) {
				HTreeComponents components = createHashTree(noOfSegments,
						false, TREE_ID_PROVIDER, SEG_ID_PROVIDER, store);
				HashTrees testTree = components.hTree;
				SimpleMemStore kvStore = components.store;

				ByteBuffer expectedKey = generateRandomKeyWithPrefix(segId);
				ByteBuffer expectedValue = ByteBuffer.wrap(randomBytes());
				StringBuffer sb = new StringBuffer();
				ByteBuffer expectedDigest = ByteBuffer.wrap(ByteUtils
						.sha1(expectedValue.array()));
				sb.append(HashTreesImpl.getHexString(expectedKey,
						expectedDigest) + "\n");
				byte[] expectedLeafNodeDigest = ByteUtils.sha1(sb.toString()
						.getBytes());

				testTree.hPut(randomByteBuffer(), randomByteBuffer());
				kvStore.put(expectedKey.array(), expectedValue.array());
				testTree.rebuildHashTree(SimpleTreeIdProvider.TREE_ID, true);
				SegmentHash segHash = testTree.getSegmentHash(
						SimpleTreeIdProvider.TREE_ID, nodeId);
				Assert.assertNotNull(segHash);
				Assert.assertTrue(Arrays.equals(expectedLeafNodeDigest,
						segHash.getHash()));

				sb.setLength(0);
				sb.append(Hex.encodeHexString(expectedLeafNodeDigest) + "\n");
				byte[] expectedRootNodeDigest = ByteUtils.sha1(sb.toString()
						.getBytes());
				SegmentHash actualRootNodeDigest = testTree.getSegmentHash(
						SimpleTreeIdProvider.TREE_ID, rootNodeId);
				Assert.assertNotNull(actualRootNodeDigest);
				Assert.assertTrue(Arrays.equals(expectedRootNodeDigest,
						actualRootNodeDigest.getHash()));
			}
		} finally {
			HashTreesImplTestUtils.closeStores(stores);
		}
	}

	@Test
	public void testUpdateSegmentHashesTest() throws IOException {

		int rootNodeId = 0;
		int nodeId = 2;
		int segId = 1;
		int noOfSegments = 2;
		HashTreesStore[] stores = generateInMemoryAndPersistentStores();

		try {
			for (HashTreesStore store : stores) {
				HTreeComponents components = createHashTree(noOfSegments,
						false, TREE_ID_PROVIDER, SEG_ID_PROVIDER, store);
				HashTrees testTree = components.hTree;

				ByteBuffer expectedKey = generateRandomKeyWithPrefix(segId);
				ByteBuffer expectedValue = ByteBuffer.wrap(randomBytes());
				StringBuffer sb = new StringBuffer();
				ByteBuffer expectedDigest = ByteBuffer.wrap(ByteUtils
						.sha1(expectedValue.array()));
				sb.append(HashTreesImpl.getHexString(expectedKey,
						expectedDigest) + "\n");
				byte[] expectedLeafNodeDigest = ByteUtils.sha1(sb.toString()
						.getBytes());

				testTree.hPut(expectedKey, expectedValue);
				testTree.rebuildHashTree(SimpleTreeIdProvider.TREE_ID, false);
				SegmentHash segHash = testTree.getSegmentHash(
						SimpleTreeIdProvider.TREE_ID, nodeId);
				Assert.assertNotNull(segHash);
				Assert.assertTrue(Arrays.equals(expectedLeafNodeDigest,
						segHash.getHash()));

				sb.setLength(0);
				sb.append(Hex.encodeHexString(expectedLeafNodeDigest) + "\n");
				byte[] expectedRootNodeDigest = ByteUtils.sha1(sb.toString()
						.getBytes());
				SegmentHash actualRootNodeDigest = testTree.getSegmentHash(
						SimpleTreeIdProvider.TREE_ID, rootNodeId);
				Assert.assertNotNull(actualRootNodeDigest);
				Assert.assertTrue(Arrays.equals(expectedRootNodeDigest,
						actualRootNodeDigest.getHash()));
			}
		} finally {
			HashTreesImplTestUtils.closeStores(stores);
		}
	}

	@Test
	public void testSynchWithEmptyTree() throws IOException {
		HashTreesStore[] stores = generateInMemoryAndPersistentStores();
		HashTreesStore[] remoteStores = generateInMemoryAndPersistentStores();

		try {
			for (int j = 0; j <= 1; j++) {
				HTreeComponents localHTreeComp = createHashTree(
						DEFAULT_SEG_DATA_BLOCKS_COUNT, false, stores[j]);
				HTreeComponents remoteHTreeComp = createHashTree(
						DEFAULT_SEG_DATA_BLOCKS_COUNT, false, remoteStores[j]);

				for (int i = 1; i <= DEFAULT_SEG_DATA_BLOCKS_COUNT; i++) {
					localHTreeComp.store.put(randomBytes(), randomBytes());
				}

				localHTreeComp.hTree.rebuildHashTree(
						SimpleTreeIdProvider.TREE_ID, false);
				SyncDiffResult synchDiff = localHTreeComp.hTree.synch(1,
						remoteHTreeComp.hTree);
				Assert.assertNotNull(synchDiff);
				Assert.assertTrue(synchDiff.isAnyUpdatesMade());

				remoteHTreeComp.hTree.rebuildHashTree(
						SimpleTreeIdProvider.TREE_ID, false);
				synchDiff = localHTreeComp.hTree
						.synch(1, remoteHTreeComp.hTree);
				Assert.assertFalse(synchDiff.isAnyUpdatesMade());

				SegmentHash localRootHash = localHTreeComp.hTree
						.getSegmentHash(DEFAULT_TREE_ID, ROOT_NODE);
				Assert.assertNotNull(localRootHash);
				SegmentHash remoteRootHash = remoteHTreeComp.hTree
						.getSegmentHash(DEFAULT_TREE_ID, ROOT_NODE);
				Assert.assertNotNull(remoteRootHash);

				Assert.assertTrue(Arrays.equals(localRootHash.getHash(),
						remoteRootHash.getHash()));
			}
		} finally {
			HashTreesImplTestUtils.closeStores(stores);
			HashTreesImplTestUtils.closeStores(remoteStores);
		}
	}

	@Test
	public void testSynchWithMissingBlocksInLocal() throws IOException {

		HashTreesStore[] stores = generateInMemoryAndPersistentStores();
		HashTreesStore[] remoteStores = generateInMemoryAndPersistentStores();

		try {
			for (int j = 0; j <= 1; j++) {
				HTreeComponents localHTreeComp = createHashTree(
						DEFAULT_SEG_DATA_BLOCKS_COUNT, false, stores[j]);
				HTreeComponents remoteHTreeComp = createHashTree(
						DEFAULT_SEG_DATA_BLOCKS_COUNT, false, remoteStores[j]);

				for (int i = 0; i < DEFAULT_SEG_DATA_BLOCKS_COUNT; i++) {
					remoteHTreeComp.store.put(randomBytes(), randomBytes());
				}

				localHTreeComp.hTree.rebuildHashTree(
						SimpleTreeIdProvider.TREE_ID, false);
				remoteHTreeComp.hTree.rebuildHashTree(
						SimpleTreeIdProvider.TREE_ID, false);
				localHTreeComp.hTree.synch(1, remoteHTreeComp.hTree);

				Assert.assertEquals(localHTreeComp.store, remoteHTreeComp.store);
				Assert.assertFalse(localHTreeComp.store.iterator().hasNext());
				Assert.assertFalse(remoteHTreeComp.store.iterator().hasNext());
			}
		} finally {
			HashTreesImplTestUtils.closeStores(stores);
			HashTreesImplTestUtils.closeStores(remoteStores);
		}
	}

	@Test
	public void testSynchWithMissingBlocksInRemote() throws IOException {

		HashTreesStore[] stores = generateInMemoryAndPersistentStores();
		HashTreesStore[] remoteStores = generateInMemoryAndPersistentStores();

		try {
			for (int j = 0; j <= 1; j++) {
				HTreeComponents localHTreeComp = createHashTree(
						DEFAULT_SEG_DATA_BLOCKS_COUNT, false, stores[j]);
				HTreeComponents remoteHTreeComp = createHashTree(
						DEFAULT_SEG_DATA_BLOCKS_COUNT, false, remoteStores[j]);

				for (int i = 0; i < DEFAULT_SEG_DATA_BLOCKS_COUNT; i++) {
					localHTreeComp.store.put(randomBytes(), randomBytes());
				}

				localHTreeComp.hTree.rebuildHashTree(
						SimpleTreeIdProvider.TREE_ID, false);
				remoteHTreeComp.hTree.rebuildHashTree(
						SimpleTreeIdProvider.TREE_ID, false);
				localHTreeComp.hTree.synch(1, remoteHTreeComp.hTree);

				Assert.assertEquals(localHTreeComp.store, remoteHTreeComp.store);
			}
		} finally {
			HashTreesImplTestUtils.closeStores(stores);
			HashTreesImplTestUtils.closeStores(remoteStores);
		}
	}

	@Test
	public void testSynchTreeWithDifferingSegmentData() throws IOException {
		HashTreesStore[] stores = generateInMemoryAndPersistentStores();
		HashTreesStore[] remoteStores = generateInMemoryAndPersistentStores();
		int segId = 1;

		try {
			for (int j = 0; j <= 1; j++) {
				HTreeComponents localHTreeComp = createHashTree(4, false,
						TREE_ID_PROVIDER, SEG_ID_PROVIDER, stores[j]);
				HTreeComponents remoteHTreeComp = createHashTree(4, false,
						TREE_ID_PROVIDER, SEG_ID_PROVIDER, remoteStores[j]);

				for (int k = 0; k <= 1; k++) {
					byte[] key = generateBytesFrom(segId, k);
					remoteHTreeComp.store.put(key, randomBytes());
				}

				for (int k = 1; k <= 2; k++) {
					byte[] key = generateBytesFrom(segId, k);
					localHTreeComp.store.put(key, randomBytes());
				}

				localHTreeComp.hTree.rebuildHashTree(
						SimpleTreeIdProvider.TREE_ID, false);
				remoteHTreeComp.hTree.rebuildHashTree(
						SimpleTreeIdProvider.TREE_ID, false);
				localHTreeComp.hTree.synch(1, remoteHTreeComp.hTree);

				Assert.assertEquals(localHTreeComp.store, remoteHTreeComp.store);
			}

		} finally {
			HashTreesImplTestUtils.closeStores(stores);
			HashTreesImplTestUtils.closeStores(remoteStores);
		}
	}

	@Test
	public void testHashTreeServerAndClient() throws IOException,
			TTransportException {
		HashTreesStore store = generateInMemoryStore();
		HashTreesStore remoteStore = generateInMemoryStore();
		HashTreesSynchListProvider syncListProvider = new EmptySyncListProvider();

		try {
			HTreeComponents localHTreeComp = createHashTree(
					DEFAULT_SEG_DATA_BLOCKS_COUNT, false, store);
			HTreeComponents remoteHTreeComp = createHashTree(
					DEFAULT_SEG_DATA_BLOCKS_COUNT, false, remoteStore);
			HashTreesManager hTreeManager = new HashTreesManager.Builder(
					"test",
					HashTreesConstants.DEFAULT_HASH_TREE_SERVER_PORT_NO,
					remoteHTreeComp.hTree, TREE_ID_PROVIDER, syncListProvider)
					.build();

			hTreeManager.start();
			HashTreesRemoteClient thriftClient = new HashTreesRemoteClient(
					new ServerName("localhost",
							HashTreesConstants.DEFAULT_HASH_TREE_SERVER_PORT_NO));

			for (int i = 1; i <= DEFAULT_SEG_DATA_BLOCKS_COUNT; i++) {
				localHTreeComp.store.put(randomBytes(), randomBytes());
			}

			localHTreeComp.hTree.rebuildHashTree(SimpleTreeIdProvider.TREE_ID,
					false);
			localHTreeComp.hTree.synch(1, thriftClient);

			for (int i = 0; i < DEFAULT_SEG_DATA_BLOCKS_COUNT; i++) {
				List<SegmentData> segBlock = remoteHTreeComp.hTree.getSegment(
						DEFAULT_TREE_ID, i);
				for (SegmentData sData : segBlock) {
					localHTreeComp.store.put(sData.getKey(), randomBytes());
				}
				localHTreeComp.hTree.rebuildHashTree(
						SimpleTreeIdProvider.TREE_ID, false);
				remoteHTreeComp.hTree.rebuildHashTree(
						SimpleTreeIdProvider.TREE_ID, false);
				localHTreeComp.hTree.synch(1, thriftClient);

				Assert.assertEquals(localHTreeComp.store, remoteHTreeComp.store);
			}

			hTreeManager.stop();
		} finally {
			HashTreesImplTestUtils.closeStores(store);
			HashTreesImplTestUtils.closeStores(remoteStore);
		}
	}

	@Test
	public void testNonBlockingCalls() throws IOException {
		int maxQueueSize = 5;
		final CountDownLatch putLatch = new CountDownLatch(1);
		final CountDownLatch deleteLatch = new CountDownLatch(1);

		HashTreesStore htStore = new HashTreesMemStore() {

			@Override
			public void putSegmentData(long treeId, int segId, ByteBuffer key,
					ByteBuffer digest) {
				try {
					putLatch.await();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void deleteSegmentData(long treeId, int segId, ByteBuffer key) {
				try {
					deleteLatch.await();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		};

		SimpleMemStore store = new SimpleMemStore();
		HashTreesImpl hTrees = new HashTreesImpl.Builder(store,
				TREE_ID_PROVIDER, htStore)
				.setNoOfSegments(DEFAULT_SEG_DATA_BLOCKS_COUNT)
				.setSegmentIdProvider(SEG_ID_PROVIDER)
				.setNonBlockingQueueSize(maxQueueSize).build();
		hTrees.start();

		boolean exceptionOccurred = false;
		for (int i = 0; i <= 2 * maxQueueSize; i++) {
			try {
				hTrees.hPut(randomByteBuffer(), randomByteBuffer());
			} catch (QueueReachedMaxCapacityException e) {
				exceptionOccurred = true;
			}
		}
		Assert.assertTrue(exceptionOccurred);

		hTrees = new HashTreesImpl.Builder(store, TREE_ID_PROVIDER, htStore)
				.setNoOfSegments(DEFAULT_SEG_DATA_BLOCKS_COUNT)
				.setSegmentIdProvider(SEG_ID_PROVIDER)
				.setNonBlockingQueueSize(maxQueueSize).build();
		hTrees.start();
		exceptionOccurred = false;
		for (int i = 0; i <= 2 * maxQueueSize; i++) {
			try {
				hTrees.hRemove(randomByteBuffer());
			} catch (QueueReachedMaxCapacityException e) {
				exceptionOccurred = true;
			}
		}
		Assert.assertTrue(exceptionOccurred);
	}

	@Test
	public void testRebuildTasksBetweenRestarts() throws IOException {
		HashTreesPersistentStore htStore = generatePersistentStore();
		int segId = 1;
		try {
			List<Integer> expectedSegIds = new ArrayList<>();
			expectedSegIds.add(segId);
			htStore.markSegments(DEFAULT_TREE_ID, expectedSegIds);
			htStore.stop();
			htStore = new HashTreesPersistentStore(htStore.getDbDir());
			List<Integer> actualSegIds = htStore
					.getDirtySegments(DEFAULT_TREE_ID);
			Assert.assertNotNull(actualSegIds);
			Assert.assertEquals(expectedSegIds, actualSegIds);
		} finally {
			htStore.delete();
		}
	}

	@Test
	public void testObservers() throws IOException {
		Store store = new SimpleMemStore();
		HashTreesStore htStore = generateInMemoryStore();
		HashTreesImpl hashTrees = new HashTreesImpl.Builder(store,
				TREE_ID_PROVIDER, htStore).setEnabledNonBlockingCalls(false)
				.build();
		final AtomicIntegerArray receivedCalls = new AtomicIntegerArray(10);
		hashTrees.addObserver(new HashTreesObserver() {

			@Override
			public void preSPut(List<KeyValue> keyValuePairs) {
				receivedCalls.set(0, 1);
			}

			@Override
			public void postSPut(List<KeyValue> keyValuePairs) {
				receivedCalls.set(1, 1);
			}

			@Override
			public void preSRemove(List<ByteBuffer> keys) {
				receivedCalls.set(2, 1);
			}

			@Override
			public void postSRemove(List<ByteBuffer> keys) {
				receivedCalls.set(3, 1);
			}

			@Override
			public void preHPut(ByteBuffer key, ByteBuffer value) {
				receivedCalls.set(4, 1);
			}

			@Override
			public void postHPut(ByteBuffer key, ByteBuffer value) {
				receivedCalls.set(5, 1);
			}

			@Override
			public void preHRemove(ByteBuffer key) {
				receivedCalls.set(6, 1);
			}

			@Override
			public void postHRemove(ByteBuffer key) {
				receivedCalls.set(7, 1);
			}

			@Override
			public void preRebuild(long treeId, boolean isFullRebuild) {
				Assert.assertTrue(isFullRebuild);
				receivedCalls.set(8, 1);
			}

			@Override
			public void postRebuild(long treeId, boolean isFullRebuild) {
				Assert.assertTrue(isFullRebuild);
				receivedCalls.set(9, 1);
			}
		});

		hashTrees.hPut(randomByteBuffer(), randomByteBuffer());
		hashTrees.rebuildHashTree(SimpleTreeIdProvider.TREE_ID, true);
		hashTrees.hRemove(randomByteBuffer());
		List<KeyValue> kvPairsList = new ArrayList<>();
		List<ByteBuffer> keys = new ArrayList<>();
		for (int i = 0; i < 1; i++) {
			kvPairsList
					.add(new KeyValue(randomByteBuffer(), randomByteBuffer()));
			keys.add(randomByteBuffer());
		}
		hashTrees.sPut(kvPairsList);
		hashTrees.sRemove(keys);
		for (int i = 0; i < 10; i++)
			Assert.assertEquals(1, receivedCalls.get(i));
	}
}
