package org.hashtrees;

import java.nio.ByteBuffer;

/**
 * Simply uses modulo hashing.
 * 
 */
public class ModuloSegIdProvider implements SegmentIdProvider {

	private final int noOfBuckets;

	public ModuloSegIdProvider(int noOfBuckets) {
		this.noOfBuckets = noOfBuckets;
	}

	@Override
	public int getSegmentId(byte[] key) {
		int hcode = ByteBuffer.wrap(key).hashCode();
		return hcode & (noOfBuckets - 1);
	}

}