/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */package org.hashtrees.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ByteUtils {

	/**
	 * Size of boolean in bytes
	 */
	public static final int SIZEOF_BOOLEAN = Byte.SIZE / Byte.SIZE;

	/**
	 * Size of byte in bytes
	 */
	public static final int SIZEOF_BYTE = SIZEOF_BOOLEAN;

	/**
	 * Size of char in bytes
	 */
	public static final int SIZEOF_CHAR = Character.SIZE / Byte.SIZE;

	/**
	 * Size of double in bytes
	 */
	public static final int SIZEOF_DOUBLE = Double.SIZE / Byte.SIZE;

	/**
	 * Size of float in bytes
	 */
	public static final int SIZEOF_FLOAT = Float.SIZE / Byte.SIZE;

	/**
	 * Size of int in bytes
	 */
	public static final int SIZEOF_INT = Integer.SIZE / Byte.SIZE;

	/**
	 * Size of long in bytes
	 */
	public static final int SIZEOF_LONG = Long.SIZE / Byte.SIZE;

	/**
	 * Size of short in bytes
	 */
	public static final int SIZEOF_SHORT = Short.SIZE / Byte.SIZE;

	public static byte[] sha1(byte[] input) {
		return getDigest("SHA-1").digest(input);
	}

	public static MessageDigest getDigest(String algorithm) {
		try {
			return MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("Unknown algorithm: " + algorithm,
					e);
		}
	}

	/**
	 * @param left
	 *            left operand
	 * @param right
	 *            right operand
	 * @return 0 if equal, < 0 if left is less than right, etc.
	 */
	public static int compareTo(final byte[] left, final byte[] right) {
		return compareTo(left, 0, left.length, right, 0, right.length);
	}

	/**
	 * Lexographically compare two arrays.
	 * 
	 * @param buffer1
	 *            left operand
	 * @param buffer2
	 *            right operand
	 * @param offset1
	 *            Where to start comparing in the left buffer
	 * @param offset2
	 *            Where to start comparing in the right buffer
	 * @param length1
	 *            How much to compare from the left buffer
	 * @param length2
	 *            How much to compare from the right buffer
	 * @return 0 if equal, < 0 if left is less than right, etc.
	 */
	public static int compareTo(byte[] buffer1, int offset1, int length1,
			byte[] buffer2, int offset2, int length2) {
		// Bring WritableComparator code local
		int end1 = offset1 + length1;
		int end2 = offset2 + length2;
		for (int i = offset1, j = offset2; i < end1 && j < end2; i++, j++) {
			int a = (buffer1[i] & 0xff);
			int b = (buffer2[j] & 0xff);
			if (a != b) {
				return a - b;
			}
		}
		return length1 - length2;
	}

	/**
	 * Converts a byte array to a long value. Reverses {@link #toBytes(long)}
	 * 
	 * @param bytes
	 *            array
	 * @return the long value
	 */
	public static long toLong(byte[] bytes) {
		return toLong(bytes, 0, SIZEOF_LONG);
	}

	/**
	 * Converts a byte array to a long value. Assumes there will be
	 * {@link #SIZEOF_LONG} bytes available.
	 * 
	 * @param bytes
	 *            bytes
	 * @param offset
	 *            offset
	 * @return the long value
	 */
	public static long toLong(byte[] bytes, int offset) {
		return toLong(bytes, offset, SIZEOF_LONG);
	}

	/**
	 * Converts a byte array to a long value.
	 * 
	 * @param bytes
	 *            array of bytes
	 * @param offset
	 *            offset into array
	 * @param length
	 *            length of data (must be {@link #SIZEOF_LONG})
	 * @return the long value
	 * @throws IllegalArgumentException
	 *             if length is not {@link #SIZEOF_LONG} or if there's not
	 *             enough room in the array at the offset indicated.
	 */
	public static long toLong(byte[] bytes, int offset, final int length) {
		if (length != SIZEOF_LONG || offset + length > bytes.length) {
			throw explainWrongLengthOrOffset(bytes, offset, length, SIZEOF_LONG);
		}
		long l = 0;
		for (int i = offset; i < offset + length; i++) {
			l <<= 8;
			l ^= bytes[i] & 0xFF;
		}
		return l;
	}

	private static IllegalArgumentException explainWrongLengthOrOffset(
			final byte[] bytes, final int offset, final int length,
			final int expectedLength) {
		String reason;
		if (length != expectedLength) {
			reason = "Wrong length: " + length + ", expected " + expectedLength;
		} else {
			reason = "offset (" + offset + ") + length (" + length
					+ ") exceed the" + " capacity of the array: "
					+ bytes.length;
		}
		return new IllegalArgumentException(reason);
	}

	/**
	 * Copy the specified bytes into a new array
	 * 
	 * @param array
	 *            The array to copy from
	 * @param from
	 *            The index in the array to begin copying from
	 * @param to
	 *            The least index not copied
	 * @return A new byte[] containing the copied bytes
	 */
	public static byte[] copy(byte[] array, int from, int to) {
		if (to - from < 0) {
			return new byte[0];
		} else {
			byte[] a = new byte[to - from];
			System.arraycopy(array, from, a, 0, to - from);
			return a;
		}
	}

	public static int roundUpToPowerOf2(int number) {
		return (number > 1) ? Integer.highestOneBit((number - 1) << 1) : 1;
	}
}