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
package org.hashtrees.test.utils;

import static org.hashtrees.test.utils.HashTreesImplTestUtils.TREE_ID_PROVIDER;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.hashtrees.HTReentrantLockProvider;
import org.hashtrees.HashTrees;
import org.hashtrees.HashTreesImpl;
import org.hashtrees.ModuloSegIdProvider;
import org.hashtrees.SyncDiffResult;
import org.hashtrees.SyncType;
import org.hashtrees.store.HashTreesStore;
import org.hashtrees.store.Store;
import org.hashtrees.thrift.generated.KeyValue;

public class HashTreesImplTestObj extends HashTreesImpl {

	private final BlockingQueue<HTSynchEvent> events;

	public static enum HTSynchEvent {
		UPDATE_SEGMENT, UPDATE_FULL_TREE, SYNCH, SYNCH_INITIATED
	}

	public HashTreesImplTestObj(final int noOfSegments,
			boolean enabledNonBlockingCalls, int nbQueSize,
			final HashTreesStore htStore, final Store store,
			BlockingQueue<HTSynchEvent> events) {
		super(noOfSegments, enabledNonBlockingCalls, nbQueSize,
				TREE_ID_PROVIDER, new ModuloSegIdProvider(noOfSegments),
				htStore, store, new HTReentrantLockProvider());
		this.events = events;
	}

	@Override
	public void sPut(List<KeyValue> keyValuePairs) throws IOException {
		super.sPut(keyValuePairs);
		events.add(HTSynchEvent.SYNCH_INITIATED);
	}

	@Override
	public void sRemove(List<ByteBuffer> keys) throws IOException {
		super.sRemove(keys);
		events.add(HTSynchEvent.SYNCH_INITIATED);
	}

	@Override
	public SyncDiffResult synch(long treeId, HashTrees remoteTree)
			throws IOException {
		SyncDiffResult result = super.synch(treeId, remoteTree);
		events.add(HTSynchEvent.SYNCH);
		return result;
	}

	@Override
	public SyncDiffResult synch(long treeId, HashTrees remoteTree,
			SyncType syncType) throws IOException {
		SyncDiffResult result = super.synch(treeId, remoteTree, syncType);
		events.add(HTSynchEvent.SYNCH);
		return result;
	}

	@Override
	public int rebuildHashTree(long treeId, boolean fullRebuild)
			throws IOException {
		int dirtySegsCount = super.rebuildHashTree(treeId, fullRebuild);
		if (!fullRebuild)
			events.add(HTSynchEvent.UPDATE_SEGMENT);
		else
			events.add(HTSynchEvent.UPDATE_FULL_TREE);
		return dirtySegsCount;
	}

}