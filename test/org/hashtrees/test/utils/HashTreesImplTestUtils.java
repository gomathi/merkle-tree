package org.hashtrees.test.utils;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.hashtrees.HashTrees;
import org.hashtrees.HashTreesIdProvider;
import org.hashtrees.HashTreesImpl;
import org.hashtrees.ModuloSegIdProvider;
import org.hashtrees.SegmentIdProvider;
import org.hashtrees.store.HashTreeSyncManagerStore;
import org.hashtrees.store.HashTreesMemStore;
import org.hashtrees.store.HashTreesPersistentStore;
import org.hashtrees.store.HashTreesStore;
import org.hashtrees.store.Store;
import org.hashtrees.util.Pair;

public class HashTreesImplTestUtils {

	private static final Random RANDOM = new Random(System.currentTimeMillis());
	public static final int ROOT_NODE = 0;
	public static final int DEFAULT_TREE_ID = 1;
	public static final int DEFAULT_SEG_DATA_BLOCKS_COUNT = 1 << 5;
	public static final int DEFAULT_HTREE_SERVER_PORT_NO = 11111;
	public static final SegIdProviderTest SEG_ID_PROVIDER = new SegIdProviderTest();
	public static final HashTreeIdProviderTest TREE_ID_PROVIDER = new HashTreeIdProviderTest();

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

	/**
	 * Default HashTreeIdProvider which always returns treeId as 1.
	 * 
	 */
	public static class HashTreeIdProviderTest implements HashTreesIdProvider {

		public static final long TREE_ID = 1;
		private final List<Long> treeIds = new ArrayList<Long>();

		public HashTreeIdProviderTest() {
			treeIds.add(TREE_ID);
		}

		@Override
		public long getTreeId(ByteBuffer key) {
			return TREE_ID;
		}

		@Override
		public Iterator<Long> getAllPrimaryTreeIds() {
			return treeIds.iterator();
		}
	}

	public static class StoreImplTest implements Store {

		public ConcurrentHashMap<ByteBuffer, ByteBuffer> localStore = new ConcurrentHashMap<ByteBuffer, ByteBuffer>();
		public volatile HashTrees hashTree;

		public void setHashTree(final HashTrees hashTree) {
			this.hashTree = hashTree;
		}

		public HashTrees getHashTree() {
			return hashTree;
		}

		@Override
		public ByteBuffer get(ByteBuffer key) {
			ByteBuffer intKey = ByteBuffer.wrap(key.array());
			return localStore.get(intKey);
		}

		@Override
		public void put(ByteBuffer key, ByteBuffer value) throws Exception {
			ByteBuffer intKey = ByteBuffer.wrap(key.array());
			ByteBuffer intValue = ByteBuffer.wrap(value.array());
			localStore.put(intKey, intValue);
			if (hashTree != null)
				hashTree.hPut(intKey, intValue);
		}

		@Override
		public ByteBuffer remove(ByteBuffer key) throws Exception {
			ByteBuffer intKey = ByteBuffer.wrap(key.array());
			ByteBuffer value = localStore.remove(intKey);
			if (hashTree != null) {
				hashTree.hRemove(intKey);
				return value;
			}
			return null;
		}

		@Override
		public Iterator<Pair<ByteBuffer, ByteBuffer>> iterator(long treeId) {
			List<Pair<ByteBuffer, ByteBuffer>> result = new ArrayList<Pair<ByteBuffer, ByteBuffer>>();
			for (Map.Entry<ByteBuffer, ByteBuffer> entry : localStore
					.entrySet())
				result.add(Pair.create(entry.getKey(), entry.getValue()));
			return result.iterator();
		}

	}

	public static class HTreeComponents {

		public final HashTreesStore hTStore;
		public final StoreImplTest store;
		public final HashTrees hTree;

		public HTreeComponents(final HashTreesStore hTStore,
				final StoreImplTest store, final HashTrees hTree) {
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
		StoreImplTest store = new StoreImplTest();
		HashTrees hTree = new HashTreesImpl(noOfSegDataBlocks, treeIdProv,
				segIdPro, hTStore, store);
		store.setHashTree(hTree);
		hTree.rebuildHashTrees(true);
		return new HTreeComponents(hTStore, store, hTree);
	}

	public static HTreeComponents createHashTree(int noOfSegments,
			final HashTreesStore hTStore) throws Exception {
		StoreImplTest store = new StoreImplTest();
		ModuloSegIdProvider segIdProvider = new ModuloSegIdProvider(
				noOfSegments);
		HashTrees hTree = new HashTreesImpl(noOfSegments, TREE_ID_PROVIDER,
				segIdProvider, hTStore, store);
		store.setHashTree(hTree);
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

	public static HashTreeSyncManagerStore[] generateInMemoryAndPersistentSyncMgrStores()
			throws Exception {
		HashTreeSyncManagerStore[] stores = new HashTreeSyncManagerStore[2];
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

	public static void closeStores(HashTreeSyncManagerStore... stores) {
		for (HashTreeSyncManagerStore store : stores) {
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
