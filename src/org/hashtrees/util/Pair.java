package org.hashtrees.util;

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
		return equals(f, thatObj.f) && equals(s, thatObj.s);
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
		int PRIME = 31;
		int result = (f == null) ? 0 : (f.hashCode());
		result = (result * PRIME) + ((s == null) ? 0 : s.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "[" + ((f == null) ? " " : f.toString()) + ","
				+ ((s == null) ? " " : s.toString()) + "]";
	}
}
