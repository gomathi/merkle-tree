package org.hashtrees.store;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.hashtrees.HashTrees;

/**
 * There could be cases where actual store is missing (key,value) pairs, or
 * having keys which are not supposed to be there. In these cases,
 * {@link HashTrees} has to directly talk to store interface.
 * 
 * {@link HashTrees} has to be provided with the implementation of this class.
 * 
 */
public interface Store {

	boolean contains(byte[] key) throws IOException;

	/**
	 * Adds the key and value to the local store. Also this call should forward
	 * the request to {@link HashTrees#hPut(ByteBuffer, ByteBuffer)} with the
	 * HashTrees object (If there is a hash trees registered already through
	 * {@link #registerHashTrees(HashTrees)}.
	 * 
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	void put(byte[] key, byte[] value) throws IOException;

	byte[] get(byte[] key) throws IOException;

	/**
	 * Removes the key from the local store. Also this call should forward the
	 * request to {@link HashTrees#hRemove(ByteBuffer)} with the HashTrees
	 * object (If there is a hash trees registered already through
	 * {@link #registerHashTrees(HashTrees)}.
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	void remove(byte[] key) throws IOException;

	/**
	 * This should return only (key,value) that are belonging to treeId. Used by
	 * {@link HashTrees#rebuildHashTree(long, boolean)} while rebuilding a
	 * specific hashtree.
	 * 
	 * @param treeId
	 * @return
	 * @throws IOException
	 */
	Iterator<Map.Entry<byte[], byte[]>> iterator(long treeId)
			throws IOException;

	/**
	 * Register hashtrees with storage implementation. On modifications to the
	 * storage, it should forward the calls to HashTrees.
	 * 
	 * @param hashTrees
	 */
	void registerHashTrees(HashTrees hashTrees);
}
