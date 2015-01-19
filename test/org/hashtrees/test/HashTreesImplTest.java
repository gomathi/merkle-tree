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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.codec.binary.Hex;
import org.hashtrees.HashTrees;
import org.hashtrees.HashTreesConstants;
import org.hashtrees.HashTreesImpl;
import org.hashtrees.SimpleTreeIdProvider;
import org.hashtrees.SyncDiffResult;
import org.hashtrees.store.HashTreesMemStore;
import org.hashtrees.store.HashTreesPersistentStore;
import org.hashtrees.store.HashTreesStore;
import org.hashtrees.store.SimpleMemStore;
import org.hashtrees.store.Store;
import org.hashtrees.synch.EmptySyncListProvider;
import org.hashtrees.synch.HashTreesManager;
import org.hashtrees.synch.HashTreesSynchListProvider;
import org.hashtrees.synch.HashTreesThriftClientProvider;
import org.hashtrees.test.utils.HashTreesImplTestUtils;
import org.hashtrees.test.utils.HashTreesImplTestUtils.HTreeComponents;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;
import org.hashtrees.thrift.generated.ServerName;
import org.hashtrees.util.ByteUtils;
import org.hashtrees.util.NonBlockingQueuingTask.QueueReachedMaxCapacityException;
import org.junit.Assert;
import org.junit.Test;

public class HashTreesImplTest {

	private static ByteBuffer generateBytesFrom(int... values) {
		byte[] result = new byte[values.length * ByteUtils.SIZEOF_INT];
		ByteBuffer bb = ByteBuffer.wrap(result);
		for (int value : values)
			bb.putInt(value);
		return bb;
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
	public void testPut() throws Exception {

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
	public void testRemove() throws Exception {

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
	public void testCompleteRebuild() throws Exception {
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
				Store kvStore = components.store;
				kvStore.registerHashTrees(null);

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
	public void testUpdateSegmentHashesTest() throws Exception {

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
				testTree.rebuildAllTrees(false);
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
	public void testSynchWithEmptyTree() throws Exception {
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

				localHTreeComp.hTree.rebuildAllTrees(false);
				SyncDiffResult synchDiff = localHTreeComp.hTree.synch(1,
						remoteHTreeComp.hTree);
				Assert.assertNotNull(synchDiff);
				Assert.assertTrue(synchDiff.isAnyUpdatesMade());

				remoteHTreeComp.hTree.rebuildAllTrees(false);
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
	public void testSynchWithMissingBlocksInLocal() throws Exception {

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

				localHTreeComp.hTree.rebuildAllTrees(false);
				remoteHTreeComp.hTree.rebuildAllTrees(false);
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
	public void testSynchWithMissingBlocksInRemote() throws Exception {

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

				localHTreeComp.hTree.rebuildAllTrees(false);
				remoteHTreeComp.hTree.rebuildAllTrees(false);
				localHTreeComp.hTree.synch(1, remoteHTreeComp.hTree);

				Assert.assertEquals(localHTreeComp.store, remoteHTreeComp.store);
			}
		} finally {
			HashTreesImplTestUtils.closeStores(stores);
			HashTreesImplTestUtils.closeStores(remoteStores);
		}
	}

	@Test
	public void testSynchTreeWithDifferingSegmentData() throws Exception {
		HashTreesStore[] stores = generateInMemoryAndPersistentStores();
		HashTreesStore[] remoteStores = generateInMemoryAndPersistentStores();
		int segId = 1;

		try {
			for (int j = 0; j <= 1; j++) {
				HTreeComponents localHTreeComp = createHashTree(
						DEFAULT_SEG_DATA_BLOCKS_COUNT, false, TREE_ID_PROVIDER,
						SEG_ID_PROVIDER, stores[j]);
				HTreeComponents remoteHTreeComp = createHashTree(
						DEFAULT_SEG_DATA_BLOCKS_COUNT, false, TREE_ID_PROVIDER,
						SEG_ID_PROVIDER, remoteStores[j]);

				for (int k = 0; k <= 1; k++) {
					ByteBuffer key = generateBytesFrom(segId, k);
					remoteHTreeComp.store.put(key.array(), randomBytes());
				}

				for (int k = 1; k <= 2; k++) {
					ByteBuffer key = generateBytesFrom(segId, k);
					localHTreeComp.store.put(key.array(), randomBytes());
				}

				localHTreeComp.hTree.rebuildAllTrees(false);
				remoteHTreeComp.hTree.rebuildAllTrees(false);
				localHTreeComp.hTree.synch(1, remoteHTreeComp.hTree);

				Assert.assertEquals(localHTreeComp.store, remoteHTreeComp.store);
			}

		} finally {
			HashTreesImplTestUtils.closeStores(stores);
			HashTreesImplTestUtils.closeStores(remoteStores);
		}
	}

	@Test
	public void testHashTreeServerAndClient() throws Exception {
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

			hTreeManager.init();
			HashTrees thriftClient = HashTreesThriftClientProvider
					.getHashTreeRemoteClient(new ServerName("localhost",
							HashTreesConstants.DEFAULT_HASH_TREE_SERVER_PORT_NO));

			for (int i = 1; i <= DEFAULT_SEG_DATA_BLOCKS_COUNT; i++) {
				localHTreeComp.store.put(randomBytes(), randomBytes());
			}

			localHTreeComp.hTree.rebuildAllTrees(false);
			localHTreeComp.hTree.synch(1, thriftClient);

			for (int i = 0; i < DEFAULT_SEG_DATA_BLOCKS_COUNT; i++) {
				List<SegmentData> segBlock = remoteHTreeComp.hTree.getSegment(
						DEFAULT_TREE_ID, i);
				for (SegmentData sData : segBlock) {
					localHTreeComp.store.put(sData.getKey(), randomBytes());
				}
				localHTreeComp.hTree.rebuildAllTrees(false);
				remoteHTreeComp.hTree.rebuildAllTrees(false);
				localHTreeComp.hTree.synch(1, thriftClient);

				Assert.assertEquals(localHTreeComp.store, remoteHTreeComp.store);
			}

			hTreeManager.shutdown();
		} finally {
			HashTreesImplTestUtils.closeStores(store);
			HashTreesImplTestUtils.closeStores(remoteStore);
		}
	}

	@Test
	public void testNonBlockingCalls() throws Exception {
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
		hTrees.init();

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
		hTrees.init();
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
	public void testRebuildTasksBetweenRestarts() throws Exception {
		HashTreesPersistentStore htStore = generatePersistentStore();
		int segId = 1;
		try {
			List<Integer> expectedSegIds = new ArrayList<>();
			expectedSegIds.add(segId);
			htStore.putSegmentData(DEFAULT_TREE_ID, segId, randomByteBuffer(),
					randomByteBuffer());
			htStore.markSegments(DEFAULT_TREE_ID, expectedSegIds);
			createHashTree(DEFAULT_SEG_DATA_BLOCKS_COUNT, false, htStore);
			List<Integer> actualSegIds = htStore
					.getDirtySegments(DEFAULT_TREE_ID);
			Assert.assertNotNull(actualSegIds);
			Assert.assertEquals(expectedSegIds.size(), actualSegIds.size());
			Collections.sort(actualSegIds);
			Assert.assertEquals(expectedSegIds, actualSegIds);
		} finally {
			htStore.delete();
		}
	}
}
