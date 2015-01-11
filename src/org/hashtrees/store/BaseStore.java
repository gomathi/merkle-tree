package org.hashtrees.store;

import java.nio.ByteBuffer;

import org.hashtrees.HashTrees;

/**
 * The implementations of {@link Store} can extend this class, so those dont
 * have to implement {@link #registerHashTrees(HashTrees)} also forwarding the
 * calls to {@link HashTrees#hPut(ByteBuffer, ByteBuffer)} and
 * {@link HashTrees#hRemove(ByteBuffer)}.
 * 
 * Look at {@link SimpleMemStore} to know how this class is being used.
 */
public abstract class BaseStore implements Store {

	private volatile HashTrees hashTrees;

	@Override
	public void put(ByteBuffer key, ByteBuffer value) throws Exception {
		if (hashTrees != null)
			hashTrees.hPut(key, value);
	}

	@Override
	public void remove(ByteBuffer key) throws Exception {
		if (hashTrees != null)
			hashTrees.hRemove(key);
	}

	@Override
	public void registerHashTrees(HashTrees hashTrees) {
		this.hashTrees = hashTrees;
	}
}
