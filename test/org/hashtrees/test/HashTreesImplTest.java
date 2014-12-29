package org.hashtrees.test;

import static org.hashtrees.test.HashTreesImplTestUtils.DEFAULT_SEG_DATA_BLOCKS_COUNT;
import static org.hashtrees.test.HashTreesImplTestUtils.DEFAULT_TREE_ID;
import static org.hashtrees.test.HashTreesImplTestUtils.ROOT_NODE;
import static org.hashtrees.test.HashTreesImplTestUtils.createHashTree;
import static org.hashtrees.test.HashTreesImplTestUtils.generateInMemoryAndPersistentStores;
import static org.hashtrees.test.HashTreesImplTestUtils.generateInMemoryStore;
import static org.hashtrees.test.HashTreesImplTestUtils.randomByteBuffer;
import static org.hashtrees.test.HashTreesImplTestUtils.randomBytes;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Hex;
import org.hashtrees.HashTrees;
import org.hashtrees.HashTreesConstants;
import org.hashtrees.HashTreesImpl;
import org.hashtrees.store.HashTreesMemStore;
import org.hashtrees.store.HashTreesStore;
import org.hashtrees.synch.HashTreeSyncManagerStore;
import org.hashtrees.synch.HashTreesSyncManagerImpl;
import org.hashtrees.synch.HashTreesThriftClientProvider;
import org.hashtrees.test.HashTreesImplTestUtils.HTreeComponents;
import org.hashtrees.test.HashTreesImplTestUtils.HashTreeIdProviderTest;
import org.hashtrees.test.HashTreesImplTestUtils.SegIdProviderTest;
import org.hashtrees.test.HashTreesImplTestUtils.StorageImplTest;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;
import org.hashtrees.thrift.generated.ServerName;
import org.hashtrees.util.ByteUtils;
import org.junit.Assert;
import org.junit.Test;

public class HashTreesImplTest {

	private static final SegIdProviderTest segIdProvider = new SegIdProviderTest();
	private static final HashTreeIdProviderTest treeIdProvider = new HashTreeIdProviderTest();
	private static final int noOfSegDataBlocks = 1 << 10;

	@Test
	public void testPut() throws Exception {

		HashTreesStore[] stores = generateInMemoryAndPersistentStores();

		try {
			for (HashTreesStore store : stores) {
				int treeId = 1;
				int segId = 1;
				String stringKey = "1";

				HTreeComponents components = createHashTree(noOfSegDataBlocks,
						treeIdProvider, segIdProvider, store);
				HashTrees testTree = components.hTree;
				HashTreesStore testTreeStorage = components.hTStorage;

				ByteBuffer key = ByteBuffer.wrap(stringKey.getBytes());
				ByteBuffer value = ByteBuffer.wrap(randomBytes());
				testTree.hPut(key, value);
				ByteBuffer digest = ByteBuffer.wrap(ByteUtils.sha1(value
						.array()));

				SegmentData segData = testTreeStorage.getSegmentData(treeId,
						segId, key);
				Assert.assertNotNull(segData);
				Assert.assertTrue(Arrays.equals(key.array(), segData.getKey()));
				Assert.assertTrue(Arrays.equals(digest.array(),
						segData.getDigest()));

				List<Integer> dirtySegs = testTreeStorage
						.clearAndGetDirtySegments(treeId);
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

		try {
			for (HashTreesStore store : stores) {
				HTreeComponents components = createHashTree(noOfSegDataBlocks,
						treeIdProvider, segIdProvider, store);
				HashTrees testTree = components.hTree;
				HashTreesStore testTreeStorage = components.hTStorage;

				ByteBuffer key = ByteBuffer.wrap("2".getBytes());
				ByteBuffer value = ByteBuffer.wrap(randomBytes());
				testTree.hPut(key, value);
				testTree.hRemove(key);

				SegmentData segData = testTreeStorage.getSegmentData(1, 2, key);
				Assert.assertNull(segData);

				List<Integer> dirtySegs = testTreeStorage
						.clearAndGetDirtySegments(1);
				Assert.assertEquals(1, dirtySegs.size());
				Assert.assertEquals(2, dirtySegs.get(0).intValue());
			}
		} finally {
			HashTreesImplTestUtils.closeStores(stores);
		}
	}

	@Test
	public void testUpdateSegmentHashesTest() throws Exception {

		int tesNoOfSegDataBlocks = 2;
		HashTreesStore[] stores = generateInMemoryAndPersistentStores();

		try {
			for (HashTreesStore store : stores) {
				HTreeComponents components = createHashTree(
						tesNoOfSegDataBlocks, treeIdProvider, segIdProvider,
						store);
				HashTrees testTree = components.hTree;
				HashTreesStore testTreeStorage = components.hTStorage;

				ByteBuffer key = ByteBuffer.wrap("1".getBytes());
				ByteBuffer value = ByteBuffer.wrap(randomBytes());
				testTree.hPut(key, value);

				testTree.rebuildHashTrees(false);

				StringBuffer sb = new StringBuffer();
				ByteBuffer digest = ByteBuffer.wrap(ByteUtils.sha1(value
						.array()));
				sb.append(HashTreesImpl.getHexString(key, digest) + "\n");
				byte[] expectedLeafNodeDigest = ByteUtils.sha1(sb.toString()
						.getBytes());
				SegmentHash segHash = testTreeStorage.getSegmentHash(1, 2);
				Assert.assertNotNull(segHash);
				Assert.assertTrue(Arrays.equals(expectedLeafNodeDigest,
						segHash.getHash()));

				sb.setLength(0);
				sb.append(Hex.encodeHexString(expectedLeafNodeDigest) + "\n");
				byte[] expectedRootNodeDigest = ByteUtils.sha1(sb.toString()
						.getBytes());
				SegmentHash actualRootNodeDigest = testTreeStorage
						.getSegmentHash(1, 0);
				Assert.assertNotNull(actualRootNodeDigest);
				Assert.assertTrue(Arrays.equals(expectedRootNodeDigest,
						actualRootNodeDigest.getHash()));
			}
		} finally {
			HashTreesImplTestUtils.closeStores(stores);
		}
	}

	@Test
	public void testUpdateWithEmptyTree() throws Exception {
		HashTreesStore[] stores = generateInMemoryAndPersistentStores();
		HashTreesStore[] remoteStores = generateInMemoryAndPersistentStores();

		try {
			for (int j = 1; j <= 1; j++) {
				HTreeComponents localHTreeComp = createHashTree(
						DEFAULT_SEG_DATA_BLOCKS_COUNT, stores[j]);
				HTreeComponents remoteHTreeComp = createHashTree(
						DEFAULT_SEG_DATA_BLOCKS_COUNT, remoteStores[j]);

				for (int i = 1; i <= DEFAULT_SEG_DATA_BLOCKS_COUNT; i++) {
					localHTreeComp.storage.put(randomByteBuffer(),
							randomByteBuffer());
				}

				localHTreeComp.hTree.rebuildHashTrees(false);
				boolean anyUpdates = localHTreeComp.hTree.synch(1,
						remoteHTreeComp.hTree);
				Assert.assertTrue(anyUpdates);

				remoteHTreeComp.hTree.rebuildHashTrees(false);
				anyUpdates = localHTreeComp.hTree.synch(1,
						remoteHTreeComp.hTree);
				Assert.assertFalse(anyUpdates);

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
	public void testUpdateTreeWithMissingBlocksInLocal() throws Exception {

		HashTreesStore[] stores = generateInMemoryAndPersistentStores();
		HashTreesStore[] remoteStores = generateInMemoryAndPersistentStores();

		try {
			for (int j = 1; j <= 1; j++) {
				HTreeComponents localHTreeComp = createHashTree(
						DEFAULT_SEG_DATA_BLOCKS_COUNT, stores[j]);
				HTreeComponents remoteHTreeComp = createHashTree(
						DEFAULT_SEG_DATA_BLOCKS_COUNT, remoteStores[j]);

				for (int i = 1; i <= DEFAULT_SEG_DATA_BLOCKS_COUNT; i++) {
					localHTreeComp.storage.put(randomByteBuffer(),
							randomByteBuffer());
				}

				localHTreeComp.hTree.rebuildHashTrees(false);
				localHTreeComp.hTree.synch(1, remoteHTreeComp.hTree);

				for (int i = 0; i < DEFAULT_SEG_DATA_BLOCKS_COUNT; i++) {
					List<SegmentData> segBlock = remoteHTreeComp.hTree
							.getSegment(DEFAULT_TREE_ID, i);
					for (SegmentData sData : segBlock) {
						localHTreeComp.storage.remove(ByteBuffer.wrap(sData
								.getKey()));
					}
					localHTreeComp.hTree.rebuildHashTrees(false);
					remoteHTreeComp.hTree.rebuildHashTrees(false);
					localHTreeComp.hTree.synch(1, remoteHTreeComp.hTree);

					Assert.assertEquals(localHTreeComp.storage.localStorage,
							remoteHTreeComp.storage.localStorage);
				}

				Assert.assertEquals(0,
						localHTreeComp.storage.localStorage.size());
				Assert.assertEquals(0,
						remoteHTreeComp.storage.localStorage.size());
			}
		} finally {
			HashTreesImplTestUtils.closeStores(stores);
			HashTreesImplTestUtils.closeStores(remoteStores);
		}
	}

	@Test
	public void testUpdateTreeWithMissingBlocksInRemote() throws Exception {

		HashTreesStore[] stores = generateInMemoryAndPersistentStores();
		HashTreesStore[] remoteStores = generateInMemoryAndPersistentStores();

		try {
			for (int j = 1; j <= 1; j++) {
				HTreeComponents localHTreeComp = createHashTree(
						DEFAULT_SEG_DATA_BLOCKS_COUNT, stores[j]);
				HTreeComponents remoteHTreeComp = createHashTree(
						DEFAULT_SEG_DATA_BLOCKS_COUNT, remoteStores[j]);

				for (int i = 1; i <= DEFAULT_SEG_DATA_BLOCKS_COUNT; i++) {
					localHTreeComp.storage.put(randomByteBuffer(),
							randomByteBuffer());
				}

				localHTreeComp.hTree.rebuildHashTrees(false);
				localHTreeComp.hTree.synch(1, remoteHTreeComp.hTree);
				remoteHTreeComp.hTree.rebuildHashTrees(false);

				for (int i = 0; i < DEFAULT_SEG_DATA_BLOCKS_COUNT; i++) {
					List<SegmentData> segBlock = remoteHTreeComp.hTree
							.getSegment(DEFAULT_TREE_ID, i);
					for (SegmentData sData : segBlock) {
						remoteHTreeComp.storage.remove(ByteBuffer.wrap(sData
								.getKey()));
					}
					remoteHTreeComp.hTree.rebuildHashTrees(false);
					localHTreeComp.hTree.synch(1, remoteHTreeComp.hTree);

					Assert.assertEquals(localHTreeComp.storage.localStorage,
							remoteHTreeComp.storage.localStorage);
				}
			}
		} finally {
			HashTreesImplTestUtils.closeStores(stores);
			HashTreesImplTestUtils.closeStores(remoteStores);
		}
	}

	@Test
	public void testUpdateTreeWithDifferingSegments() throws Exception {
		HashTreesStore[] stores = generateInMemoryAndPersistentStores();
		HashTreesStore[] remoteStores = generateInMemoryAndPersistentStores();

		try {
			for (int j = 1; j <= 1; j++) {
				HTreeComponents localHTreeComp = createHashTree(
						DEFAULT_SEG_DATA_BLOCKS_COUNT, stores[j]);
				HTreeComponents remoteHTreeComp = createHashTree(
						DEFAULT_SEG_DATA_BLOCKS_COUNT, remoteStores[j]);

				for (int i = 1; i <= DEFAULT_SEG_DATA_BLOCKS_COUNT; i++) {
					localHTreeComp.storage.put(randomByteBuffer(),
							randomByteBuffer());
				}

				localHTreeComp.hTree.rebuildHashTrees(false);
				localHTreeComp.hTree.synch(1, remoteHTreeComp.hTree);

				for (int i = 0; i < DEFAULT_SEG_DATA_BLOCKS_COUNT; i++) {
					List<SegmentData> segBlock = remoteHTreeComp.hTree
							.getSegment(DEFAULT_TREE_ID, i);
					for (SegmentData sData : segBlock) {
						localHTreeComp.storage.put(
								ByteBuffer.wrap(sData.getKey()),
								randomByteBuffer());
					}
					localHTreeComp.hTree.rebuildHashTrees(false);
					remoteHTreeComp.hTree.rebuildHashTrees(false);
					localHTreeComp.hTree.synch(1, remoteHTreeComp.hTree);

					Assert.assertEquals(localHTreeComp.storage.localStorage,
							remoteHTreeComp.storage.localStorage);
				}
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
		HashTreeSyncManagerStore syncMgrStore = generateInMemoryStore();

		try {
			HTreeComponents localHTreeComp = createHashTree(
					DEFAULT_SEG_DATA_BLOCKS_COUNT, store);
			HTreeComponents remoteHTreeComp = createHashTree(
					DEFAULT_SEG_DATA_BLOCKS_COUNT, remoteStore);
			HashTreesSyncManagerImpl hTreeManager = new HashTreesSyncManagerImpl(
					"test", remoteHTreeComp.hTree, treeIdProvider,
					syncMgrStore,
					HashTreesConstants.DEFAULT_HASH_TREE_SERVER_PORT_NO);

			hTreeManager.init();
			Thread.sleep(100);
			HashTrees thriftClient = HashTreesThriftClientProvider
					.getHashTreeRemoteClient(new ServerName("localhost",
							HashTreesConstants.DEFAULT_HASH_TREE_SERVER_PORT_NO));

			for (int i = 1; i <= DEFAULT_SEG_DATA_BLOCKS_COUNT; i++) {
				localHTreeComp.storage.put(randomByteBuffer(),
						randomByteBuffer());
			}

			localHTreeComp.hTree.rebuildHashTrees(false);
			localHTreeComp.hTree.synch(1, thriftClient);

			for (int i = 0; i < DEFAULT_SEG_DATA_BLOCKS_COUNT; i++) {
				List<SegmentData> segBlock = remoteHTreeComp.hTree.getSegment(
						DEFAULT_TREE_ID, i);
				for (SegmentData sData : segBlock) {
					localHTreeComp.storage.put(ByteBuffer.wrap(sData.getKey()),
							randomByteBuffer());
				}
				localHTreeComp.hTree.rebuildHashTrees(false);
				remoteHTreeComp.hTree.rebuildHashTrees(false);
				localHTreeComp.hTree.synch(1, thriftClient);

				Assert.assertEquals(localHTreeComp.storage.localStorage,
						remoteHTreeComp.storage.localStorage);
			}

			hTreeManager.shutdown();
		} finally {
			HashTreesImplTestUtils.closeStores(store);
			HashTreesImplTestUtils.closeStores(remoteStore);
		}
	}

	@Test
	public void testEnableNonBlockingCalls() {
		HashTreesStore htStore = generateInMemoryStore();
		StorageImplTest store = new StorageImplTest();
		HashTrees hTrees = new HashTreesImpl(DEFAULT_SEG_DATA_BLOCKS_COUNT,
				treeIdProvider, segIdProvider, htStore, store);
		Assert.assertFalse(hTrees.enableNonblockingOperations());
		Assert.assertTrue(hTrees.isNonBlockingCallsEnabled());
		Assert.assertTrue(hTrees.enableNonblockingOperations());
	}

	@Test
	public void testStop() {
		HashTreesStore htStore = generateInMemoryStore();
		StorageImplTest store = new StorageImplTest();
		HashTrees hTrees = new HashTreesImpl(DEFAULT_SEG_DATA_BLOCKS_COUNT,
				treeIdProvider, segIdProvider, htStore, store);
		hTrees.enableNonblockingOperations();
		Assert.assertTrue(hTrees.isNonBlockingCallsEnabled());
		hTrees.stop();
		Assert.assertFalse(hTrees.isNonBlockingCallsEnabled());
	}

	@Test
	public void testDisableNonBlockingCalls() {
		HashTreesStore htStore = generateInMemoryStore();
		StorageImplTest store = new StorageImplTest();
		HashTrees hTrees = new HashTreesImpl(DEFAULT_SEG_DATA_BLOCKS_COUNT,
				treeIdProvider, segIdProvider, htStore, store);
		Assert.assertTrue(hTrees.disableNonblockingOperations());
		Assert.assertFalse(hTrees.isNonBlockingCallsEnabled());
		hTrees.enableNonblockingOperations();
		Assert.assertFalse(hTrees.disableNonblockingOperations());
	}

	@Test
	public void testNonBlockingCalls() throws Exception {
		final CountDownLatch putArrivedEventLatch = new CountDownLatch(1);
		final CountDownLatch deleteArrivedEventLatch = new CountDownLatch(1);

		HashTreesStore htStore = new HashTreesMemStore() {

			@Override
			public void putSegmentData(long treeId, int segId, ByteBuffer key,
					ByteBuffer digest) {
				super.putSegmentData(treeId, segId, key, digest);
				putArrivedEventLatch.countDown();
			}

			@Override
			public void deleteSegmentData(long treeId, int segId, ByteBuffer key) {
				super.deleteSegmentData(treeId, segId, key);
				deleteArrivedEventLatch.countDown();
			}
		};
		StorageImplTest store = new StorageImplTest();
		HashTrees hTrees = new HashTreesImpl(DEFAULT_SEG_DATA_BLOCKS_COUNT,
				treeIdProvider, segIdProvider, htStore, store);
		hTrees.enableNonblockingOperations(10);

		ByteBuffer key = ByteBuffer.wrap("1".getBytes());
		ByteBuffer plainValue = ByteBuffer.wrap(randomBytes());
		ByteBuffer digestedValue = ByteBuffer.wrap(ByteUtils.sha1(plainValue
				.array()));

		hTrees.hPut(key, plainValue);
		putArrivedEventLatch.await(10000, TimeUnit.MILLISECONDS);
		SegmentData sd = htStore.getSegmentData(1, 1, key);
		Assert.assertNotNull(sd);
		Assert.assertEquals(digestedValue, sd.digest);

		hTrees.hRemove(key);
		deleteArrivedEventLatch.await(10000, TimeUnit.MILLISECONDS);
		sd = htStore.getSegmentData(1, 1, key);
		Assert.assertNull(sd);
	}
}
