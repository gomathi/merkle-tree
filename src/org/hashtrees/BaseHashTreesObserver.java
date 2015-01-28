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
package org.hashtrees;

import java.nio.ByteBuffer;
import java.util.List;

import org.hashtrees.thrift.generated.KeyValue;

/**
 * Implements {@link HashTreesObserver}, and all the operations are noop.
 * Extensions of these classes can just override only methods they are
 * interested in.
 * 
 */
public class BaseHashTreesObserver implements HashTreesObserver {

	@Override
	public void preSPut(List<KeyValue> keyValuePairs) {

	}

	@Override
	public void postSPut(List<KeyValue> keyValuePairs) {

	}

	@Override
	public void preSRemove(List<ByteBuffer> keys) {

	}

	@Override
	public void postSRemove(List<ByteBuffer> keys) {

	}

	@Override
	public void preHPut(ByteBuffer key, ByteBuffer value) {

	}

	@Override
	public void postHPut(ByteBuffer key, ByteBuffer value) {

	}

	@Override
	public void preHRemove(ByteBuffer key) {

	}

	@Override
	public void postHRemove(ByteBuffer key) {

	}

	@Override
	public void preRebuild(long treeId, boolean isFullRebuild) {

	}

	@Override
	public void postRebuild(long treeId, boolean isFullRebuild) {

	}

}
