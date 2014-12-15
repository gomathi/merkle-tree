package org.hashtrees.util;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLongArray;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Default {@link BitSet} provided in java is not thread safe. This class
 * provides a minimalistic thread safe version of BitSet and also dynamically
 * allocates any .
 * 
 */
@ThreadSafe
public class AtomicBitSet {

	private final static int ADDRESS_BITS_PER_WORD = 6;
	private final static int BITS_PER_WORD = 1 << ADDRESS_BITS_PER_WORD;

	private final static int ADDRESS_BITS_PER_WORD_ATOMIC_ARRAY = 10;
	private final static int ATOMIC_LONG_ARRAY_SIZE = 1 << ADDRESS_BITS_PER_WORD_ATOMIC_ARRAY;
	private final ConcurrentSkipListMap<Integer, AtomicLongArray> bitsHolderMap = new ConcurrentSkipListMap<>();

	private int getWordPos(int bitIndex) {
		return bitIndex >> ADDRESS_BITS_PER_WORD_ATOMIC_ARRAY;
	}

	private int getWordIndex(int bitIndex) {
		return (bitIndex & (ATOMIC_LONG_ARRAY_SIZE - 1)) >> ADDRESS_BITS_PER_WORD;
	}

	private AtomicLongArray getBitsHolderFromMap(int bitIndex) {
		int wordPos = getWordPos(bitIndex);
		if (!bitsHolderMap.containsKey(wordPos))
			bitsHolderMap.putIfAbsent(wordPos, new AtomicLongArray(
					ATOMIC_LONG_ARRAY_SIZE));
		return bitsHolderMap.get(wordPos);
	}

	/**
	 * Sets given bitIndex.
	 * 
	 * @param bitIndex
	 *            , can not be negative.
	 */
	public void set(int bitIndex) {
		AtomicLongArray bitsHolder = getBitsHolderFromMap(bitIndex);
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
	 *            , can not be negative.
	 * @return, true corresponds to setBit and false corresponds to clearBit
	 */
	public boolean get(int bitIndex) {
		AtomicLongArray bitsHolder = getBitsHolderFromMap(bitIndex);
		int arrIndex = getWordIndex(bitIndex);
		long value = (bitsHolder.get(arrIndex) >> bitIndex);
		return (value & 1) == 1;
	}

	public List<Integer> clearAndGetAllSetBits() {
		List<Integer> result = new ArrayList<Integer>();
		for (Entry<Integer, AtomicLongArray> bitsHolderEntry : bitsHolderMap
				.entrySet()) {
			int offset = bitsHolderEntry.getKey() * ATOMIC_LONG_ARRAY_SIZE;
			AtomicLongArray bitsHolder = bitsHolderEntry.getValue();
			for (int i = 0; i < bitsHolder.length(); i++) {
				long oldValue = bitsHolder.get(i);
				while (!bitsHolder.compareAndSet(i, oldValue, 0))
					oldValue = bitsHolder.get(i);
				for (int j = offset + (i * BITS_PER_WORD); oldValue != 0; j++) {
					if ((oldValue & 1) == 1)
						result.add(j);
					oldValue = oldValue >> 1;
				}
			}
		}
		return result;
	}

	/**
	 * Clears the given bitIndex.
	 * 
	 * @param bitIndex
	 *            , can not be negative.
	 */
	public void clear(int bitIndex) {
		AtomicLongArray bitsHolder = getBitsHolderFromMap(bitIndex);
		int arrIndex = getWordIndex(bitIndex);
		while (true) {
			long oldValue = bitsHolder.get(arrIndex);
			long newValue = oldValue & ~(1L << bitIndex);
			if (bitsHolder.compareAndSet(arrIndex, oldValue, newValue))
				return;
		}
	}

	/**
	 * Clears all the bits.
	 * 
	 */
	public void clear() {
		for (AtomicLongArray bitsHolder : bitsHolderMap.values())
			for (int i = 0; i < bitsHolder.length(); i++)
				bitsHolder.set(i, 0);
	}

}
