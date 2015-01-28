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
 */package org.hashtrees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Default HashTreeIdProvider which always returns treeId as 1. This can be used
 * in places where only a single hash tree is maintained by {@link HashTrees}
 * 
 */
public class SimpleTreeIdProvider implements HashTreesIdProvider {

	public static final long TREE_ID = 1;
	private static final List<Long> TREE_IDS;

	static {
		List<Long> treeIds = new ArrayList<Long>();
		treeIds.add(TREE_ID);
		TREE_IDS = Collections.unmodifiableList(treeIds);
	}

	@Override
	public long getTreeId(byte[] key) {
		return TREE_ID;
	}

	@Override
	public Iterator<Long> getAllPrimaryTreeIds() {
		return TREE_IDS.iterator();
	}
}