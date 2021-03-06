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
package org.hashtrees.store;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.hashtrees.HashTrees;
import org.hashtrees.HashTreesCustomRuntimeException;

/**
 * There could be cases where actual store is missing (key,value) pairs, or
 * having keys which are not supposed to be there. In these cases,
 * {@link HashTrees} has to directly talk to store interface.
 * 
 * {@link HashTrees} has to be provided with the implementation of this class.
 * 
 */
public interface Store {

	boolean contains(byte[] key) throws IOException;

	/**
	 * Adds the key and value to the local store. Also this call should forward
	 * the request to {@link HashTrees#hPut(ByteBuffer, ByteBuffer)} with the
	 * HashTrees object (If there is a hash trees registered already through
	 * {@link #registerHashTrees(HashTrees)}.
	 * 
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	void put(byte[] key, byte[] value) throws IOException;

	byte[] get(byte[] key) throws IOException;

	/**
	 * Removes the key from the local store. Also this call should forward the
	 * request to {@link HashTrees#hRemove(ByteBuffer)} with the HashTrees
	 * object (If there is a hash trees registered already through
	 * {@link #registerHashTrees(HashTrees)}.
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	void delete(byte[] key) throws IOException;

	/**
	 * This should return only (key,value) that are belonging to treeId. Used by
	 * {@link HashTrees#rebuildHashTree(long, boolean)} while rebuilding a
	 * specific hashtree.
	 * 
	 * Note: Iterator implementations should throw
	 * {@link HashTreesCustomRuntimeException} so that failure cases can be
	 * handled properly by {@link HashTrees}
	 * 
	 * @param treeId
	 * @return
	 * @throws IOException
	 */
	Iterator<Map.Entry<byte[], byte[]>> iterator(long treeId)
			throws IOException;

	/**
	 * Register hashtrees with storage implementation. On modifications to the
	 * storage, it should forward the calls to HashTrees.
	 * 
	 * @param hashTrees
	 */
	void registerHashTrees(HashTrees hashTrees);
}
