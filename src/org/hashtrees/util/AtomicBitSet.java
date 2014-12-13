package org.hashtrees.util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLongArray;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Default {@link BitSet} provided in java is not thread safe. This class
 * provides a minimalistic thread safe version of BitSet.
 * 
 */
@ThreadSafe
public class AtomicBitSet {

	private final static int ADDRESS_BITS_PER_WORD = 6;
	private final static int BITS_PER_WORD = 1 << ADDRESS_BITS_PER_WORD;

	private final int totBits;
	private final AtomicLongArray bitsHolder;

	public AtomicBitSet(int totBits) {
		if (totBits < 0)
			throw new IllegalArgumentException("totBits can not be less than 0");
		this.totBits = totBits;
		int length = getWordIndex(totBits - 1) + 1;
		this.bitsHolder = new AtomicLongArray(length);
	}

	private void validateArgument(int bitIndex) {
		if (bitIndex >= totBits || bitIndex < 0)
			throw new ArrayIndexOutOfBoundsException(
					"Given index is invalid : " + bitIndex);
	}

	/**
	 * Gets the index of the element in {@link #bitsHolder} for the given
	 * bitIndex.
	 * 
	 * @param bitIndex
	 *            , values can be between 0 and {@link #totBits} - 1
	 * @return
	 */
	private int getWordIndex(int bitIndex) {
		return bitIndex >> ADDRESS_BITS_PER_WORD;
	}

	/**
	 * Sets given bitIndex.
	 * 
	 * @param bitIndex
	 *            , can not be negative or larger than or equal to the length of
	 *            the bitSet.
	 */
	public void set(int bitIndex) {
		validateArgument(bitIndex);

		int arrIndex = getWordIndex(bitIndex);
		while (true) {
			long oldValue = bitsHolder.get(arrIndex);
			long newValue = oldValue | (1L << bitIndex);
			if (bitsHolder.compareAndSet(arrIndex, oldValue, newValue))
				return;
		}
	}

	/**
	 * Gets the value of bitIndex.
	 * 
	 * @param bitIndex
	 *            , can not be negative or larger than or equal to the length of
	 *            the bitSet.
	 * @return, true corresponds to setBit and false corresponds to clearBit
	 */
	public boolean get(int bitIndex) {
		validateArgument(bitIndex);

		int arrIndex = getWordIndex(bitIndex);
		long value = (bitsHolder.get(arrIndex) >> bitIndex);
		return (value & 1) == 1;
	}

	public List<Integer> clearAndGetAllSetBits() {
		List<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < bitsHolder.length(); i++) {
			long oldValue = bitsHolder.get(i);
			while (!bitsHolder.compareAndSet(i, oldValue, 0))
				oldValue = bitsHolder.get(i);
			for (int j = i * BITS_PER_WORD, max = (j + BITS_PER_WORD); j < max
					&& j < totBits && oldValue != 0; j++) {
				if ((oldValue & 1) == 1)
					result.add(j);
				oldValue = oldValue >> 1;
			}
		}
		return result;
	}

	/**
	 * Clears all the bits.
	 * 
	 */
	public void clear() {
		for (int i = 0; i < bitsHolder.length(); i++)
			bitsHolder.set(i, 0);
	}

	/**
	 * Clears the given bitIndex.
	 * 
	 * @param bitIndex
	 *            , can not be negative or larger than or equal to the length of
	 *            the bitSet.
	 */
	public void clear(int bitIndex) {
		validateArgument(bitIndex);

		int arrIndex = getWordIndex(bitIndex);
		while (true) {
			long oldValue = bitsHolder.get(arrIndex);
			long newValue = oldValue & ~(1L << bitIndex);
			if (bitsHolder.compareAndSet(arrIndex, oldValue, newValue))
				return;
		}
	}
}
