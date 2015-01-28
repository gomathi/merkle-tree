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
 * grows as needed.
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
	 *            , can not be negative
	 * @return, previousValue
	 */
	public boolean set(int bitIndex) {
		AtomicLongArray bitsHolder = getBitsHolderFromMap(bitIndex);
		int arrIndex = getWordIndex(bitIndex);
		while (true) {
			long oldValue = bitsHolder.get(arrIndex);
			long newValue = oldValue | (1L << bitIndex);
			if (bitsHolder.compareAndSet(arrIndex, oldValue, newValue)) {
				return ((oldValue >> bitIndex) & 1) == 1;
			}
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

	public List<Integer> getAllSetBits() {
		List<Integer> result = new ArrayList<Integer>();
		for (Entry<Integer, AtomicLongArray> bitsHolderEntry : bitsHolderMap
				.entrySet()) {
			AtomicLongArray bitsHolder = bitsHolderEntry.getValue();
			for (int i = 0; i < bitsHolder.length(); i++) {
				int offset = bitsHolderEntry.getKey() * ATOMIC_LONG_ARRAY_SIZE;
				long value = bitsHolder.get(i);
				for (int j = offset + (i * BITS_PER_WORD), max = j
						+ (BITS_PER_WORD); value != 0 && j < max; j++) {
					if ((value & 1) == 1)
						result.add(j);
					value = value >> 1;
				}
			}
		}
		return result;
	}

	public List<Integer> clearAndGetAllSetBits() {
		List<Integer> result = new ArrayList<Integer>();
		for (Entry<Integer, AtomicLongArray> bitsHolderEntry : bitsHolderMap
				.entrySet()) {
			AtomicLongArray bitsHolder = bitsHolderEntry.getValue();
			for (int i = 0; i < bitsHolder.length(); i++) {
				int offset = bitsHolderEntry.getKey() * ATOMIC_LONG_ARRAY_SIZE;
				long oldValue = bitsHolder.get(i);
				while (!bitsHolder.compareAndSet(i, oldValue, 0))
					oldValue = bitsHolder.get(i);
				for (int j = offset + (i * BITS_PER_WORD), max = j
						+ (BITS_PER_WORD); oldValue != 0 && j < max; j++) {
					if ((oldValue & 1) == 1)
						result.add(j);
					oldValue = oldValue >> 1;
				}
			}
		}
		return result;
	}

	public void clearBits(List<Integer> bitIndexList) {
		if (bitIndexList.isEmpty())
			return;
		int prevBitIndex = bitIndexList.get(0);
		int prevWordPos = getWordPos(prevBitIndex);
		int prevArrIndex = getWordIndex(prevBitIndex);
		long helper = 1L << prevBitIndex;

		for (int i = 1; i < bitIndexList.size(); i++) {
			int currBitIndex = bitIndexList.get(i);
			int currWordPos = getWordPos(currBitIndex);
			int currArrIndex = getWordIndex(currBitIndex);
			if (currWordPos != prevWordPos || currArrIndex != prevArrIndex) {
				while (true) {
					AtomicLongArray bitsHolder = getBitsHolderFromMap(prevBitIndex);
					long oldValue = bitsHolder.get(prevArrIndex);
					long newValue = oldValue & ~(helper);
					if (bitsHolder.compareAndSet(prevArrIndex, oldValue,
							newValue))
						break;
				}
				prevBitIndex = currBitIndex;
				prevWordPos = currWordPos;
				prevArrIndex = currArrIndex;
				helper = 0;
			}
			helper |= (1L << currBitIndex);
		}

		while (true) {
			AtomicLongArray bitsHolder = getBitsHolderFromMap(prevBitIndex);
			long oldValue = bitsHolder.get(prevArrIndex);
			long newValue = oldValue & ~(helper);
			if (bitsHolder.compareAndSet(prevArrIndex, oldValue, newValue))
				break;
		}
	}

	/**
	 * Clears the given bitIndex.
	 * 
	 * @param bitIndex
	 *            , can not be negative.
	 * @return the previousValue
	 */
	public boolean clear(int bitIndex) {
		AtomicLongArray bitsHolder = getBitsHolderFromMap(bitIndex);
		int arrIndex = getWordIndex(bitIndex);
		while (true) {
			long oldValue = bitsHolder.get(arrIndex);
			long newValue = oldValue & ~(1L << bitIndex);
			if (bitsHolder.compareAndSet(arrIndex, oldValue, newValue))
				return ((oldValue >> bitIndex) & 1) == 1;
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
