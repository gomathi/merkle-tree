package org.hashtrees.util;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
public class FlattenIterator<T> implements Iterator<T> {

	private final Iterator<Iterator<T>> itrs;
	private final Queue<Iterator<T>> intQue = new ArrayDeque<>(1);

	public FlattenIterator(Iterator<Iterator<T>> itrs) {
		this.itrs = itrs;
	}

	@Override
	public boolean hasNext() {
		loadNextElement();
		return !intQue.isEmpty();
	}

	@Override
	public T next() {
		if (!hasNext())
			throw new NoSuchElementException(
					"No more elements exist to return.");
		return intQue.peek().next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	private void loadNextElement() {
		while (intQue.isEmpty() || !intQue.peek().hasNext()) {
			intQue.poll();
			if (itrs.hasNext())
				intQue.add(itrs.next());
			else
				return;
		}
	}

}
