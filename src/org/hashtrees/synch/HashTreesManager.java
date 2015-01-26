package org.hashtrees.synch;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.hashtrees.HashTrees;
import org.hashtrees.HashTreesIdProvider;
import org.hashtrees.SyncDiffResult;
import org.hashtrees.SyncType;
import org.hashtrees.thrift.generated.RebuildHashTreeRequest;
import org.hashtrees.thrift.generated.RebuildHashTreeResponse;
import org.hashtrees.thrift.generated.ServerName;
import org.hashtrees.util.CustomThreadFactory;
import org.hashtrees.util.Pair;
import org.hashtrees.util.Service;
import org.hashtrees.util.StoppableTask;
import org.hashtrees.util.TaskQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterators;

/**
 * A hashtrees manager which runs background tasks to rebuild hash trees, and
 * synch remote hash trees.
 * 
 * HashTrees updates tree hashes at regular intervals, not on every update of
 * the key. Hence if two hash trees are continuously updating their hashes at
 * different intervals, synch operation between two trees will always cause a
 * mismatch even though underlying data is same. We should avoid unnecessary
 * network transfers. Thus a different approach is used where whenever the
 * primary hash tree rebuilds its hash tree, it requests the remote hash tree to
 * rebuild the hash tree as well. So the following synch operation will not
 * differ much in segment hash.
 * 
 * HashTreesManager goes through the following states.
 * 
 * START -> (REBUILD followed by SYNCH -> (pause) *) -> STOP(when requested).
 * 
 * At any time, the manager can be asked to shutdown, by requesting stop.
 * 
 * {@link HashTrees} is a stand alone class, it does not do automatically build
 * segments or any additional synch functionalities, this class provides those
 * functions.
 * 
 */
public class HashTreesManager extends StoppableTask implements
		HashTreesSyncCallsObserver, Service {

	private final static Logger LOG = LoggerFactory
			.getLogger(HashTreesManager.class);
	private final static String HT_MGR_TPOOL = "HTMgrWorkerThreadPool";
	private final static String HT_MGR_SCHED_THREAD = "HTMgrSchedulerThread";
	private final static String HT_THRIFT_SERVER_THREAD = "HTThriftServerThread";
	private final static long MAX_UNSYNCED_TIME = 10 * 60 * 1000; // in
																	// milliseconds

	private final int noOfThreads;
	private final long fullRebuildPeriod, period;
	private final boolean synchEnabled, rebuildEnabled;
	private final ServerName localServer;
	private final HashTrees hashTrees;
	private final HashTreesIdProvider treeIdProvider;
	private final HashTreesSynchListProvider syncListProvider;
	private final HashTreesSynchAuthenticator authenticator;
	private final SyncType syncType;

	private final ConcurrentLinkedQueue<HashTreesManagerObserver> observers = new ConcurrentLinkedQueue<>();
	private final ConcurrentSkipListMap<ServerName, HashTreesRemoteClient> servers = new ConcurrentSkipListMap<>();
	private final ConcurrentMap<Pair<ServerName, Long>, Pair<Long, Boolean>> remoteTreeAndLastBuildReqTS = new ConcurrentHashMap<>();
	private final ConcurrentMap<Pair<ServerName, Long>, Long> remoteTreeAndLastSyncedTS = new ConcurrentHashMap<>();

	private final AtomicBoolean initialized = new AtomicBoolean(false);
	private final AtomicBoolean stopped = new AtomicBoolean(false);

	private volatile ExecutorService threadPool;
	private volatile ScheduledExecutorService scheduledExecutor;
	private volatile HashTreesThriftServerTask htThriftServer;

	public HashTreesManager(int noOfThreads, long period,
			long fullRebuildPeriod, boolean rebuildEnabled,
			boolean synchEnabled, ServerName localServer, HashTrees hashTrees,
			HashTreesIdProvider treeIdProvider,
			HashTreesSynchListProvider syncListProvider,
			HashTreesSynchAuthenticator authenticator, SyncType syncType) {
		this.noOfThreads = noOfThreads;
		this.period = period;
		this.fullRebuildPeriod = fullRebuildPeriod;
		this.synchEnabled = synchEnabled;
		this.rebuildEnabled = rebuildEnabled;
		this.localServer = localServer;
		this.syncListProvider = syncListProvider;
		this.hashTrees = hashTrees;
		this.treeIdProvider = treeIdProvider;
		this.authenticator = authenticator;
		this.syncType = syncType;
	}

	@Override
	public void onRebuildHashTreeResponse(RebuildHashTreeResponse response) {
		LOG.info("Rebuild response arrived : {}.", response);
		Pair<ServerName, Long> snAndTid = Pair.create(response.responder,
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
		LOG.info("Rebuild response : {} - processed.", response);
	}

	@Override
	public void onRebuildHashTreeRequest(RebuildHashTreeRequest request)
			throws IOException {
		LOG.info("Rebuild request arrived : {} .", request);
		try {
			rebuildHashTree(request.treeId, request.expFullRebuildTimeInt);
			HashTreesRemoteClient client = getHashTreeSyncClient(request.requester);
			RebuildHashTreeResponse response = new RebuildHashTreeResponse(
					localServer, request.treeId, request.tokenNo);
			client.submitRebuildResponse(response);
		} catch (TException e) {
			throw new IOException(e);
		}
		LOG.info("Rebuild request : {} - processed", request);
	}

	private void rebuildAllLocalTrees() {
		Iterator<Callable<Void>> rebuildTasks = Iterators.transform(
				treeIdProvider.getAllPrimaryTreeIds(),
				new Function<Long, Callable<Void>>() {

					@Override
					public Callable<Void> apply(final Long treeId) {
						return new Callable<Void>() {

							@Override
							public Void call() throws IOException {
								sendRebuildRequestToRemoteTrees(treeId);
								rebuildHashTree(treeId, fullRebuildPeriod);
								return null;
							}
						};
					}
				});

		LOG.info("Building locally managed trees.");
		TaskQueue<Void> taskQueue = new TaskQueue<Void>(threadPool,
				rebuildTasks, noOfThreads);
		while (taskQueue.hasNext()) {
			try {
				taskQueue.next().get();
			} catch (ExecutionException | InterruptedException e) {
				LOG.info("Failure occurred in build task.", e);
			}
			if (hasStopRequested()) {
				taskQueue.stopAsync();
			}
		}
		LOG.info("No of successful/failed rebuild tasks : "
				+ taskQueue.getPasseTasksCount() + "/"
				+ taskQueue.getFailedTasksCount());
		LOG.info("Building locally managed trees - Done");
	}

	private void sendRebuildRequestToRemoteTrees(long treeId) {
		Iterator<ServerName> serverItr = syncListProvider.getServerNameListFor(
				treeId).iterator();
		while (serverItr.hasNext()) {
			ServerName sn = serverItr.next();
			Pair<ServerName, Long> serverNameWTreeId = Pair.create(sn, treeId);
			try {
				long buildReqTS = System.currentTimeMillis();
				HashTreesRemoteClient client = getHashTreeSyncClient(sn);
				remoteTreeAndLastSyncedTS.putIfAbsent(serverNameWTreeId,
						buildReqTS);
				remoteTreeAndLastBuildReqTS.put(serverNameWTreeId,
						Pair.create(buildReqTS, false));
				RebuildHashTreeRequest request = new RebuildHashTreeRequest(
						localServer, treeId, buildReqTS, fullRebuildPeriod);
				client.submitRebuildRequest(request);
			} catch (TException | IOException e) {
				LOG.error("Unable to send rebuild notification to {} - {}",
						serverNameWTreeId, e.getMessage(), e);
			}
		}
	}

	private void synchAllRemoteTrees() {
		final Iterator<Long> treeIds = treeIdProvider.getAllPrimaryTreeIds();
		Iterator<Pair<ServerName, Long>> remoteTreeIterator = new Iterator<Pair<ServerName, Long>>() {

			private final ConcurrentLinkedQueue<Pair<ServerName, Long>> intQue = new ConcurrentLinkedQueue<>();

			@Override
			public Pair<ServerName, Long> next() {
				if (!hasNext())
					throw new NoSuchElementException(
							"No more elements exist to return.");
				return intQue.remove();
			}

			@Override
			public boolean hasNext() {
				loadNextElement();
				return !intQue.isEmpty();
			}

			private void loadNextElement() {
				if (intQue.isEmpty()) {
					while (treeIds.hasNext()) {
						long treeId = treeIds.next();
						for (ServerName sn : syncListProvider
								.getServerNameListFor(treeId)) {
							Pair<ServerName, Long> serverNameATreeId = Pair
									.create(sn, treeId);
							Pair<Long, Boolean> lastBuildReqTSAndResponse = remoteTreeAndLastBuildReqTS
									.remove(serverNameATreeId);
							Long unsyncedTime = remoteTreeAndLastSyncedTS
									.get(serverNameATreeId);

							if (unsyncedTime == null
									|| lastBuildReqTSAndResponse == null) {
								LOG.info(
										"Unsynced info entry is not available. Synch should be followed by rebuild. Skipping syncing {}.",
										serverNameATreeId);
								continue;
							}

							if ((lastBuildReqTSAndResponse.getSecond())
									|| ((System.currentTimeMillis() - unsyncedTime) > MAX_UNSYNCED_TIME)) {
								intQue.add(serverNameATreeId);
								remoteTreeAndLastSyncedTS
										.remove(serverNameATreeId);
							} else {
								LOG.info(
										"Did not receive confirmation from {} for the rebuilding. Not syncing the remote node.",
										serverNameATreeId);
							}
						}
						if (!intQue.isEmpty())
							break;
					}
				}
			}
		};

		Iterator<Callable<Void>> syncTasks = Iterators.transform(
				remoteTreeIterator,
				new Function<Pair<ServerName, Long>, Callable<Void>>() {

					@Override
					public Callable<Void> apply(
							final Pair<ServerName, Long> serverNameAndTreeId) {
						return new Callable<Void>() {

							@Override
							public Void call() throws IOException,
									SynchNotAllowedException {
								synch(serverNameAndTreeId.getFirst(),
										serverNameAndTreeId.getSecond(), false,
										syncType);
								return null;
							}
						};
					}
				});

		LOG.info("Synching remote hash trees.");
		TaskQueue<Void> taskQueue = new TaskQueue<>(threadPool, syncTasks,
				noOfThreads);
		while (taskQueue.hasNext()) {
			try {
				taskQueue.next().get();
			} catch (InterruptedException | ExecutionException e) {
				LOG.error("Exception occurred in synch task.", e);
			}
			if (hasStopRequested()) {
				taskQueue.stopAsync();
			}
		}
		LOG.info("No of successful/failed synch tasks : {} / {}",
				taskQueue.getPasseTasksCount(), taskQueue.getFailedTasksCount());
		LOG.info("Synching remote hash trees - Done");
	}

	public void synch(ServerName sn, long treeId) throws IOException,
			SynchNotAllowedException {
		synch(sn, treeId, true, SyncType.UPDATE);
	}

	private void rebuildHashTree(final long treeId, long fullRebuildPeriod)
			throws IOException {

		notifyObservers(new Function<HashTreesManagerObserver, Void>() {

			@Override
			public Void apply(HashTreesManagerObserver htmObserver) {
				htmObserver.preRebuild(treeId);
				return null;
			}
		});
		Stopwatch watch = Stopwatch.createStarted();
		int dirtySegsCount = hashTrees.rebuildHashTree(treeId,
				fullRebuildPeriod);
		watch.stop();
		LOG.info("Total no of dirty segments : {} ", dirtySegsCount);
		LOG.info("Time taken for rebuilding (treeId: {}) (in ms) :", treeId,
				watch.elapsed(TimeUnit.MILLISECONDS));
		notifyObservers(new Function<HashTreesManagerObserver, Void>() {

			@Override
			public Void apply(HashTreesManagerObserver htmObserver) {
				htmObserver.postRebuild(treeId);
				return null;
			}
		});
	}

	public void synch(final ServerName sn, final long treeId,
			boolean doAuthenticate, SyncType syncType) throws IOException,
			SynchNotAllowedException {
		boolean synchAllowed = doAuthenticate ? authenticator.canSynch(
				localServer, sn) : true;
		Pair<ServerName, Long> hostNameAndTreeId = Pair.create(sn, treeId);
		if (synchAllowed) {
			try {
				notifyObservers(new Function<HashTreesManagerObserver, Void>() {

					@Override
					public Void apply(HashTreesManagerObserver input) {
						input.preSync(treeId, sn);
						return null;
					}
				});
				LOG.info("Syncing {}.", hostNameAndTreeId);
				Stopwatch watch = Stopwatch.createStarted();
				HashTreesRemoteClient remoteSyncClient = getHashTreeSyncClient(sn);
				SyncDiffResult result = hashTrees.synch(treeId,
						remoteSyncClient, syncType);
				LOG.info("Synch result for {} - {}", hostNameAndTreeId, result);
				watch.stop();
				LOG.info("Time taken for syncing ({}) (in ms) : {}",
						hostNameAndTreeId, watch.elapsed(TimeUnit.MILLISECONDS));
				LOG.info("Syncing {} complete.", hostNameAndTreeId);
				notifyObservers(new Function<HashTreesManagerObserver, Void>() {

					@Override
					public Void apply(HashTreesManagerObserver htmObserver) {
						htmObserver.postSync(treeId, sn);
						return null;
					}
				});
			} catch (TException e) {
				LOG.error("Unable to synch remote hash tree server {} : {}",
						hostNameAndTreeId, e);
			}
		} else {
			LOG.error("Synch is not allowed between {} and {}", localServer, sn);
			throw new SynchNotAllowedException(localServer, sn);
		}
	}

	private HashTreesRemoteClient getHashTreeSyncClient(ServerName sn)
			throws TTransportException {
		HashTreesRemoteClient client = servers.get(sn);
		if (client == null) {
			servers.putIfAbsent(sn, new HashTreesRemoteClient(sn));
			client = servers.get(sn);
		}
		return client;
	}

	public void addObserver(HashTreesManagerObserver observer) {
		observers.add(observer);
	}

	public void removeObserver(HashTreesManagerObserver observer) {
		observers.remove(observer);
	}

	private void notifyObservers(
			Function<HashTreesManagerObserver, Void> function) {
		Iterator<Void> itr = Iterators
				.transform(observers.iterator(), function);
		while (itr.hasNext())
			itr.next();
	}

	@Override
	public void start() {
		if (initialized.compareAndSet(false, true)) {
			LOG.info("Hash tree sync manager operations starting.");
			String hostNameAndPortNo = localServer.toString();
			String threadPoolName = HT_MGR_TPOOL + "," + hostNameAndPortNo;
			String executorThreadName = HT_MGR_SCHED_THREAD + ","
					+ hostNameAndPortNo;
			threadPool = Executors.newFixedThreadPool(noOfThreads,
					new CustomThreadFactory(threadPoolName));
			scheduledExecutor = Executors.newScheduledThreadPool(1,
					new CustomThreadFactory(executorThreadName));
			CountDownLatch initializedLatch = new CountDownLatch(1);
			htThriftServer = new HashTreesThriftServerTask(hashTrees, this,
					syncListProvider, localServer.getPortNo(), initializedLatch);
			new Thread(htThriftServer, HT_THRIFT_SERVER_THREAD).start();
			try {
				initializedLatch.await();
			} catch (InterruptedException e) {
				LOG.error(
						"Exception occurred while waiting for the server to start",
						e);
			}
			scheduledExecutor.scheduleWithFixedDelay(this, 0, period,
					TimeUnit.MILLISECONDS);
		} else {
			LOG.info("HashTreeSyncManager initialized already.");
			return;
		}
	}

	@Override
	public void runImpl() {
		LOG.info("Executing rebuild/synch operations.");
		if (rebuildEnabled)
			rebuildAllLocalTrees();
		if (synchEnabled)
			synchAllRemoteTrees();
		LOG.info("Executing rebuild/synch operations - Done.");
	}

	@Override
	public void stop() {
		if (stopped.compareAndSet(false, true)) {
			LOG.info("Stopping hash trees manager operations.");
			CountDownLatch localLatch = new CountDownLatch(1);
			super.stopAsync(localLatch);
			try {
				localLatch.await();
			} catch (InterruptedException e) {
				LOG.error("Exception occurred while stopping the operations.",
						e);
			}
			if (scheduledExecutor != null)
				scheduledExecutor.shutdown();
			if (htThriftServer != null)
				htThriftServer.stopAsync();
			if (threadPool != null)
				threadPool.shutdown();
			LOG.info("Stopping hash trees manager operations - Done.");
		} else
			LOG.info("Hash trees manager operations stopped already. No actions were taken.");
	}

	@NotThreadSafe
	public static class Builder {

		public final static int DEF_NO_OF_THREADS = 10;
		// Default scheduling time interval
		public final static long DEF_SCHEDULE_PERIOD = 5 * 60 * 1000;

		private final ServerName localServer;
		private final HashTrees hashTrees;
		private final HashTreesIdProvider treeIdProvider;
		private final HashTreesSynchListProvider syncListProvider;

		private long period = DEF_SCHEDULE_PERIOD, fullRebuildPeriod = -1;
		private int noOfThreads = DEF_NO_OF_THREADS;
		private boolean rebuildEnabled = true, syncEnabled = true;
		private HashTreesSynchAuthenticator authenticator;
		private SyncType syncType;

		public Builder(String serverName, int portNo, HashTrees hashTrees,
				HashTreesIdProvider treeIdProvider,
				HashTreesSynchListProvider syncListProvider) {
			this.localServer = new ServerName(serverName, portNo);
			this.hashTrees = hashTrees;
			this.treeIdProvider = treeIdProvider;
			this.syncListProvider = syncListProvider;
		}

		/**
		 * Schedules rebuild and synch operation periodically. First execution
		 * will begin after calling {@link HashTreesManager#start()}. The
		 * following execution will begin after 'period' time interval from the
		 * first task's completion.
		 * 
		 * Default value is 5 minutes.
		 * 
		 * @param period
		 *            , in milliseconds.
		 * @return
		 */
		public Builder schedule(long period) {
			this.period = period;
			return this;
		}

		/**
		 * Allows to execute full rebuild on hash trees.This will be triggered
		 * if a tree is not fully rebuilt for more than fullRebuildPeriod. By
		 * default fullRebuild is never called on {@link HashTrees}.
		 * 
		 * @param fullRebuildPeriod
		 * @return
		 */
		public Builder setFullRebuildPeriod(long fullRebuildPeriod) {
			this.fullRebuildPeriod = fullRebuildPeriod;
			return this;
		}

		/**
		 * Sets no of threads to be created by thread pool. Thread pool is used
		 * for rebuild/synch operations. By default 10 threads are used.
		 * 
		 * @param noOfThreads
		 * @return
		 */
		public Builder setNoOfThreads(int noOfThreads) {
			this.noOfThreads = noOfThreads;
			return this;
		}

		/**
		 * Disables rebuild operation. By default this is enabled.
		 * 
		 * @return
		 */
		public Builder disableRebuild() {
			this.rebuildEnabled = false;
			return this;
		}

		/**
		 * Disables synch operation. By default this is enabled.
		 * 
		 * @param enable
		 * @return
		 */
		public Builder disableSync() {
			this.syncEnabled = false;
			return this;
		}

		public Builder setSyncType(SyncType syncType) {
			this.syncType = syncType;
			return this;
		}

		public HashTreesManager build() {
			if (authenticator == null)
				authenticator = new AllowAllSynchAuthenticator();
			if (syncType == null)
				syncType = SyncType.UPDATE;
			return new HashTreesManager(noOfThreads, period, fullRebuildPeriod,
					rebuildEnabled, syncEnabled, localServer, hashTrees,
					treeIdProvider, syncListProvider, authenticator, syncType);
		}
	}
}