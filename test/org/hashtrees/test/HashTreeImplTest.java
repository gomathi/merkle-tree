package org.hashtrees.test;

import static org.hashtrees.test.HashTreeImplTestUtils.DEFAULT_SEG_DATA_BLOCKS_COUNT;
import static org.hashtrees.test.HashTreeImplTestUtils.DEFAULT_TREE_ID;
import static org.hashtrees.test.HashTreeImplTestUtils.ROOT_NODE;
import static org.hashtrees.test.HashTreeImplTestUtils.createHashTree;
import static org.hashtrees.test.HashTreeImplTestUtils.generateInMemoryAndPersistentStores;
import static org.hashtrees.test.HashTreeImplTestUtils.generateInMemoryStore;
import static org.hashtrees.test.HashTreeImplTestUtils.randomByteBuffer;
import static org.hashtrees.test.HashTreeImplTestUtils.randomBytes;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.hashtrees.HashTree;
import org.hashtrees.HashTreeConstants;
import org.hashtrees.HashTreeImpl;
import org.hashtrees.storage.HashTreeStorage;
import org.hashtrees.synch.HashTreeSyncManagerImpl;
import org.hashtrees.synch.HashTreeThriftClientProvider;
import org.hashtrees.test.HashTreeImplTestUtils.HTreeComponents;
import org.hashtrees.test.HashTreeImplTestUtils.HashTreeIdProviderTest;
import org.hashtrees.test.HashTreeImplTestUtils.SegIdProviderTest;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;
import org.hashtrees.thrift.generated.ServerName;
import org.hashtrees.util.ByteUtils;
import org.junit.Assert;
import org.junit.Test;

public class HashTreeImplTest {

	private static final SegIdProviderTest segIdProvider = new SegIdProviderTest();
	private static final HashTreeIdProviderTest treeIdProvider = new HashTreeIdProviderTest();
	private static final int noOfSegDataBlocks = 1024;

	@Test
	public void testPut() throws Exception {

		HashTreeStorage[] stores = generateInMemoryAndPersistentStores(noOfSegDataBlocks);

		try {
			for (HashTreeStorage store : stores) {
				int treeId = 1;
				int segId = 1;
				String stringKey = "1";

				HTreeComponents components = createHashTree(noOfSegDataBlocks,
						treeIdProvider, segIdProvider, store);
				HashTree testTree = components.hTree;
				HashTreeStorage testTreeStorage = components.hTStorage;

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
			HashTreeImplTestUtils.closeStores(stores);
		}
	}

	@Test
	public void testRemove() throws Exception {

		HashTreeStorage[] stores = generateInMemoryAndPersistentStores(noOfSegDataBlocks);

		try {
			for (HashTreeStorage store : stores) {
				HTreeComponents components = createHashTree(noOfSegDataBlocks,
						treeIdProvider, segIdProvider, store);
				HashTree testTree = components.hTree;
				HashTreeStorage testTreeStorage = components.hTStorage;

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
			HashTreeImplTestUtils.closeStores(stores);
		}
	}

	@Test
	public void testUpdateSegmentHashesTest() throws Exception {

		int tesNoOfSegDataBlocks = 2;
		HashTreeStorage[] stores = generateInMemoryAndPersistentStores(tesNoOfSegDataBlocks);

		try {
			for (HashTreeStorage store : stores) {
				HTreeComponents components = createHashTree(
						tesNoOfSegDataBlocks, treeIdProvider, segIdProvider,
						store);
				HashTree testTree = components.hTree;
				HashTreeStorage testTreeStorage = components.hTStorage;

				ByteBuffer key = ByteBuffer.wrap("1".getBytes());
				ByteBuffer value = ByteBuffer.wrap(randomBytes());
				testTree.hPut(key, value);

				testTree.rebuildHashTrees(false);

				StringBuffer sb = new StringBuffer();
				ByteBuffer digest = ByteBuffer.wrap(ByteUtils.sha1(value
						.array()));
				sb.append(HashTreeImpl.getHexString(key, digest) + "\n");
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
			HashTreeImplTestUtils.closeStores(stores);
		}
	}

	@Test
	public void testUpdateWithEmptyTree() throws Exception {
		HashTreeStorage[] stores = generateInMemoryAndPersistentStores(DEFAULT_SEG_DATA_BLOCKS_COUNT);
		HashTreeStorage[] remoteStores = generateInMemoryAndPersistentStores(DEFAULT_SEG_DATA_BLOCKS_COUNT);

		try {
			for (int j = 0; j <= 1; j++) {
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
			HashTreeImplTestUtils.closeStores(stores);
			HashTreeImplTestUtils.closeStores(remoteStores);
		}
	}

	@Test
	public void testUpdateTreeWithMissingBlocksInLocal() throws Exception {

		HashTreeStorage[] stores = generateInMemoryAndPersistentStores(DEFAULT_SEG_DATA_BLOCKS_COUNT);
		HashTreeStorage[] remoteStores = generateInMemoryAndPersistentStores(DEFAULT_SEG_DATA_BLOCKS_COUNT);

		try {
			for (int j = 0; j <= 1; j++) {
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

					if (!localHTreeComp.storage.localStorage
							.equals(remoteHTreeComp.storage.localStorage)) {
						System.out.println(localHTreeComp.storage.localStorage);
						System.out
								.println(remoteHTreeComp.storage.localStorage);
					}

					Assert.assertEquals(localHTreeComp.storage.localStorage,
							remoteHTreeComp.storage.localStorage);
				}

				Assert.assertEquals(0,
						localHTreeComp.storage.localStorage.size());
				Assert.assertEquals(0,
						remoteHTreeComp.storage.localStorage.size());
			}
		} finally {
			HashTreeImplTestUtils.closeStores(stores);
			HashTreeImplTestUtils.closeStores(remoteStores);
		}
	}

	@Test
	public void testUpdateTreeWithMissingBlocksInRemote() throws Exception {

		HashTreeStorage[] stores = generateInMemoryAndPersistentStores(DEFAULT_SEG_DATA_BLOCKS_COUNT);
		HashTreeStorage[] remoteStores = generateInMemoryAndPersistentStores(DEFAULT_SEG_DATA_BLOCKS_COUNT);

		try {
			for (int j = 0; j <= 1; j++) {
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
			HashTreeImplTestUtils.closeStores(stores);
			HashTreeImplTestUtils.closeStores(remoteStores);
		}
	}

	@Test
	public void testUpdateTreeWithDifferingSegments() throws Exception {
		HashTreeStorage[] stores = generateInMemoryAndPersistentStores(DEFAULT_SEG_DATA_BLOCKS_COUNT);
		HashTreeStorage[] remoteStores = generateInMemoryAndPersistentStores(DEFAULT_SEG_DATA_BLOCKS_COUNT);

		try {
			for (int j = 0; j <= 1; j++) {
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
			HashTreeImplTestUtils.closeStores(stores);
			HashTreeImplTestUtils.closeStores(remoteStores);
		}
	}

	@Test
	public void testHashTreeServerAndClient() throws Exception {
		HashTreeStorage store = generateInMemoryStore(DEFAULT_SEG_DATA_BLOCKS_COUNT);
		HashTreeStorage remoteStore = generateInMemoryStore(DEFAULT_SEG_DATA_BLOCKS_COUNT);

		try {
			HTreeComponents localHTreeComp = createHashTree(
					DEFAULT_SEG_DATA_BLOCKS_COUNT, store);
			HTreeComponents remoteHTreeComp = createHashTree(
					DEFAULT_SEG_DATA_BLOCKS_COUNT, remoteStore);
			HashTreeSyncManagerImpl hTreeManager = new HashTreeSyncManagerImpl(
					"test", remoteHTreeComp.hTree, treeIdProvider,
					HashTreeConstants.DEFAULT_HASH_TREE_SERVER_PORT_NO);

			hTreeManager.init();
			Thread.sleep(100);
			HashTree thriftClient = HashTreeThriftClientProvider
					.getHashTreeRemoteClient(new ServerName("localhost",
							HashTreeConstants.DEFAULT_HASH_TREE_SERVER_PORT_NO));

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
			HashTreeImplTestUtils.closeStores(store);
			HashTreeImplTestUtils.closeStores(remoteStore);
		}
	}
}
