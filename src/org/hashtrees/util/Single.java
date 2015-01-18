package org.hashtrees.util;

import java.util.concurrent.ConcurrentMap;

import com.google.common.base.Objects;

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
		return Objects.equal(data, thatObj.data);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(data);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("object", data).toString();
	}
}
