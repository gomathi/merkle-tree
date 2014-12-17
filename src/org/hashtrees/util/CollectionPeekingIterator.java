package org.hashtrees.util;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

import javax.annotation.concurrent.NotThreadSafe;

import com.google.common.collect.PeekingIterator;

@NotThreadSafe
public class CollectionPeekingIterator<T> implements PeekingIterator<T> {

	private final Queue<T> pQueue;
	private final Iterator<T> iItr;

	public CollectionPeekingIterator(Collection<T> collection) {
		pQueue = new ArrayDeque<T>(1);
		iItr = collection.iterator();
	}

	private void addElement() {
		if (iItr.hasNext())
			pQueue.add(iItr.next());
	}

	@Override
	public boolean hasNext() {
		if (pQueue.isEmpty())
			addElement();

		return pQueue.size() > 0;
	}

	@Override
	public T peek() {
		if (!hasNext())
			throw new NoSuchElementException(
					"No elements availale to be peeked.");
		return pQueue.peek();
	}

	@Override
	public T next() {
		if (!hasNext())
			throw new NoSuchElementException(
					"No elements availale to be returned.");
		return pQueue.remove();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(
				"This operation is not supported.");
	}
}
