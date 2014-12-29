package org.hashtrees.synch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.hashtrees.HashTrees;
import org.hashtrees.HashTreesIdProvider;
import org.hashtrees.HashTreesImpl;
import org.hashtrees.thrift.generated.HashTreeSyncInterface;
import org.hashtrees.thrift.generated.ServerName;
import org.hashtrees.util.CustomThreadFactory;
import org.hashtrees.util.LockedBy;
import org.hashtrees.util.Pair;
import org.hashtrees.util.Single;
import org.hashtrees.util.StoppableTask;
import org.hashtrees.util.TaskQueue;

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
 * {@link HashTreesImpl} is a stand alone class, it does not do automatically
 * build segments or any additional synch functionalities, this class provides
 * those functions.
 * 
 */
public class HashTreesSyncManagerImpl extends StoppableTask implements
		HashTreesSyncManager {

	private enum STATE {
		START, REBUILD, SYNCH
	}

	private final static Logger LOG = Logger
			.getLogger(HashTreesSyncManagerImpl.class);
	private final static String HT_THREAD_NAME = "HTSyncManagerThreads";
	private final static String HT_SM_THREAD = "HTStateMachineExecThread";

	public final static int DEFAULT_NO_OF_THREADS = 10;
	// Expected time interval between two consecutive tree full rebuilds.
	public final static long DEFAULT_FULL_REBUILD_TIME_INTERVAL = 30 * 60 * 1000;
	// If local hash tree do not receive rebuilt confirmation from remote node,
	// then it will not synch remote node. To handle worst case scenarios, we
	// will synch remote node after a specific period of time.
	public final static long DEFAULT_MAX_UNSYNCED_TIME_INTERVAL = 15 * 60 * 1000;
	// Synch and Rebuild events are executed alternatively. This specifies the
	// lapse between these two events.
	public final static long DEFAULT_INTERVAL_BW_SYNCH_AND_REBUILD = 5 * 60 * 1000;

	private final HashTrees hashTree;
	private final HashTreesIdProvider treeIdProvider;
	private final HashTreeSyncManagerStore syncManagerStore;
	private final ServerName localServer;
	private final int noOfThreads;
	private final long fullRebuildTimeInterval;
	private final long intBetweenSynchAndRebuild;

	@LockedBy("serversToSync")
	private final ConcurrentMap<ServerName, Single<HashTreeSyncInterface.Iface>> serversToSync = new ConcurrentSkipListMap<>();
	private final ConcurrentMap<Pair<ServerName, Long>, Pair<Long, Boolean>> serverWTreeIdAndLastBuildRequestTSMap = new ConcurrentHashMap<>();
	private final ConcurrentMap<Pair<ServerName, Long>, Long> serverWTreeIdAndLastSyncedTSMap = new ConcurrentHashMap<>();
	private final ScheduledExecutorService stateMgrExecutor = Executors
			.newScheduledThreadPool(1, new CustomThreadFactory(HT_SM_THREAD));
	private final AtomicBoolean initialized = new AtomicBoolean(false);
	private final AtomicBoolean stopped = new AtomicBoolean(false);

	private volatile ExecutorService fixedExecutors;
	private volatile STATE currState = STATE.START;
	private volatile HashTreesThriftServerTask htThriftServer;

	private static class ServerRemovedFromSyncList extends Exception {

		private static final long serialVersionUID = -7708009928736413442L;

		public ServerRemovedFromSyncList(ServerName sn) {
			super(sn + " is removed from sync list.");
		}
	}

	public HashTreesSyncManagerImpl(String thisHostName, HashTrees hashTree,
			HashTreesIdProvider treeIdProvider,
			HashTreeSyncManagerStore syncMgrStore, int serverPortNo) {
		this(hashTree, treeIdProvider, syncMgrStore, thisHostName,
				serverPortNo, DEFAULT_FULL_REBUILD_TIME_INTERVAL,
				DEFAULT_INTERVAL_BW_SYNCH_AND_REBUILD, DEFAULT_NO_OF_THREADS);
	}

	public HashTreesSyncManagerImpl(HashTrees hashTree,
			HashTreesIdProvider treeIdProvider,
			HashTreeSyncManagerStore syncMgrStore, String localHostName,
			int localServerPortNo, long fullRebuildTimeInterval,
			long intBWSynchAndRebuild, int noOfBGThreads) {
		this.localServer = new ServerName(localHostName, localServerPortNo);
		this.hashTree = hashTree;
		this.syncManagerStore = syncMgrStore;
		this.treeIdProvider = treeIdProvider;
		this.fullRebuildTimeInterval = fullRebuildTimeInterval;
		this.noOfThreads = noOfBGThreads;
		this.intBetweenSynchAndRebuild = intBWSynchAndRebuild;
		List<ServerName> serversToSyncInStore = syncMgrStore.getAllServers();
		for (ServerName sn : serversToSyncInStore)
			addServerToSyncList(sn);
	}

	@Override
	public void onRebuildHashTreeResponse(ServerName sn, long treeId,
			long tokenNo) {
		Pair<ServerName, Long> snAndTid = Pair.create(sn, treeId);
		Pair<Long, Boolean> tsAndResponse = serverWTreeIdAndLastBuildRequestTSMap
				.get(snAndTid);
		if (tsAndResponse != null && tsAndResponse.getFirst().equals(tokenNo)) {
			Pair<Long, Boolean> updatedResponse = Pair.create(tokenNo, true);
			serverWTreeIdAndLastBuildRequestTSMap.replace(snAndTid,
					tsAndResponse, updatedResponse);
		}
	}

	@Override
	public void onRebuildHashTreeRequest(ServerName sn, long treeId,
			long tokenNo, long expFullRebuildTimeInt) throws Exception {
		boolean fullRebuild = (System.currentTimeMillis() - hashTree
				.getLastFullyRebuiltTimeStamp(treeId)) > expFullRebuildTimeInt ? true
				: false;
		hashTree.rebuildHashTree(treeId, fullRebuild);
		HashTreeSyncInterface.Iface client = getHashTreeSyncClient(sn);
		client.postRebuildHashTreeResponse(localServer, treeId, tokenNo);
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
			boolean fullRebuild;
			try {
				fullRebuild = (System.currentTimeMillis() - hashTree
						.getLastFullyRebuiltTimeStamp(treeId)) > fullRebuildTimeInterval ? true
						: false;
				sendRequestForRebuild(treeId);
				treeIdAndRebuildType.add(Pair.create(treeId, fullRebuild));
			} catch (Exception e) {
				LOG.warn("Exception occurred while rebuilding.", e);
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
								hashTree.rebuildHashTree(input.getFirst(),
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
		for (ServerName sn : serversToSync.keySet()) {
			Pair<ServerName, Long> serverNameWTreeId = Pair.create(sn, treeId);
			try {
				long buildReqTS = System.currentTimeMillis();
				HashTreeSyncInterface.Iface client = getHashTreeSyncClient(sn);
				client.rebuildHashTree(sn, treeId, buildReqTS,
						DEFAULT_MAX_UNSYNCED_TIME_INTERVAL);
				serverWTreeIdAndLastSyncedTSMap.putIfAbsent(serverNameWTreeId,
						buildReqTS);
				serverWTreeIdAndLastBuildRequestTSMap.put(serverNameWTreeId,
						Pair.create(buildReqTS, false));
			} catch (TException | ServerRemovedFromSyncList e) {
				LOG.warn("Unable to send rebuild notification to "
						+ serverNameWTreeId, e);
			}
		}
	}

	private void synch() {
		Iterator<Long> treeIds = treeIdProvider.getAllPrimaryTreeIds();
		List<Pair<ServerName, Long>> hostNameAndTreeIdList = new ArrayList<>();

		while (treeIds.hasNext()) {
			long treeId = treeIds.next();

			for (ServerName sn : serversToSync.keySet()) {
				Pair<ServerName, Long> serverNameATreeId = Pair.create(sn,
						treeId);

				Pair<Long, Boolean> lastBuildReqTSAndResponse = serverWTreeIdAndLastBuildRequestTSMap
						.remove(serverNameATreeId);
				Long unsyncedTime = serverWTreeIdAndLastSyncedTSMap
						.get(serverNameATreeId);

				if (unsyncedTime == null || lastBuildReqTSAndResponse == null) {
					LOG.debug("Unsynced info entry is not available. Synch should be followed by rebuild. Skipping syncing "
							+ serverNameATreeId);
					continue;
				}

				try {
					if ((lastBuildReqTSAndResponse.getSecond())
							|| ((System.currentTimeMillis() - unsyncedTime) > DEFAULT_MAX_UNSYNCED_TIME_INTERVAL)) {
						hostNameAndTreeIdList.add(serverNameATreeId);
						serverWTreeIdAndLastSyncedTSMap
								.remove(serverNameATreeId);
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

		if (hostNameAndTreeIdList.size() == 0) {
			LOG.info("There is no synch required for any remote trees. Skipping this cycle.");
			return;
		}
		LOG.info("Synching remote hash trees.");
		Collection<Callable<Void>> syncTasks = Collections2.transform(
				hostNameAndTreeIdList,
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
			HashTreeSyncInterface.Iface remoteSyncClient = getHashTreeSyncClient(sn);
			hashTree.synch(treeId, new HashTreesRemoteClient(remoteSyncClient));
			LOG.info("Syncing " + hostNameAndTreeId + " complete.");
		} catch (TException | ServerRemovedFromSyncList e) {
			LOG.warn("Unable to synch remote hash tree server : "
					+ hostNameAndTreeId, e);
		}
	}

	private HashTreeSyncInterface.Iface getHashTreeSyncClient(ServerName sn)
			throws TTransportException, ServerRemovedFromSyncList {
		Single<HashTreeSyncInterface.Iface> wrappedObj = serversToSync.get(sn);
		if (wrappedObj != null && wrappedObj.getData() == null) {
			synchronized (serversToSync) {
				wrappedObj = serversToSync.get(sn);
				if (wrappedObj != null && wrappedObj.getData() == null)
					serversToSync.put(sn, Single
							.create(HashTreesThriftClientProvider
									.getThriftHashTreeClient(sn)));
			}
		}
		if (wrappedObj == null || wrappedObj.getData() == null)
			throw new ServerRemovedFromSyncList(sn);
		return wrappedObj.getData();
	}

	public void init() {
		if (initialized.compareAndSet(false, true)) {
			this.fixedExecutors = Executors.newFixedThreadPool(noOfThreads,
					new CustomThreadFactory(HT_THREAD_NAME));
			CountDownLatch initializedLatch = new CountDownLatch(1);
			htThriftServer = new HashTreesThriftServerTask(hashTree, this,
					localServer.getPortNo(), initializedLatch);
			new Thread(htThriftServer).start();
			try {
				initializedLatch.await();
			} catch (InterruptedException e) {
				LOG.warn(
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

	public void shutdown() {
		if (stopped.compareAndSet(false, true)) {
			CountDownLatch localLatch = new CountDownLatch(1);
			super.stop(localLatch);
			try {
				localLatch.await();
			} catch (InterruptedException e) {
				LOG.warn("Exception occurred while stopping the operations.", e);
			}
			if (htThriftServer != null)
				htThriftServer.stop();
			if (stateMgrExecutor != null)
				stateMgrExecutor.shutdown();
			if (fixedExecutors != null)
				fixedExecutors.shutdown();
			LOG.info("Hash tree sync manager operations stopped.");
		} else
			LOG.info("Hash tree sync manager operations stopped already. No actions were taken.");
	}

	@Override
	public void addServerToSyncList(ServerName sn) {
		synchronized (serversToSync) {
			if (!serversToSync.containsKey(sn)) {
				try {
					syncManagerStore.addServerToSyncList(sn);
					serversToSync.put(sn, Single
							.create(HashTreesThriftClientProvider
									.getThriftHashTreeClient(sn)));
				} catch (TTransportException e) {
					HashTreeSyncInterface.Iface dummy = null;
					serversToSync.put(sn, Single.create(dummy));
				}
				LOG.info(sn + " has been added to sync list.");
			} else
				LOG.info(sn + "has been already on the sync list.");
		}
	}

	@Override
	public void removeServerFromSyncList(ServerName sn) {
		boolean removed;
		synchronized (serversToSync) {
			syncManagerStore.removeServerFromSyncList(sn);
			removed = (serversToSync.remove(sn) != null) ? true : false;
		}
		if (removed)
			LOG.info(sn + "has been removed from sync list.");
		else
			LOG.info(sn + " was not in the sync list to be removed.");
	}
}
