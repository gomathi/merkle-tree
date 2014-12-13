package org.hashtrees.storage;

import java.nio.ByteBuffer;
import java.util.Iterator;

import org.hashtrees.util.Pair;

public interface Storage {

	ByteBuffer get(ByteBuffer key);

	void put(ByteBuffer key, ByteBuffer value) throws Exception;

	ByteBuffer remove(ByteBuffer key) throws Exception;

	Iterator<Pair<ByteBuffer, ByteBuffer>> iterator();
}
