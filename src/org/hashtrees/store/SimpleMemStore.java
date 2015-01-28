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
 */package org.hashtrees.store;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

/**
 * In memory implementation of {@link Store}. Intended to be used in unit tests.
 * 
 * Note the {@link #hashCode()} is calculated by using the internal map of key
 * values. It does not use any other values to calculate hashCode.
 * 
 */
public class SimpleMemStore extends BaseStore {

	private final ConcurrentHashMap<ByteBuffer, ByteBuffer> kvMap = new ConcurrentHashMap<>();

	@Override
	public byte[] get(byte[] key) {
		ByteBuffer intKey = ByteBuffer.wrap(key);
		ByteBuffer valueBB = kvMap.get(intKey);
		if (valueBB != null) {
			byte[] value = new byte[valueBB.remaining()];
			valueBB.duplicate().get(value);
			return value;
		}
		return null;
	}

	@Override
	public void put(byte[] key, byte[] value) throws IOException {
		ByteBuffer intKey = ByteBuffer.wrap(key);
		ByteBuffer intValue = ByteBuffer.wrap(value);
		kvMap.put(intKey, intValue);
		super.put(key, value);
	}

	@Override
	public void delete(byte[] key) throws IOException {
		ByteBuffer intKey = ByteBuffer.wrap(key);
		kvMap.remove(intKey);
		super.delete(key);
	}

	/**
	 * Just returns all the key values in the store, irrespective of which
	 * treeId it belongs to.
	 */
	@Override
	public Iterator<Map.Entry<byte[], byte[]>> iterator(long treeId) {
		return iterator();
	}

	@Override
	public int hashCode() {
		return kvMap.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof SimpleMemStore))
			return false;
		SimpleMemStore that = (SimpleMemStore) other;
		return this.kvMap.equals(that.kvMap);
	}

	public Iterator<Map.Entry<byte[], byte[]>> iterator() {
		return Iterators
				.transform(
						kvMap.entrySet().iterator(),
						new Function<Map.Entry<ByteBuffer, ByteBuffer>, Map.Entry<byte[], byte[]>>() {

							@Override
							public Entry<byte[], byte[]> apply(
									final Entry<ByteBuffer, ByteBuffer> input) {
								return new Entry<byte[], byte[]>() {

									@Override
									public byte[] setValue(byte[] value) {
										throw new UnsupportedOperationException();
									}

									@Override
									public byte[] getValue() {
										byte[] value = new byte[input
												.getValue().remaining()];
										input.getValue().duplicate().get(value);
										return value;
									}

									@Override
									public byte[] getKey() {
										byte[] key = new byte[input.getKey()
												.remaining()];
										input.getKey().duplicate().get(key);
										return key;
									}
								};
							}
						});
	}

	@Override
	public boolean contains(byte[] key) {
		ByteBuffer intKey = ByteBuffer.wrap(key);
		return kvMap.containsKey(intKey);
	}

}