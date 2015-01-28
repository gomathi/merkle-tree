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

import org.hashtrees.HashTrees;

/**
 * The implementations of {@link Store} can extend this class, so those dont
 * have to implement {@link #registerHashTrees(HashTrees)} also forwarding the
 * calls to {@link HashTrees#hPut(ByteBuffer, ByteBuffer)} and
 * {@link HashTrees#hRemove(ByteBuffer)}.
 * 
 * Look at {@link SimpleMemStore} to know how this class is being used.
 */
public abstract class BaseStore implements Store {

	private volatile HashTrees hashTrees;

	@Override
	public void put(byte[] key, byte[] value) throws IOException {
		if (hashTrees != null)
			hashTrees.hPut(ByteBuffer.wrap(key), ByteBuffer.wrap(value));
	}

	@Override
	public void delete(byte[] key) throws IOException {
		if (hashTrees != null)
			hashTrees.hRemove(ByteBuffer.wrap(key));
	}

	@Override
	public void registerHashTrees(HashTrees hashTrees) {
		this.hashTrees = hashTrees;
	}
}
