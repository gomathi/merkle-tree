package org.hashtrees.util;

import com.google.common.base.Objects;

public class Pair<F, S> {

	private final F f;
	private final S s;

	public Pair(F f, S s) {
		this.f = f;
		this.s = s;
	}

	public static <F, S> Pair<F, S> create(F f, S s) {
		return new Pair<F, S>(f, s);
	}

	public F getFirst() {
		return f;
	}

	public S getSecond() {
		return s;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null || !(that instanceof Pair<?, ?>))
			return false;
		Pair<?, ?> thatObj = (Pair<?, ?>) that;
		return Objects.equal(f, thatObj.f) && Objects.equal(s, thatObj.s);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(f, s);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("first", f).add("second", s)
				.toString();
	}
}
