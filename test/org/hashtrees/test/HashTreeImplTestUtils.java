package org.hashtrees.test;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.FileUtils;
import org.hashtrees.HashTree;
import org.hashtrees.HashTreeIdProvider;
import org.hashtrees.HashTreeImpl;
import org.hashtrees.SegmentIdProvider;
import org.hashtrees.storage.HashTreeMemStorage;
import org.hashtrees.storage.HashTreePersistentStorage;
import org.hashtrees.storage.HashTreeStorage;
import org.hashtrees.storage.Storage;
import org.hashtrees.util.Pair;

public class HashTreeImplTestUtils {

	private static final Random RANDOM = new Random(System.currentTimeMillis());
	public static final int ROOT_NODE = 0;
	public static final int DEFAULT_TREE_ID = 1;
	public static final int DEFAULT_SEG_DATA_BLOCKS_COUNT = 1 << 10;
	public static final int DEFAULT_HTREE_SERVER_PORT_NO = 11111;

	/**
	 * Default SegId provider which expects the key to be an integer wrapped as
	 * bytes.
	 * 
	 */
	public static class SegIdProviderTest implements SegmentIdProvider {

		@Override
		public int getSegmentId(ByteBuffer key) {
			try {
				return Integer.parseInt(new String(key.array(), "UTF-8"));
			} catch (NumberFormatException | UnsupportedEncodingException e) {
				throw new IllegalArgumentException(
						"Exception occurred while converting the string");
			}
		}

	}

	/**
	 * Default HashTreeIdProvider which always returns treeId as 1.
	 * 
	 */
	public static class HashTreeIdProviderTest implements HashTreeIdProvider {

		private final List<Long> treeIds = new ArrayList<Long>();

		public HashTreeIdProviderTest() {
			treeIds.add(1l);
		}

		@Override
		public long getTreeId(ByteBuffer key) {
			return 1;
		}

		@Override
		public Iterator<Long> getAllPrimaryTreeIds() {
			return treeIds.iterator();
		}
	}

	public static class StorageImplTest implements Storage {

		final ConcurrentHashMap<ByteBuffer, ByteBuffer> localStorage = new ConcurrentHashMap<ByteBuffer, ByteBuffer>();
		volatile HashTree hashTree;

		public void setHashTree(final HashTree hashTree) {
			this.hashTree = hashTree;
		}

		@Override
		public ByteBuffer get(ByteBuffer key) {
			return localStorage.get(key);
		}

		@Override
		public void put(ByteBuffer key, ByteBuffer value) throws Exception {
			localStorage.put(key, value);
			if (hashTree != null)
				hashTree.hPut(key, value);
		}

		@Override
		public ByteBuffer remove(ByteBuffer key) throws Exception {
			ByteBuffer value = localStorage.remove(key);
			if (hashTree != null) {
				hashTree.hRemove(key);
				return value;
			}
			return null;
		}

		@Override
		public Iterator<Pair<ByteBuffer, ByteBuffer>> iterator() {
			List<Pair<ByteBuffer, ByteBuffer>> result = new ArrayList<Pair<ByteBuffer, ByteBuffer>>();
			for (Map.Entry<ByteBuffer, ByteBuffer> entry : localStorage
					.entrySet())
				result.add(Pair.create(entry.getKey(), entry.getValue()));
			return result.iterator();
		}

	}

	public static class HTreeComponents {

		public final HashTreeStorage hTStorage;
		public final StorageImplTest storage;
		public final HashTree hTree;

		public HTreeComponents(final HashTreeStorage hTStorage,
				final StorageImplTest storage, final HashTree hTree) {
			this.hTStorage = hTStorage;
			this.storage = storage;
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
			final HashTreeIdProvider treeIdProv,
			final SegmentIdProvider segIdPro, final HashTreeStorage hTStorage)
			throws Exception {
		StorageImplTest storage = new StorageImplTest();
		HashTree hTree = new HashTreeImpl(noOfSegDataBlocks, treeIdProv,
				segIdPro, hTStorage, storage);
		hTree.rebuildHashTrees(true);
		return new HTreeComponents(hTStorage, storage, hTree);
	}

	public static HTreeComponents createHashTree(int noOfSegments,
			final HashTreeStorage hTStorage) throws Exception {
		HashTreeIdProvider treeIdProvider = new HashTreeIdProviderTest();
		StorageImplTest storage = new StorageImplTest();
		HashTree hTree = new HashTreeImpl(noOfSegments, treeIdProvider,
				hTStorage, storage);
		storage.setHashTree(hTree);
		hTree.rebuildHashTrees(true);
		return new HTreeComponents(hTStorage, storage, hTree);
	}

	public static HashTreeStorage generateInMemoryStore(int noOfSegDataBlocks) {
		return new HashTreeMemStorage(noOfSegDataBlocks);
	}

	private static HashTreeStorage generatePersistentStore(int noOfSegDataBlocks)
			throws Exception {
		return new HashTreePersistentStorage(randomDirName(), noOfSegDataBlocks);
	}

	public static HashTreeStorage[] generateInMemoryAndPersistentStores(
			int noOfSegDataBlocks) throws Exception {
		HashTreeStorage[] stores = new HashTreeStorage[2];
		stores[0] = generateInMemoryStore(noOfSegDataBlocks);
		stores[1] = generatePersistentStore(noOfSegDataBlocks);
		return stores;
	}

	public static void closeStores(HashTreeStorage... stores) {
		for (HashTreeStorage store : stores) {
			if (store instanceof HashTreePersistentStorage) {
				HashTreePersistentStorage pStore = (HashTreePersistentStorage) store;
				FileUtils.deleteQuietly(new File(pStore.getDbDir()));
			}
		}
	}
}
