package org.hashtrees.test.utils;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.hashtrees.HashTrees;
import org.hashtrees.HashTreesIdProvider;
import org.hashtrees.HashTreesImpl;
import org.hashtrees.ModuloSegIdProvider;
import org.hashtrees.SegmentIdProvider;
import org.hashtrees.SimpleTreeIdProvider;
import org.hashtrees.store.HashTreesManagerStore;
import org.hashtrees.store.HashTreesMemStore;
import org.hashtrees.store.HashTreesPersistentStore;
import org.hashtrees.store.HashTreesStore;
import org.hashtrees.store.SimpleMemStore;

public class HashTreesImplTestUtils {

	private static final Random RANDOM = new Random(System.currentTimeMillis());
	public static final int ROOT_NODE = 0;
	public static final int DEFAULT_TREE_ID = 1;
	public static final int DEFAULT_SEG_DATA_BLOCKS_COUNT = 1 << 5;
	public static final int DEFAULT_HTREE_SERVER_PORT_NO = 11111;
	public static final SegIdProviderTest SEG_ID_PROVIDER = new SegIdProviderTest();
	public static final SimpleTreeIdProvider TREE_ID_PROVIDER = new SimpleTreeIdProvider();

	/**
	 * Default SegId provider which expects the key to be an integer wrapped as
	 * bytes.
	 * 
	 */
	public static class SegIdProviderTest implements SegmentIdProvider {

		@Override
		public int getSegmentId(ByteBuffer key) {
			try {
				ByteBuffer bb = ByteBuffer.wrap(key.array());
				return bb.getInt();
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(
						"Exception occurred while converting the string");
			}
		}

	}

	public static class HTreeComponents {

		public final HashTreesStore hTStore;
		public final SimpleMemStore store;
		public final HashTrees hTree;

		public HTreeComponents(final HashTreesStore hTStore,
				final SimpleMemStore store, final HashTrees hTree) {
			this.hTStore = hTStore;
			this.store = store;
			this.hTree = hTree;
		}
	}

	public static byte[] randomBytes() {
		byte[] emptyBuffer = new byte[8];
		RANDOM.nextBytes(emptyBuffer);
		return emptyBuffer;
	}

	public static ByteBuffer randomByteBuffer() {
		byte[] random = new byte[8];
		RANDOM.nextBytes(random);
		return ByteBuffer.wrap(random);
	}

	public static String randomDirName() {
		return "/tmp/test/random" + RANDOM.nextInt();
	}

	public static HTreeComponents createHashTree(int noOfSegDataBlocks,
			final HashTreesIdProvider treeIdProv,
			final SegmentIdProvider segIdPro, final HashTreesStore hTStore)
			throws Exception {
		SimpleMemStore store = new SimpleMemStore();
		HashTreesImpl hTree = new HashTreesImpl(noOfSegDataBlocks, treeIdProv,
				segIdPro, hTStore, store);
		store.registerHashTrees(hTree);
		hTree.rebuildHashTrees(true);
		return new HTreeComponents(hTStore, store, hTree);
	}

	public static HTreeComponents createHashTree(int noOfSegments,
			final HashTreesStore hTStore) throws Exception {
		SimpleMemStore store = new SimpleMemStore();
		ModuloSegIdProvider segIdProvider = new ModuloSegIdProvider(
				noOfSegments);
		HashTrees hTree = new HashTreesImpl(noOfSegments, TREE_ID_PROVIDER,
				segIdProvider, hTStore, store);
		store.registerHashTrees(hTree);
		hTree.rebuildHashTrees(true);
		return new HTreeComponents(hTStore, store, hTree);
	}

	public static HashTreesMemStore generateInMemoryStore() {
		return new HashTreesMemStore();
	}

	public static HashTreesPersistentStore generatePersistentStore()
			throws Exception {
		return new HashTreesPersistentStore(randomDirName());
	}

	public static HashTreesManagerStore[] generateInMemoryAndPersistentSyncMgrStores()
			throws Exception {
		HashTreesManagerStore[] stores = new HashTreesManagerStore[2];
		stores[0] = generateInMemoryStore();
		stores[1] = generatePersistentStore();
		return stores;
	}

	public static HashTreesStore[] generateInMemoryAndPersistentStores()
			throws Exception {
		HashTreesStore[] stores = new HashTreesStore[2];
		stores[0] = generateInMemoryStore();
		stores[1] = generatePersistentStore();
		return stores;
	}

	public static void closeStores(HashTreesManagerStore... stores) {
		for (HashTreesManagerStore store : stores) {
			if (store instanceof HashTreesPersistentStore) {
				HashTreesPersistentStore pStore = (HashTreesPersistentStore) store;
				FileUtils.deleteQuietly(new File(pStore.getDbDir()));
			}
		}
	}

	public static void closeStores(HashTreesStore... stores) {
		for (HashTreesStore store : stores) {
			if (store instanceof HashTreesPersistentStore) {
				HashTreesPersistentStore pStore = (HashTreesPersistentStore) store;
				FileUtils.deleteQuietly(new File(pStore.getDbDir()));
			}
		}
	}
}
