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
package org.hashtrees.manager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.thrift.TException;
import org.hashtrees.HashTrees;
import org.hashtrees.thrift.generated.HashTreesSyncInterface;
import org.hashtrees.thrift.generated.KeyValue;
import org.hashtrees.thrift.generated.RebuildHashTreeRequest;
import org.hashtrees.thrift.generated.RebuildHashTreeResponse;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;
import org.hashtrees.thrift.generated.ServerName;

/**
 * Just wraps up {@link HashTrees} and provides a view as
 * {@link HashTreesSyncInterface.Iface}. This is used by Thrift server.
 * 
 */
public class HashTreesThriftServer implements HashTreesSyncInterface.Iface {

	private final HashTrees hashTrees;
	private final HashTreesSyncCallsObserver syncCallsObserver;
	private final HashTreesSynchListProvider syncListProvider;

	public HashTreesThriftServer(final HashTrees hashTree,
			final HashTreesSyncCallsObserver syncCallsObserver,
			final HashTreesSynchListProvider syncListProvider) {
		this.hashTrees = hashTree;
		this.syncCallsObserver = syncCallsObserver;
		this.syncListProvider = syncListProvider;
	}

	@Override
	public void sPut(List<KeyValue> keyValuePairs) throws TException {
		try {
			hashTrees.sPut(keyValuePairs);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public void sRemove(List<ByteBuffer> keys) throws TException {
		try {
			hashTrees.sRemove(keys);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public List<SegmentHash> getSegmentHashes(long treeId, List<Integer> nodeIds)
			throws TException {
		try {
			return hashTrees.getSegmentHashes(treeId, nodeIds);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public SegmentHash getSegmentHash(long treeId, int nodeId)
			throws TException {
		try {
			return hashTrees.getSegmentHash(treeId, nodeId);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public List<SegmentData> getSegment(long treeId, int segId)
			throws TException {
		try {
			return hashTrees.getSegment(treeId, segId);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public SegmentData getSegmentData(long treeId, int segId, ByteBuffer key)
			throws TException {
		try {
			return hashTrees.getSegmentData(treeId, segId, key);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public void deleteTreeNode(long treeId, int nodeId) throws TException {
		try {
			hashTrees.deleteTreeNode(treeId, nodeId);
		} catch (IOException e) {
			throw new TException(e);
		}
	}

	@Override
	public void submitRebuildRequest(RebuildHashTreeRequest request)
			throws TException {
		try {
			syncCallsObserver.onRebuildHashTreeRequest(request);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public void submitRebuildResponse(RebuildHashTreeResponse response)
			throws TException {
		try {
			syncCallsObserver.onRebuildHashTreeResponse(response);
		} catch (Exception e) {
			throw new TException(e);
		}
	}

	@Override
	public List<ServerName> getServerNameListFor(long treeId) throws TException {
		return syncListProvider.getServerNameListFor(treeId);
	}

	@Override
	public String ping() throws TException {
		return "hello";
	}
}
