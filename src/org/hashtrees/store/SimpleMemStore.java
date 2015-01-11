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
 */
public class SimpleMemStore extends BaseStore {

	private final ConcurrentHashMap<ByteBuffer, ByteBuffer> kvMap = new ConcurrentHashMap<ByteBuffer, ByteBuffer>();

	@Override
	public ByteBuffer get(ByteBuffer key) {
		ByteBuffer intKey = ByteBuffer.wrap(key.array());
		return kvMap.get(intKey);
	}

	@Override
	public void put(ByteBuffer key, ByteBuffer value) throws Exception {
		ByteBuffer intKey = ByteBuffer.wrap(key.array());
		ByteBuffer intValue = ByteBuffer.wrap(value.array());
		kvMap.put(intKey, intValue);
		super.put(intKey, intValue);
	}

	@Override
	public void remove(ByteBuffer key) throws Exception {
		ByteBuffer intKey = ByteBuffer.wrap(key.array());
		kvMap.remove(intKey);
		super.remove(intKey);
	}

	/**
	 * Just returns all the key values in the store, irrespective of which
	 * treeId it belongs to.
	 */
	@Override
	public Iterator<Pair<ByteBuffer, ByteBuffer>> iterator(long treeId) {
		return iterator();
	}

	@Override
	public int hashCode() {
		return kvMap.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (!(other instanceof SimpleMemStore))
			return false;
		SimpleMemStore that = (SimpleMemStore) other;
		return this.kvMap.equals(that.kvMap);
	}

	@Override
	public Iterator<Pair<ByteBuffer, ByteBuffer>> iterator() {
		List<Pair<ByteBuffer, ByteBuffer>> result = new ArrayList<Pair<ByteBuffer, ByteBuffer>>();
		for (Map.Entry<ByteBuffer, ByteBuffer> entry : kvMap.entrySet())
			result.add(Pair.create(entry.getKey(), entry.getValue()));
		return result.iterator();
	}

}