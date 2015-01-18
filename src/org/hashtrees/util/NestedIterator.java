package org.hashtrees.util;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class NestedIterator<T> implements Iterator<T> {

	private final Queue<Iterator<T>> itrQueue;

	public NestedIterator(List<Iterator<T>> iterators) {
		itrQueue = new ArrayDeque<>(iterators);
	}

	@Override
	public boolean hasNext() {
		while (!itrQueue.isEmpty() && !itrQueue.peek().hasNext())
			itrQueue.remove();
		return !itrQueue.isEmpty() && itrQueue.peek().hasNext();
	}

	@Override
	public T next() {
		if (!hasNext())
			throw new NoSuchElementException("No more elements exist.");
		return itrQueue.peek().next();
	}
}
