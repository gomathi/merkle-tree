package org.hashtrees.util;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.base.Function;

@NotThreadSafe
public class DataIterator<T> implements Iterator<T> {

	private final Queue<T> dataQueue = new ArrayDeque<>(1);
	private final byte[] prefixKey;
	private final Function<Map.Entry<byte[], byte[]>, T> converter;
	private final Iterator<Map.Entry<byte[], byte[]>> kvBytesItr;

	public DataIterator(byte[] prefixKey,
			Function<Map.Entry<byte[], byte[]>, T> converter,
			Iterator<Map.Entry<byte[], byte[]>> kvBytesItr) {
		this.prefixKey = prefixKey;
		this.converter = converter;
		this.kvBytesItr = kvBytesItr;
	}

	@Override
	public boolean hasNext() {
		loadNextElement();
		return dataQueue.size() > 0;
	}

	@Override
	public T next() {
		if (!hasNext())
			throw new NoSuchElementException("No more elements exist.");
		return dataQueue.remove();
	}

	private void loadNextElement() {
		if (dataQueue.isEmpty() && kvBytesItr.hasNext()) {
			Map.Entry<byte[], byte[]> entry = kvBytesItr.next();
			if (ByteUtils.compareTo(prefixKey, 0, prefixKey.length,
					entry.getKey(), 0, prefixKey.length) != 0)
				return;
			dataQueue.add(converter.apply(entry));
		}
	}
}