package org.hashtrees.util;

import java.util.concurrent.ConcurrentMap;

/**
 * A wrapper object of another object. It can be used in cases where you want to
 * store null object in a data structure, and the data structure does not allow
 * null value. For eg, {@link ConcurrentMap} does not allow null values.
 * 
 * @param <T>
 */
public class Single<T> {
	private final T data;

	public Single(T data) {
		this.data = data;
	}

	public static <T> Single<T> create(T data) {
		return new Single<T>(data);
	}

	public T getData() {
		return data;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null || !(that instanceof Single<?>))
			return false;
		Single<?> thatObj = (Single<?>) that;
		return equals(data, thatObj.data);
	}

	private static <T> boolean equals(T first, T second) {
		if (first == null && second == null)
			return true;
		if (first == null || second == null)
			return false;
		return first.equals(second);
	}

	@Override
	public int hashCode() {
		int result = (data == null) ? 0 : (data.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "[" + ((data == null) ? " " : data.toString()) + "]";
	}
}
