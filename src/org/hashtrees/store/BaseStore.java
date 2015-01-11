package org.hashtrees.store;

import java.nio.ByteBuffer;

import org.hashtrees.HashTrees;

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
