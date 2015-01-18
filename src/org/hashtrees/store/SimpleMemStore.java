package org.hashtrees.store;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hashtrees.util.Pair;

/**
 * In memory implementation of {@link Store}. Intended to be used in unit tests.
 * 
 * Note the {@link #hashCode()} is calculated by using the internal map of key
 * values. It does not use any other values to calculate hashCode.
 * 
 */
public class SimpleMemStore extends BaseStore {

	private final ConcurrentHashMap<ByteBuffer, ByteBuffer> kvMap = new ConcurrentHashMap<>();

	@Override
	public byte[] get(byte[] key) {
		ByteBuffer intKey = ByteBuffer.wrap(key);
		return (intKey != null) ? kvMap.get(intKey).array() : null;
	}

	@Override
	public void put(byte[] key, byte[] value) throws Exception {
		ByteBuffer intKey = ByteBuffer.wrap(key);
		ByteBuffer intValue = ByteBuffer.wrap(value);
		kvMap.put(intKey, intValue);
		super.put(key, value);
	}

	@Override
	public void remove(byte[] key) throws Exception {
		ByteBuffer intKey = ByteBuffer.wrap(key);
		kvMap.remove(intKey);
		super.remove(key);
	}

	/**
	 * Just returns all the key values in the store, irrespective of which
	 * treeId it belongs to.
	 */
	@Override
	public Iterator<Pair<byte[], byte[]>> iterator(long treeId) {
		return iterator();
	}

	@Override
	public int hashCode() {
		return kvMap.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof SimpleMemStore))
			return false;
		SimpleMemStore that = (SimpleMemStore) other;
		return this.kvMap.equals(that.kvMap);
	}

	@Override
	public Iterator<Pair<byte[], byte[]>> iterator() {
		List<Pair<byte[], byte[]>> result = new ArrayList<>();
		for (Map.Entry<ByteBuffer, ByteBuffer> entry : kvMap.entrySet())
			result.add(Pair.create(entry.getKey().array(), entry.getValue()
					.array()));
		return result.iterator();
	}

	@Override
	public boolean contains(byte[] key) {
		ByteBuffer intKey = ByteBuffer.wrap(key);
		return kvMap.containsKey(intKey);
	}

}