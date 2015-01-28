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
package org.hashtrees.usage;

import java.io.IOException;
import java.util.Arrays;

import junit.framework.Assert;

import org.hashtrees.HashTrees;
import org.hashtrees.HashTreesImpl;
import org.hashtrees.SimpleTreeIdProvider;
import org.hashtrees.SyncType;
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
	 * @param store
	 * @return
	 * @throws IOException
	 */
	public static HashTreesImpl buildHashTrees(Store store) throws IOException {
		HashTreesImpl hashTrees = new HashTreesImpl.Builder(store,
				new SimpleTreeIdProvider(), new HashTreesMemStore())
				.setEnabledNonBlockingCalls(false).setNoOfSegments(16).build();
		return hashTrees;
	}

	/**
	 * We need to register {@link HashTrees} with {@link Store} object. So that
	 * {@link Store} will forward the necessary calls to {@link HashTrees}. Note
	 * it is not compulsory to implement registerHashTrees method. As long as
	 * the {@link HashTrees} gets update operations in some way, its more than
	 * enough.
	 * 
	 * @param store
	 * @param hashTrees
	 */
	public static void registerHashTreesWithStore(Store store,
			HashTreesImpl hashTrees) {
		store.registerHashTrees(hashTrees);
	}

	/**
	 * The following function creates a {@link Store} and {@link HashTrees}
	 * object to support the {@link Store}.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static Pair<Store, HashTreesImpl> createAStoreWithEnabledHashTrees()
			throws IOException {
		Store store = buildStore();
		HashTreesImpl hashTrees = buildHashTrees(store);
		registerHashTreesWithStore(store, hashTrees);
		return Pair.create(store, hashTrees);
	}

	/**
	 * {@link HashTreesImpl} requires explicitly calling its
	 * {@link HashTreesImpl#start()} method.
	 * 
	 * @param hashTrees
	 */
	public static void start(HashTreesImpl... hashTrees) {
		for (HashTreesImpl hashTree : hashTrees)
			hashTree.start();
	}

	/**
	 * {@link HashTreesImpl} requires explicitly calling its
	 * {@link HashTreesImpl#stop()} method.
	 * 
	 * @param hashTrees
	 */
	public static void stop(HashTreesImpl... hashTrees) {
		for (HashTreesImpl hashTree : hashTrees)
			hashTree.stop();
	}

	/**
	 * Following test case creates a primary and backup database, with hashtree
	 * support. It does some modifications on the primary database, and using
	 * hash tree of the primary database synchs up the backup database.
	 * 
	 * @throws IOException
	 */
	@Test
	public void createPrimaryAndBackupStoresAndSynchThemUsingHashTrees()
			throws IOException {
		Pair<Store, HashTreesImpl> primary = createAStoreWithEnabledHashTrees();
		Pair<Store, HashTreesImpl> backup = createAStoreWithEnabledHashTrees();

		start(primary.getSecond(), backup.getSecond());

		byte[] keyBytes = "testKey".getBytes();
		byte[] valueBytes = "testValue".getBytes();
		primary.getFirst().put(keyBytes, valueBytes);

		primary.getSecond().rebuildHashTree(1, false);
		primary.getSecond().synch(1, backup.getSecond(), SyncType.UPDATE);
		Assert.assertTrue(Arrays.equals(backup.getFirst().get(keyBytes),
				valueBytes));

		stop(primary.getSecond(), backup.getSecond());
	}
}
