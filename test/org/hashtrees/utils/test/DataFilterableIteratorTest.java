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
 */
package org.hashtrees.utils.test;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.Assert;

import org.hashtrees.util.ByteUtils;
import org.hashtrees.util.DataFilterableIterator;
import org.junit.Test;

import com.google.common.base.Function;

public class DataFilterableIteratorTest {

	private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

	@Test
	public void testDataFilterableIteratorWithLessThanOrEquivalentValues() {

		byte[] endKey = new byte[4];
		ByteBuffer bb = ByteBuffer.wrap(endKey);
		bb.putInt(10);
		Function<Map.Entry<byte[], byte[]>, Integer> kvBytesToIntegerConverter = new Function<Map.Entry<byte[], byte[]>, Integer>() {

			@Override
			public Integer apply(Entry<byte[], byte[]> input) {
				ByteBuffer bb = ByteBuffer.wrap(input.getKey());
				return bb.getInt();
			}
		};

		DataFilterableIterator<Integer> iterator = new DataFilterableIterator<>(
				endKey, false, kvBytesToIntegerConverter,
				new IntegerToBytesIterator());

		List<Integer> expected = new ArrayList<>();
		for (int i = 1; i <= 10; i++)
			expected.add(i);
		List<Integer> actual = new ArrayList<>();
		for (int i = 1; i <= 20; i++) {
			if (iterator.hasNext())
				actual.add(iterator.next());
		}
		Assert.assertFalse(actual.isEmpty());
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testDataFilterableIteratorWithExactMatchValues() {

		byte[] endKey = new byte[4];
		ByteBuffer bb = ByteBuffer.wrap(endKey);
		bb.putInt(1);
		Function<Map.Entry<byte[], byte[]>, Integer> kvBytesToIntegerConverter = new Function<Map.Entry<byte[], byte[]>, Integer>() {

			@Override
			public Integer apply(Entry<byte[], byte[]> input) {
				ByteBuffer bb = ByteBuffer.wrap(input.getKey());
				return bb.getInt();
			}
		};

		DataFilterableIterator<Integer> iterator = new DataFilterableIterator<>(
				endKey, true, kvBytesToIntegerConverter,
				new IntegerToBytesIterator());

		List<Integer> expected = new ArrayList<>();
		expected.add(1);
		List<Integer> actual = new ArrayList<>();
		for (int i = 1; i <= 20; i++) {
			if (iterator.hasNext())
				actual.add(iterator.next());
		}
		Assert.assertFalse(actual.isEmpty());
		Assert.assertEquals(expected, actual);
	}

	private final class IntegerToBytesIterator implements
			Iterator<Map.Entry<byte[], byte[]>> {

		private final AtomicInteger value = new AtomicInteger(0);
		private final Queue<Entry<byte[], byte[]>> intQueue = new ArrayDeque<Entry<byte[], byte[]>>();

		@Override
		public boolean hasNext() {
			return true;
		}

		@Override
		public Entry<byte[], byte[]> next() {
			loadNextElement();
			return intQueue.remove();
		}

		private void loadNextElement() {
			if (intQueue.isEmpty()) {
				intQueue.add(new Entry<byte[], byte[]>() {

					byte[] key;

					@Override
					public byte[] setValue(byte[] value) {
						return null;
					}

					@Override
					public byte[] getValue() {
						return EMPTY_BYTE_ARRAY;
					}

					@Override
					public byte[] getKey() {
						if (key == null) {
							key = new byte[ByteUtils.SIZEOF_INT];
							ByteBuffer bb = ByteBuffer.wrap(key);
							bb.putInt(value.incrementAndGet());
						}
						return key;
					}
				});
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

}
