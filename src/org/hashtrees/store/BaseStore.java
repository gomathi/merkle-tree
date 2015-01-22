package org.hashtrees.store;

import java.io.IOException;
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
	public void put(byte[] key, byte[] value) throws IOException {
		if (hashTrees != null)
			hashTrees.hPut(ByteBuffer.wrap(key), ByteBuffer.wrap(value));
	}

	@Override
	public void remove(byte[] key) throws IOException {
		if (hashTrees != null)
			hashTrees.hRemove(ByteBuffer.wrap(key));
	}

	@Override
	public void registerHashTrees(HashTrees hashTrees) {
		this.hashTrees = hashTrees;
	}
}
