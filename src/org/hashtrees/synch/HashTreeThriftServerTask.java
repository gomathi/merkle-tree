package org.hashtrees.synch;

import java.util.concurrent.CountDownLatch;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.log4j.Logger;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.hashtrees.HashTree;
import org.hashtrees.thrift.generated.HashTreeSyncInterface;
import org.hashtrees.thrift.generated.HashTreeSyncInterface.Iface;
import org.hashtrees.util.StoppableTask;

/**
 * This class launches a server in order for other nodes to communicate and
 * update the HashTree on this node.
 * 
 */
@ThreadSafe
public class HashTreeThriftServerTask extends StoppableTask {

	private final static Logger LOG = Logger
			.getLogger(HashTreeThriftServerTask.class.getName());
	private volatile TServer server;
	private final HashTree localHashTree;
	private final HashTreeSyncManagerImpl htSynchMgr;
	private final int serverPortNo;
	private final CountDownLatch initializedLatch;

	public HashTreeThriftServerTask(final HashTree localHashTree,
			final HashTreeSyncManagerImpl hashTreeMgr, final int serverPortNo) {
		this(localHashTree, hashTreeMgr, serverPortNo, null);
	}

	public HashTreeThriftServerTask(final HashTree hTree,
			final HashTreeSyncManagerImpl htSynchMgr, final int serverPortNo,
			final CountDownLatch initializedLatch) {
		this.localHashTree = hTree;
		this.htSynchMgr = htSynchMgr;
		this.serverPortNo = serverPortNo;
		this.initializedLatch = initializedLatch;
	}

	@Override
	public synchronized void stop() {
		if (server.isServing())
			server.stop();
		super.stop();
	}

	private static TServer createServer(int serverPortNo,
			HashTreeThriftServer hashTreeServer) throws TTransportException {
		TServerSocket serverTransport = new TServerSocket(serverPortNo);
		HashTreeSyncInterface.Processor<Iface> processor = new HashTreeSyncInterface.Processor<HashTreeSyncInterface.Iface>(
				hashTreeServer);
		TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(
				serverTransport).processor(processor));
		return server;
	}

	private void startServer() throws TTransportException {
		if (server == null) {
			this.server = createServer(serverPortNo, new HashTreeThriftServer(
					localHashTree, htSynchMgr));
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
