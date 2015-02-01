package org.hashtrees;

/**
 * Extension of {@link RuntimeException} is defined to use in places where
 * checked exceptions are not allowed like iterators.
 * 
 */
public class HashTreesCustomRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -7090544509716685721L;

	public HashTreesCustomRuntimeException() {
		super();
	}

	public HashTreesCustomRuntimeException(String msg) {
		super(msg);
	}

	public HashTreesCustomRuntimeException(Throwable arg) {
		super(arg);
	}

	public HashTreesCustomRuntimeException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

}
