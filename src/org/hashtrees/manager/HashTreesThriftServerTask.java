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
 */package org.hashtrees.manager;

import java.util.concurrent.CountDownLatch;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.hashtrees.HashTrees;
import org.hashtrees.thrift.generated.HashTreesSyncInterface;
import org.hashtrees.thrift.generated.HashTreesSyncInterface.Iface;
import org.hashtrees.util.StoppableTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class launches a server in order for other nodes to communicate and
 * update the HashTree on this node.
 * 
 */
@ThreadSafe
public class HashTreesThriftServerTask extends StoppableTask {

	private final static Logger LOG = LoggerFactory
			.getLogger(HashTreesThriftServerTask.class.getName());
	private volatile TServer server;
	private final HashTrees localHashTree;
	private final HashTreesSyncCallsObserver htSynchCallsObserver;
	private final HashTreesSynchListProvider htSyncListProvider;
	private final int serverPortNo;
	private final CountDownLatch initializedLatch;

	public HashTreesThriftServerTask(final HashTrees hTree,
			final HashTreesSyncCallsObserver htSynchCallsObserver,
			final HashTreesSynchListProvider htSyncListProvider,
			final int serverPortNo, final CountDownLatch initializedLatch) {
		this.localHashTree = hTree;
		this.htSynchCallsObserver = htSynchCallsObserver;
		this.htSyncListProvider = htSyncListProvider;
		this.serverPortNo = serverPortNo;
		this.initializedLatch = initializedLatch;
	}

	@Override
	public synchronized void stopAsync() {
		if (server.isServing())
			server.stop();
		super.stopAsync();
	}

	private static TServer createServer(int serverPortNo,
			HashTreesThriftServer hashTreeServer) throws TTransportException {
		TServerSocket serverTransport = new TServerSocket(serverPortNo);
		HashTreesSyncInterface.Processor<Iface> processor = new HashTreesSyncInterface.Processor<HashTreesSyncInterface.Iface>(
				hashTreeServer);
		TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(
				serverTransport).processor(processor));
		return server;
	}

	private void startServer() throws TTransportException {
		if (server == null) {
			this.server = createServer(serverPortNo, new HashTreesThriftServer(
					localHashTree, htSynchCallsObserver, htSyncListProvider));
			if (initializedLatch != null)
				initializedLatch.countDown();
			server.serve();
			LOG.info("Hash tree server has started.");
		}
	}

	@Override
	public void run() {
		runImpl();
	}

	@Override
	public void runImpl() {
		try {
			startServer();
		} catch (TTransportException e) {
			LOG.error("Exception occurred while starting server.", e);
		}
	}

}
