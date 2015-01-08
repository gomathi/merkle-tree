package org.hashtrees.synch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.hashtrees.HashTrees;
import org.hashtrees.HashTreesIdProvider;
import org.hashtrees.store.HashTreeSyncManagerStore;
import org.hashtrees.thrift.generated.HashTreesSyncInterface;
import org.hashtrees.thrift.generated.RebuildHashTreeRequest;
import org.hashtrees.thrift.generated.RebuildHashTreeResponse;
import org.hashtrees.thrift.generated.ServerName;
import org.hashtrees.util.CustomThreadFactory;
import org.hashtrees.util.Pair;
import org.hashtrees.util.StoppableTask;
import org.hashtrees.util.TaskQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * A state machine based manager which runs background tasks to rebuild hash
 * trees, and synch remote hash trees.
 * 
 * HashTrees updates tree hashes at regular intervals, not on every update of
 * the key. Hence if two hash trees are continuously updating their hashes at
 * different intervals, synch operation between these trees will always cause a
 * mismatch even though underlying data is same. We should avoid unnecessary
 * network transfers. Thus a state machine based approach is used where whenever
 * the primary hash tree rebuilds its hash tree, it requests the remote hash
 * tree to rebuild the hash tree as well.
 * 
 * HashTreeManager goes through the following states.
 * 
 * START -> REBUILD -> SYNCH -> REBUILD -> STOP(when requested). STOP state can
 * be reached from REBUILD or SYNCH state.
 * 
 * At any time, the manager can be asked to shutdown, by requesting stop.
 * 
 * {@link HashTrees} is a stand alone class, it does not do automatically build
 * segments or any additional synch functionalities, this class provides those
 * functions.
 * 
 */
public class HashTreesSyncManager extends StoppableTask implements
		HashTreesSyncCallsObserver {

	private enum STATE {
		START, REBUILD, SYNCH
	}

	private final static Logger LOG = LoggerFactory
			.getLogger(HashTreesSyncManager.class);
	private final static String SYNC_THREADS_NAME = "SyncManagerThreads";
	private final static String STATE_MACHINE_THREAD_NAME = "StateMachineThread";

	public final static int DEF_NO_OF_THREADS = 10;
	// Expected time interval between two consecutive tree full rebuilds.
	public final static long DEF_FULL_REBUILD_TIME_INTERVAL = 30 * 60 * 1000;
	// If local hash tree do not receive rebuilt confirmation from remote node,
	// then it will not synch remote node. To handle worst case scenarios, we
	// will synch remote node after a specific period of time.
	public final static long DEF_MAX_UNSYNCED_TIME_INTERVAL = 15 * 60 * 1000;
	// Synch and Rebuild events are executed alternatively. This specifies the
	// lapse between these two events.
	public final static long DEF_INTERVAL_BW_SYNCH_AND_REBUILD = 5 * 60 * 1000;

	private final String syncThreadsName;
	private final String stateMachineThreadName;
	private final HashTrees hashTrees;
	private final HashTreesIdProvider treeIdProvider;
	private final HashTreeSyncManagerStore syncManagerStore;
	private final ServerName localServer;
	private final int noOfThreads;
	private final long fullRebuildTimeInterval;
	private final long intBetweenSynchAndRebuild;

	private final ConcurrentSkipListMap<ServerName, HashTreesSyncInterface.Iface> servers = new ConcurrentSkipListMap<>();
	private final ConcurrentSkipListSet<ServerName> serversToSync = new ConcurrentSkipListSet<>();
	private final ConcurrentMap<Pair<ServerName, Long>, Pair<Long, Boolean>> remoteTreeAndLastBuildReqTS = new ConcurrentHashMap<>();
	private final ConcurrentMap<Pair<ServerName, Long>, Long> remoteTreeAndLastSyncedTS = new ConcurrentHashMap<>();
	private final ScheduledExecutorService stateMgrExecutor;
	private final AtomicBoolean initialized = new AtomicBoolean(false);
	private final AtomicBoolean stopped = new AtomicBoolean(false);

	private volatile ExecutorService fixedExecutors;
	private volatile STATE currState = STATE.START;
	private volatile HashTreesThriftServerTask htThriftServer;

	private static String getHostNameAndPortNoString(String hostName, int portNo) {
		return hostName + "," + portNo;
	}

	public HashTreesSyncManager(String localHostName, int localServerPortNo,
			HashTrees hashTrees, HashTreesIdProvider treeIdProvider,
			HashTreeSyncManagerStore syncMgrStore) {
		this(hashTrees, treeIdProvider, syncMgrStore, localHostName,
				localServerPortNo, DEF_FULL_REBUILD_TIME_INTERVAL,
				DEF_INTERVAL_BW_SYNCH_AND_REBUILD, DEF_NO_OF_THREADS);
	}

	public HashTreesSyncManager(HashTrees hashTrees,
			HashTreesIdProvider treeIdProvider,
			HashTreeSyncManagerStore syncMgrStore, String localHostName,
			int localServerPortNo, long fullRebuildTimeInterval,
			long intBWSynchAndRebuild, int noOfBGThreads) {
		this.localServer = new ServerName(localHostName, localServerPortNo);
		this.syncThreadsName = SYNC_THREADS_NAME + ","
				+ getHostNameAndPortNoString(localHostName, localServerPortNo);
		this.stateMachineThreadName = STATE_MACHINE_THREAD_NAME + ","
				+ getHostNameAndPortNoString(localHostName, localServerPortNo);
		this.hashTrees = hashTrees;
		this.syncManagerStore = syncMgrStore;
		this.treeIdProvider = treeIdProvider;
		this.fullRebuildTimeInterval = fullRebuildTimeInterval;
		this.noOfThreads = noOfBGThreads;
		this.intBetweenSynchAndRebuild = intBWSynchAndRebuild;
		List<ServerName> serversToSyncInStore = syncMgrStore.getAllServers();
		for (ServerName sn : serversToSyncInStore)
			addServerToSyncList(sn);
		stateMgrExecutor = Executors.newScheduledThreadPool(1,
				new CustomThreadFactory(stateMachineThreadName));
	}

	@Override
	public void onRebuildHashTreeResponse(RebuildHashTreeResponse response) {
		Pair<ServerName, Long> snAndTid = Pair.create(response.sn,
				response.treeId);
		Pair<Long, Boolean> tsAndResponse = remoteTreeAndLastBuildReqTS
				.get(snAndTid);
		if (tsAndResponse != null
				&& tsAndResponse.getFirst().equals(response.tokenNo)) {
			Pair<Long, Boolean> updatedResponse = Pair.create(response.tokenNo,
					true);
			remoteTreeAndLastBuildReqTS.replace(snAndTid, tsAndResponse,
					updatedResponse);
		}
	}

	@Override
	public void onRebuildHashTreeRequest(RebuildHashTreeRequest request)
			throws Exception {
		boolean fullRebuild = (System.currentTimeMillis() - hashTrees
				.getLastFullyRebuiltTimeStamp(request.treeId)) > request.expFullRebuildTimeInt ? true
				: false;
		hashTrees.rebuildHashTree(request.treeId, fullRebuild);
		HashTreesSyncInterface.Iface client = getHashTreeSyncClient(request.requester);
		RebuildHashTreeResponse response = new RebuildHashTreeResponse(
				localServer, request.treeId, request.tokenNo);
		client.submitRebuildResponse(response);
	}

	private void rebuildAllLocallyManagedTrees() {
		Iterator<Long> treeIdItr = treeIdProvider.getAllPrimaryTreeIds();
		if (!treeIdItr.hasNext()) {
			LOG.info("There are no locally managed trees. So skipping rebuild operation.");
			return;
		}
		List<Pair<Long, Boolean>> treeIdAndRebuildType = new ArrayList<Pair<Long, Boolean>>();
		while (treeIdItr.hasNext()) {
			long treeId = treeIdItr.next();
			try {
				boolean isFullRebuild = (System.currentTimeMillis() - hashTrees
						.getLastFullyRebuiltTimeStamp(treeId)) > fullRebuildTimeInterval ? true
						: false;
				treeIdAndRebuildType.add(Pair.create(treeId, isFullRebuild));
			} catch (Exception e) {
				LOG.error("Exception occurred while rebuilding.", e);
			}
			if (hasStopRequested()) {
				LOG.info("Stop has been requested. Not proceeding with further rebuild task.");
				return;
			}
		}
		Collection<Callable<Void>> rebuildTasks = Collections2.transform(
				treeIdAndRebuildType,
				new Function<Pair<Long, Boolean>, Callable<Void>>() {

					@Override
					public Callable<Void> apply(final Pair<Long, Boolean> input) {
						return new Callable<Void>() {

							@Override
							public Void call() throws Exception {
								sendRequestForRebuild(input.getFirst());
								hashTrees.rebuildHashTree(input.getFirst(),
										input.getSecond());
								return null;
							}
						};
					}
				});
		LOG.info("Building locally managed trees.");
		TaskQueue<Void> taskQueue = new TaskQueue<Void>(fixedExecutors,
				rebuildTasks.iterator(), noOfThreads);
		while (taskQueue.hasNext()) {
			taskQueue.next();
			if (hasStopRequested()) {
				taskQueue.stop();
			}
		}
		LOG.info("Building locally managed trees - Done");
	}

	private void sendRequestForRebuild(long treeId) {
		for (ServerName sn : serversToSync) {
			Pair<ServerName, Long> serverNameWTreeId = Pair.create(sn, treeId);
			try {
				long buildReqTS = System.currentTimeMillis();
				HashTreesSyncInterface.Iface client = getHashTreeSyncClient(sn);
				remoteTreeAndLastSyncedTS.putIfAbsent(serverNameWTreeId,
						buildReqTS);
				remoteTreeAndLastBuildReqTS.put(serverNameWTreeId,
						Pair.create(buildReqTS, false));
				RebuildHashTreeRequest request = new RebuildHashTreeRequest(
						localServer, treeId, buildReqTS,
						DEF_FULL_REBUILD_TIME_INTERVAL);
				client.submitRebuildRequest(request);
			} catch (TException e) {
				LOG.error("Unable to send rebuild notification to "
						+ serverNameWTreeId, e);
			}
		}
	}

	private void synch() {
		Iterator<Long> treeIds = treeIdProvider.getAllPrimaryTreeIds();
		List<Pair<ServerName, Long>> remoteTrees = new ArrayList<>();

		while (treeIds.hasNext()) {
			long treeId = treeIds.next();

			for (ServerName sn : serversToSync) {
				Pair<ServerName, Long> serverNameATreeId = Pair.create(sn,
						treeId);
				Pair<Long, Boolean> lastBuildReqTSAndResponse = remoteTreeAndLastBuildReqTS
						.remove(serverNameATreeId);
				Long unsyncedTime = remoteTreeAndLastSyncedTS
						.get(serverNameATreeId);

				if (unsyncedTime == null || lastBuildReqTSAndResponse == null) {
					LOG.info("Unsynced info entry is not available. Synch should be followed by rebuild. Skipping syncing "
							+ serverNameATreeId);
					continue;
				}

				try {
					if ((lastBuildReqTSAndResponse.getSecond())
							|| ((System.currentTimeMillis() - unsyncedTime) > DEF_MAX_UNSYNCED_TIME_INTERVAL)) {
						remoteTrees.add(serverNameATreeId);
						remoteTreeAndLastSyncedTS.remove(serverNameATreeId);
					} else {
						LOG.info("Did not receive confirmation from "
								+ serverNameATreeId
								+ " for the rebuilding. Not syncing the remote node.");
					}
				} catch (Exception e) {
					LOG.error("Exception occurred while doing synch.", e);
				}

				if (hasStopRequested()) {
					LOG.info("Stop has been requested. Skipping further sync tasks");
					return;
				}
			}
		}

		if (remoteTrees.size() == 0) {
			LOG.info("There is no synch required for any remote trees. Skipping this cycle.");
			return;
		}
		LOG.info("Synching remote hash trees.");
		Collection<Callable<Void>> syncTasks = Collections2.transform(
				remoteTrees,
				new Function<Pair<ServerName, Long>, Callable<Void>>() {

					@Override
					public Callable<Void> apply(
							final Pair<ServerName, Long> input) {
						return new Callable<Void>() {

							@Override
							public Void call() throws Exception {
								synch(input.getFirst(), input.getSecond());
								return null;
							}
						};
					}
				});

		TaskQueue<Void> taskQueue = new TaskQueue<Void>(fixedExecutors,
				syncTasks.iterator(), noOfThreads);
		while (taskQueue.hasNext()) {
			taskQueue.next();
			if (hasStopRequested()) {
				taskQueue.stop();
			}
		}
		LOG.info("Synching remote hash trees. - Done");
	}

	private void synch(ServerName sn, long treeId) throws Exception {
		Pair<ServerName, Long> hostNameAndTreeId = Pair.create(sn, treeId);
		try {
			LOG.info("Syncing " + hostNameAndTreeId);
			HashTreesSyncInterface.Iface remoteSyncClient = getHashTreeSyncClient(sn);
			hashTrees
					.synch(treeId, new HashTreesRemoteClient(remoteSyncClient));
			LOG.info("Syncing " + hostNameAndTreeId + " complete.");
		} catch (TException e) {
			LOG.error("Unable to synch remote hash tree server : "
					+ hostNameAndTreeId, e);
		}
	}

	private HashTreesSyncInterface.Iface getHashTreeSyncClient(ServerName sn)
			throws TTransportException {
		HashTreesSyncInterface.Iface client = servers.get(sn);
		if (client == null) {
			servers.putIfAbsent(sn,
					HashTreesThriftClientProvider.getThriftHashTreeClient(sn));
			client = servers.get(sn);
		}
		return client;
	}

	public void init() {
		if (initialized.compareAndSet(false, true)) {
			this.fixedExecutors = Executors.newFixedThreadPool(noOfThreads,
					new CustomThreadFactory(syncThreadsName));
			CountDownLatch initializedLatch = new CountDownLatch(1);
			htThriftServer = new HashTreesThriftServerTask(hashTrees, this,
					localServer.getPortNo(), initializedLatch);
			new Thread(htThriftServer).start();
			try {
				initializedLatch.await();
			} catch (InterruptedException e) {
				LOG.error(
						"Exception occurred while waiting for the server to start",
						e);
			}
			stateMgrExecutor.scheduleWithFixedDelay(this, 0,
					intBetweenSynchAndRebuild, TimeUnit.MILLISECONDS);
		} else {
			LOG.info("HashTreeSyncManager initialized already.");
			return;
		}
	}

	private static STATE getNextState(STATE currState) {
		switch (currState) {
		case START:
		case SYNCH:
			return STATE.REBUILD;
		case REBUILD:
			return STATE.SYNCH;
		default:
			throw new IllegalStateException();
		}
	}

	@Override
	public void runImpl() {
		currState = getNextState(currState);
		LOG.info("Executing actions for state " + currState);
		switch (currState) {
		case REBUILD:
			rebuildAllLocallyManagedTrees();
			break;
		case SYNCH:
			synch();
			break;
		case START:
		default:
			break;
		}
		LOG.info("Executing actions for state " + currState + " - Done.");
	}

	@Override
	public void addServerToSyncList(ServerName sn) {
		syncManagerStore.addServerToSyncList(sn);
		boolean added = serversToSync.add(sn);
		if (added) {
			LOG.info(sn + " has been added to sync list.");
		} else
			LOG.info(sn + "has been already on the sync list.");
	}

	@Override
	public void removeServerFromSyncList(ServerName sn) {
		syncManagerStore.removeServerFromSyncList(sn);
		boolean removed = serversToSync.remove(sn);
		if (removed)
			LOG.info(sn + "has been removed from sync list.");
		else
			LOG.info(sn + " was not in the sync list to be removed.");
	}

	public void shutdown() {
		if (stopped.compareAndSet(false, true)) {
			CountDownLatch localLatch = new CountDownLatch(1);
			super.stop(localLatch);
			try {
				localLatch.await();
			} catch (InterruptedException e) {
				LOG.error("Exception occurred while stopping the operations.",
						e);
			}
			if (stateMgrExecutor != null)
				stateMgrExecutor.shutdown();
			if (htThriftServer != null)
				htThriftServer.stop();
			if (fixedExecutors != null)
				fixedExecutors.shutdown();
			LOG.info("Hash tree sync manager operations stopped.");
		} else
			LOG.info("Hash tree sync manager operations stopped already. No actions were taken.");
	}
}
