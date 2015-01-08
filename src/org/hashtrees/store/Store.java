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

	void put(ByteBuffer key, ByteBuffer value) throws Exception;

	ByteBuffer remove(ByteBuffer key) throws Exception;

	Iterator<Pair<ByteBuffer, ByteBuffer>> iterator(long treeId);
}
