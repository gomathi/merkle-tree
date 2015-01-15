package org.hashtrees.store;

import java.nio.ByteBuffer;
import java.util.Iterator;

import org.hashtrees.HashTrees;
import org.hashtrees.util.Pair;

/**
 * There could be cases where actual store is missing (key,value) pairs, or
 * having keys which are not supposed to be there. In these cases,
 * {@link HashTrees} has to directly talk to store interface.
 * 
 * {@link HashTrees} has to be provided with the implementation of this class.
 * 
 */
public interface Store {

	ByteBuffer get(ByteBuffer key);

	boolean contains(ByteBuffer key);

	/**
	 * Adds the key and value to the local store. Also this call should forward
	 * the request to {@link HashTrees#hPut(ByteBuffer, ByteBuffer)} with the
	 * HashTrees object (If there is a hash trees registered already through
	 * {@link #registerHashTrees(HashTrees)}.
	 * 
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	void put(ByteBuffer key, ByteBuffer value) throws Exception;

	/**
	 * Removes the key from the local store. Also this call should forward the
	 * request to {@link HashTrees#hRemove(ByteBuffer)} with the HashTrees
	 * object (If there is a hash trees registered already through
	 * {@link #registerHashTrees(HashTrees)}.
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	void remove(ByteBuffer key) throws Exception;

	/**
	 * This should return only (key,value) that are belonging to treeId. Used by
	 * {@link HashTrees#rebuildHashTree(long, boolean)} while rebuilding a
	 * specific hashtree.
	 * 
	 * @param treeId
	 * @return
	 */
	Iterator<Pair<ByteBuffer, ByteBuffer>> iterator(long treeId);

	/**
	 * Returns all keyValue pairs that are part of this store.Used by
	 * {@link HashTrees#rebuildHashTree(long, boolean)} while rebuilding all
	 * hashtrees.
	 * 
	 * @return
	 */
	Iterator<Pair<ByteBuffer, ByteBuffer>> iterator();

	void registerHashTrees(HashTrees hashTrees);
}
