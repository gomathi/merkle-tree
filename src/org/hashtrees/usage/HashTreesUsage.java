package org.hashtrees.usage;

import java.nio.ByteBuffer;
import java.util.Arrays;

import junit.framework.Assert;

import org.hashtrees.HashTrees;
import org.hashtrees.HashTreesImpl;
import org.hashtrees.SimpleTreeIdProvider;
import org.hashtrees.store.HashTreesMemStore;
import org.hashtrees.store.SimpleMemStore;
import org.hashtrees.store.Store;
import org.hashtrees.util.Pair;
import org.junit.Test;

/**
 * This class provides an example of how to build a {@link HashTrees} object,
 * and using it.
 * 
 * Read the following functions sequentially.
 * 
 */
public class HashTreesUsage {

	/**
	 * First of all a {@link HashTrees} is maintained for set of data(for
	 * example a database). Using hash trees of primary and secondary database,
	 * primary and secondary data can be quickly synchronized with little
	 * network transfers. For more information, look at {@link http
	 * ://en.wikipedia.org/wiki/Merkle_tree}.
	 * 
	 * As a first step, hashtree requires a store object. On store object,
	 * hashtrees will perform read/write operations.(For example during synch of
	 * primary and secondary). You need to implement {@link Store} interface.
	 * 
	 * The following function just implements a in memory version of
	 * {@link Store} for this example.
	 * 
	 * @return
	 */
	public static Store buildStore() {
		return new SimpleMemStore();
	}

	/**
	 * Once you have the {@link Store} object, you need to build the instance of
	 * {@link HashTrees}. {@link HashTreesImpl} is the actual implementation,
	 * and it is also provided with {@link HashTreesImpl.Builder} to easily
	 * build the instance of {@link HashTreesImpl}.
	 * 
	 * Following example creates the hash trees with in memory store
	 * implementation.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static HashTreesImpl buildHashTrees(Store store) throws Exception {
		HashTreesImpl hashTrees = new HashTreesImpl.Builder(store,
				new SimpleTreeIdProvider(), new HashTreesMemStore())
				.setNoOfSegments(16).build();
		return hashTrees;
	}

	/**
	 * We need to register hashtrees with store object. So that store will
	 * forward the necessary calls to hashtrees.
	 * 
	 * @param hashTrees
	 */
	public static void registerHashTreesWithStore(Store store,
			HashTreesImpl hashTrees) {
		store.registerHashTrees(hashTrees);
	}

	/**
	 * The following function creates a store and hash trees object to support
	 * the store.
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Pair<Store, HashTreesImpl> createAStoreWithEnabledHashTrees()
			throws Exception {
		Store store = buildStore();
		HashTreesImpl hashTrees = buildHashTrees(store);
		registerHashTreesWithStore(store, hashTrees);
		return Pair.create(store, hashTrees);
	}

	/**
	 * Stops all the operations on {@link HashTreesImpl}
	 * 
	 * @param hashTrees
	 */
	public static void stop(HashTreesImpl hashTrees) {
		hashTrees.stop();
	}

	/**
	 * Following test case creates a primary and backup database, with hashtree
	 * support. It does some modifications on the primary database, and using
	 * hash tree of the primary database to synch backup database.
	 * 
	 * @throws Exception
	 */
	@Test
	public void createPrimaryAndBackupStoresAndSynchThemUsingHashTrees()
			throws Exception {
		Pair<Store, HashTreesImpl> primary = createAStoreWithEnabledHashTrees();
		Pair<Store, HashTreesImpl> backup = createAStoreWithEnabledHashTrees();

		ByteBuffer key = ByteBuffer.wrap("testKey".getBytes());
		ByteBuffer value = ByteBuffer.wrap("testValue".getBytes());
		primary.getFirst().put(key, value);

		primary.getSecond().rebuildHashTree(1, true);
		primary.getSecond().synch(1, backup.getSecond());
		Assert.assertTrue(Arrays.equals(backup.getFirst().get(key).array(),
				value.array()));

		stop(primary.getSecond());
		stop(backup.getSecond());
	}
}
