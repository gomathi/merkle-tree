package org.hashtrees;

import static org.hashtrees.TreeUtils.getImmediateChildren;
import static org.hashtrees.TreeUtils.getNoOfNodes;
import static org.hashtrees.TreeUtils.getParent;
import static org.hashtrees.TreeUtils.height;
import static org.hashtrees.util.ByteUtils.sha1;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.commons.codec.binary.Hex;
import org.hashtrees.storage.HashTreeMemStorage;
import org.hashtrees.storage.HashTreeStorage;
import org.hashtrees.storage.Storage;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;
import org.hashtrees.util.ByteUtils;
import org.hashtrees.util.CollectionPeekingIterator;
import org.hashtrees.util.LockedBy;
import org.hashtrees.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HashTree has segment blocks and segment trees.
 * 
 * 1) Segment blocks, where the (key, hash of value) pairs are stored. All the
 * pairs are stored in sorted order. Whenever a key addition/removal happens on
 * the node, HashTree segment is updated. Keys are distributed using uniform
 * hash distribution. Max no of segments is {@link #MAX_NO_OF_BUCKETS}.
 * 
 * 2) Segment trees, where the segments' hashes are updated and maintained. Tree
 * is not updated on every update on a segment. Rather, tree update is happening
 * at regular intervals.Tree can be binary or 4-ary tree.
 * 
 * HashTree can host multiple hash trees. Each hash tree is differentiated by a
 * tree id.
 * 
 * Uses {@link HashTreeStorage} for storing tree and segments.
 * {@link HashTreeMemStorage} provides in memory implementation of storing
 * entire tree and segments.
 * 
 */
@ThreadSafe
public class HashTreeImpl implements HashTree {

	private final static char COMMA_DELIMETER = ',';
	private final static char NEW_LINE_DELIMETER = '\n';
	private final static int ROOT_NODE = 0;
	private final static int MAX_NO_OF_BUCKETS = 1 << 30;
	private final static int BINARY_TREE = 2;
	private final static Logger LOGGER = LoggerFactory
			.getLogger(HashTreeImpl.class.getName());

	private final int noOfChildren;
	private final int internalNodesCount;
	private final int segmentsCount;

	private final HashTreeStorage hTStorage;
	private final HashTreeIdProvider treeIdProvider;
	private final SegmentIdProvider segIdProvider;
	private final Storage storage;

	private final ConcurrentMap<Long, ReentrantLock> treeLocks = new ConcurrentHashMap<Long, ReentrantLock>();

	private final Object nonBlockingCallsLock = new Object();
	@LockedBy("nonBlockingCallsLock")
	private volatile boolean enabledNonBlockingCalls;
	@LockedBy("nonBlockingCallsLock")
	private volatile NonBlockingHashTreeDataUpdater bgDataUpdater;

	public HashTreeImpl(int noOfSegments,
			final HashTreeIdProvider treeIdProvider,
			final SegmentIdProvider segIdProvider,
			final HashTreeStorage hTStroage, final Storage storage) {
		this.noOfChildren = BINARY_TREE;
		this.segmentsCount = ((noOfSegments > MAX_NO_OF_BUCKETS) || (noOfSegments < 0)) ? MAX_NO_OF_BUCKETS
				: roundUpToPowerOf2(noOfSegments);
		this.internalNodesCount = getNoOfNodes(
				(height(this.segmentsCount, noOfChildren) - 1), noOfChildren);
		this.treeIdProvider = treeIdProvider;
		this.segIdProvider = segIdProvider;
		this.hTStorage = hTStroage;
		this.storage = storage;
	}

	@Override
	public void hPut(final ByteBuffer key, final ByteBuffer value) {
		if (enabledNonBlockingCalls) {
			List<ByteBuffer> input = new ArrayList<ByteBuffer>();
			input.add(key);
			input.add(value);
			bgDataUpdater.enque(Pair.create(HTOperation.PUT, input));
		} else
			hPutInternal(key, value);
	}

	void hPutInternal(final ByteBuffer key, final ByteBuffer value) {
		long treeId = treeIdProvider.getTreeId(key);
		int segId = segIdProvider.getSegmentId(key);
		ByteBuffer digest = ByteBuffer.wrap(sha1(value.array()));
		hTStorage.putSegmentData(treeId, segId, key, digest);
		hTStorage.setDirtySegment(treeId, segId);
	}

	@Override
	public void hRemove(final ByteBuffer key) {
		if (enabledNonBlockingCalls) {
			List<ByteBuffer> input = new ArrayList<ByteBuffer>();
			input.add(key);
			bgDataUpdater.enque(Pair.create(HTOperation.REMOVE, input));
		} else {
			hRemoveInternal(key);
		}
	}

	void hRemoveInternal(final ByteBuffer key) {
		long treeId = treeIdProvider.getTreeId(key);
		int segId = segIdProvider.getSegmentId(key);
		hTStorage.deleteSegmentData(treeId, segId, key);
		hTStorage.setDirtySegment(treeId, segId);
	}

	@Override
	public boolean synch(long treeId, final HashTree remoteTree)
			throws Exception {

		Collection<Integer> leafNodesToCheck = new ArrayList<Integer>();
		Collection<Integer> missingNodesInRemote = new ArrayList<Integer>();
		List<Integer> missingNodesInLocal = new ArrayList<Integer>();

		findDifferences(treeId, remoteTree, leafNodesToCheck,
				missingNodesInRemote, missingNodesInLocal);

		if (leafNodesToCheck.isEmpty() && missingNodesInLocal.isEmpty()
				&& missingNodesInRemote.isEmpty())
			return false;

		Collection<Integer> segsToCheck = getSegmentIdsFromLeafIds(leafNodesToCheck);
		syncSegments(treeId, segsToCheck, remoteTree);

		Collection<Integer> missingSegsInRemote = getSegmentIdsFromLeafIds(getAllLeafNodeIds(missingNodesInRemote));
		updateRemoteTreeWithMissingSegments(treeId, missingSegsInRemote,
				remoteTree);

		remoteTree.deleteTreeNodes(treeId, missingNodesInLocal);
		return true;
	}

	private void findDifferences(long treeId, HashTree remoteTree,
			Collection<Integer> nodesToCheck,
			Collection<Integer> missingNodesInRemote,
			Collection<Integer> missingNodesInLocal) throws Exception {
		CollectionPeekingIterator<SegmentHash> localItr = null, remoteItr = null;
		SegmentHash local, remote;

		List<Integer> pQueue = new ArrayList<Integer>();
		pQueue.add(ROOT_NODE);
		while (!pQueue.isEmpty()) {

			localItr = new CollectionPeekingIterator<SegmentHash>(
					getSegmentHashes(treeId, pQueue));
			remoteItr = new CollectionPeekingIterator<SegmentHash>(
					remoteTree.getSegmentHashes(treeId, pQueue));
			pQueue = new ArrayList<Integer>();
			while (localItr.hasNext() && remoteItr.hasNext()) {
				local = localItr.peek();
				remote = remoteItr.peek();

				if (local.getNodeId() == remote.getNodeId()) {
					if (!Arrays.equals(local.getHash(), remote.getHash())) {
						if (isLeafNode(local.getNodeId()))
							nodesToCheck.add(local.getNodeId());
						else
							pQueue.addAll(getImmediateChildren(
									local.getNodeId(), noOfChildren));

					}
					localItr.next();
					remoteItr.next();
				} else if (local.getNodeId() < remote.getNodeId()) {
					missingNodesInRemote.add(local.getNodeId());
					localItr.next();
				} else {
					missingNodesInLocal.add(remote.getNodeId());
					remoteItr.next();
				}
			}
		}
		while (localItr != null && localItr.hasNext()) {
			missingNodesInRemote.add(localItr.next().getNodeId());
		}
		while (remoteItr != null && remoteItr.hasNext()) {
			missingNodesInLocal.add(remoteItr.next().getNodeId());
		}
	}

	private void syncSegments(long treeId, Collection<Integer> segIds,
			HashTree remoteTree) throws Exception {
		for (int segId : segIds)
			syncSegment(treeId, segId, remoteTree);
	}

	private void syncSegment(long treeId, int segId, HashTree remoteTree)
			throws Exception {
		CollectionPeekingIterator<SegmentData> localDataItr = new CollectionPeekingIterator<SegmentData>(
				getSegment(treeId, segId));
		CollectionPeekingIterator<SegmentData> remoteDataItr = new CollectionPeekingIterator<SegmentData>(
				remoteTree.getSegment(treeId, segId));

		Map<ByteBuffer, ByteBuffer> kvsForAddition = new HashMap<ByteBuffer, ByteBuffer>();
		List<ByteBuffer> keysForeRemoval = new ArrayList<ByteBuffer>();

		SegmentData local, remote;
		while (localDataItr.hasNext() && remoteDataItr.hasNext()) {
			local = localDataItr.peek();
			remote = remoteDataItr.peek();

			int compRes = ByteUtils.compareTo(local.getKey(), remote.getKey());
			if (compRes == 0) {
				if (!Arrays.equals(local.getDigest(), remote.getDigest()))
					kvsForAddition.put(ByteBuffer.wrap(local.getKey()),
							storage.get(ByteBuffer.wrap(local.getKey())));
				localDataItr.next();
				remoteDataItr.next();
			} else if (compRes < 0) {
				kvsForAddition.put(ByteBuffer.wrap(local.getKey()),
						storage.get(ByteBuffer.wrap(local.getKey())));
				localDataItr.next();
			} else {
				keysForeRemoval.add(ByteBuffer.wrap(remote.getKey()));
				remoteDataItr.next();
			}
		}
		while (localDataItr.hasNext()) {
			local = localDataItr.next();
			kvsForAddition.put(ByteBuffer.wrap(local.getKey()),
					storage.get(ByteBuffer.wrap(local.getKey())));
		}
		while (remoteDataItr.hasNext())
			keysForeRemoval.add(ByteBuffer.wrap(remoteDataItr.next().getKey()));

		if (kvsForAddition.size() > 0)
			remoteTree.sPut(kvsForAddition);
		if (keysForeRemoval.size() > 0)
			remoteTree.sRemove(keysForeRemoval);
	}

	private void updateRemoteTreeWithMissingSegments(long treeId,
			Collection<Integer> segIds, HashTree remoteTree) throws Exception {
		for (int segId : segIds) {
			final Map<ByteBuffer, ByteBuffer> keyValuePairs = new HashMap<ByteBuffer, ByteBuffer>();
			List<SegmentData> sdValues = getSegment(treeId, segId);
			for (SegmentData sd : sdValues)
				keyValuePairs.put(ByteBuffer.wrap(sd.getKey()),
						storage.get(ByteBuffer.wrap(sd.getKey())));
			if (sdValues.size() > 0)
				remoteTree.sPut(keyValuePairs);
		}
	}

	@Override
	public SegmentHash getSegmentHash(long treeId, int nodeId) {
		return hTStorage.getSegmentHash(treeId, nodeId);
	}

	@Override
	public List<SegmentHash> getSegmentHashes(long treeId,
			final List<Integer> nodeIds) {
		return hTStorage.getSegmentHashes(treeId, nodeIds);
	}

	@Override
	public SegmentData getSegmentData(long treeId, int segId, ByteBuffer key) {
		return hTStorage.getSegmentData(treeId, segId, key);
	}

	@Override
	public List<SegmentData> getSegment(long treeId, int segId) {
		return hTStorage.getSegment(treeId, segId);
	}

	private boolean acquireTreeLock(long treeId, boolean waitForLock) {
		if (!treeLocks.containsKey(treeId)) {
			ReentrantLock lock = new ReentrantLock();
			treeLocks.putIfAbsent(treeId, lock);
		}
		Lock lock = treeLocks.get(treeId);
		if (waitForLock) {
			lock.lock();
			return true;
		}
		return lock.tryLock();
	}

	private void releaseTreeLock(long treeId) {
		treeLocks.get(treeId).unlock();
	}

	@Override
	public void rebuildHashTrees(boolean fullRebuild) {
		Iterator<Long> treeIdItr = hTStorage.getAllTreeIds();
		while (treeIdItr.hasNext())
			rebuildHashTree(treeIdItr.next(), fullRebuild);
	}

	@Override
	public void rebuildHashTree(long treeId, boolean fullRebuild) {
		boolean acquiredLock = fullRebuild ? acquireTreeLock(treeId, true)
				: acquireTreeLock(treeId, false);
		if (acquiredLock) {
			try {
				long currentTs = System.currentTimeMillis();
				List<Integer> dirtySegmentBuckets = hTStorage
						.clearAndGetDirtySegments(treeId);

				Map<Integer, ByteBuffer> dirtyNodeAndDigestMap = rebuildLeaves(
						treeId, dirtySegmentBuckets);
				rebuildInternalNodes(treeId, dirtyNodeAndDigestMap);
				for (Map.Entry<Integer, ByteBuffer> dirtyNodeAndDigest : dirtyNodeAndDigestMap
						.entrySet())
					hTStorage.putSegmentHash(treeId,
							dirtyNodeAndDigest.getKey(),
							dirtyNodeAndDigest.getValue());
				if (fullRebuild)
					hTStorage.setLastFullyTreeBuiltTimestamp(treeId, currentTs);
				hTStorage.setLastHashTreeUpdatedTimestamp(treeId, currentTs);
			} finally {
				releaseTreeLock(treeId);
			}
		}
	}

	@Override
	public void sPut(final Map<ByteBuffer, ByteBuffer> keyValuePairs)
			throws Exception {
		for (Map.Entry<ByteBuffer, ByteBuffer> keyValuePair : keyValuePairs
				.entrySet())
			storage.put(keyValuePair.getKey(), keyValuePair.getValue());
	}

	@Override
	public void sRemove(final List<ByteBuffer> keys) throws Exception {
		for (ByteBuffer key : keys)
			storage.remove(key);
	}

	@Override
	public void deleteTreeNodes(long treeId, List<Integer> nodeIds)
			throws Exception {
		List<Integer> segIds = getSegmentIdsFromLeafIds(getAllLeafNodeIds(nodeIds));
		for (int segId : segIds) {
			Iterator<SegmentData> segDataItr = getSegment(treeId, segId)
					.iterator();
			while (segDataItr.hasNext()) {
				storage.remove(ByteBuffer.wrap(segDataItr.next().getKey()));
			}
		}
	}

	/**
	 * Rebuilds the dirty segments, and updates the segment hashes of the
	 * leaves.
	 * 
	 * @return, node ids, and uncommitted digest.
	 */
	private Map<Integer, ByteBuffer> rebuildLeaves(long treeId,
			final List<Integer> dirtySegments) {
		Map<Integer, ByteBuffer> dirtyNodeIdAndDigestMap = new HashMap<Integer, ByteBuffer>();
		for (int dirtySegId : dirtySegments) {
			ByteBuffer digest = digestSegmentData(treeId, dirtySegId);
			int nodeId = getLeafIdFromSegmentId(dirtySegId);
			dirtyNodeIdAndDigestMap.put(nodeId, digest);
		}
		return dirtyNodeIdAndDigestMap;
	}

	/**
	 * Concatenates the given ByteBuffer values by first converting them to the
	 * equivalent hex strings, and then concatenated by adding the comma
	 * delimiter.
	 * 
	 * @param values
	 * @return
	 */
	public static String getHexString(ByteBuffer... values) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < values.length - 1; i++) {
			sb.append(Hex.encodeHexString(values[i].array()) + COMMA_DELIMETER);
		}
		if (values.length > 0)
			sb.append(Hex.encodeHexString(values[values.length - 1].array()));
		return sb.toString();
	}

	private ByteBuffer digestSegmentData(long treeId, int segId) {
		List<SegmentData> dirtySegmentData = hTStorage
				.getSegment(treeId, segId);
		List<String> hexStrings = new ArrayList<String>();

		for (SegmentData sd : dirtySegmentData)
			hexStrings.add(getHexString(sd.key, sd.digest));

		return digestHexStrings(hexStrings);
	}

	/**
	 * 
	 * @param segDataList
	 * @return
	 */
	public static ByteBuffer digestByteBuffers(List<ByteBuffer> bbList) {
		List<String> hexStrings = new ArrayList<String>();
		for (ByteBuffer bb : bbList)
			hexStrings.add(Hex.encodeHexString(bb.array()));
		return digestHexStrings(hexStrings);
	}

	public static ByteBuffer digestHexStrings(List<String> hexStrings) {
		StringBuilder sb = new StringBuilder();
		for (String hexString : hexStrings)
			sb.append(hexString + NEW_LINE_DELIMETER);
		return ByteBuffer.wrap(sha1(sb.toString().getBytes()));
	}

	/**
	 * Updates the segment hashes iteratively for each level on the tree.
	 * 
	 * @param nodeIdAndDigestMap
	 */
	private void rebuildInternalNodes(long treeId,
			final Map<Integer, ByteBuffer> nodeIdAndDigestMap) {
		Set<Integer> parentNodeIds = new TreeSet<Integer>();
		Set<Integer> nodeIds = new TreeSet<Integer>();
		nodeIds.addAll(nodeIdAndDigestMap.keySet());

		while (!nodeIds.isEmpty()) {
			for (int nodeId : nodeIds)
				parentNodeIds.add(getParent(nodeId, noOfChildren));

			rebuildParentNodes(treeId, parentNodeIds, nodeIdAndDigestMap);

			nodeIds.clear();
			nodeIds.addAll(parentNodeIds);
			parentNodeIds.clear();

			if (nodeIds.contains(ROOT_NODE))
				break;
		}
	}

	/**
	 * For each parent id, gets all the child hashes, and updates the parent
	 * hash.
	 * 
	 * @param parentIds
	 */
	private void rebuildParentNodes(long treeId, final Set<Integer> parentIds,
			Map<Integer, ByteBuffer> nodeIdAndDigestMap) {
		List<Integer> children;
		List<ByteBuffer> segHashes = new ArrayList<ByteBuffer>(noOfChildren);
		ByteBuffer segHashBB;
		SegmentHash segHash;

		for (int parentId : parentIds) {
			children = getImmediateChildren(parentId, noOfChildren);

			for (int child : children) {
				if (nodeIdAndDigestMap.containsKey(child))
					segHashBB = nodeIdAndDigestMap.get(child);
				else {
					segHash = hTStorage.getSegmentHash(treeId, child);
					segHashBB = (segHash == null) ? null : segHash.hash;
				}
				if (segHashBB != null)
					segHashes.add(segHashBB);
			}
			ByteBuffer digest = digestByteBuffers(segHashes);
			nodeIdAndDigestMap.put(parentId, digest);
			segHashes.clear();
		}
	}

	/**
	 * Segment block id starts with 0. Each leaf node corresponds to a segment
	 * block. This function does the mapping from leaf node id to segment block
	 * id.
	 * 
	 * @param segId
	 * @return
	 */
	private int getLeafIdFromSegmentId(int segId) {
		return internalNodesCount + segId;
	}

	/**
	 * 
	 * @param leafNodeId
	 * @return
	 */
	private int getSegmentIdFromLeafId(int leafNodeId) {
		return leafNodeId - internalNodesCount;
	}

	private List<Integer> getSegmentIdsFromLeafIds(
			final Collection<Integer> leafNodeIds) {
		List<Integer> result = new ArrayList<Integer>(leafNodeIds.size());
		for (Integer leafNodeId : leafNodeIds)
			result.add(getSegmentIdFromLeafId(leafNodeId));
		return result;
	}

	/**
	 * Given a node id, finds all the leaves that can be reached from this node.
	 * If the nodeId is a leaf node, then that will be returned as the result.
	 * 
	 * @param nodeId
	 * @return, all ids of leaf nodes.
	 */
	private Collection<Integer> getAllLeafNodeIds(int nodeId) {
		Queue<Integer> pQueue = new ArrayDeque<Integer>();
		pQueue.add(nodeId);
		while (pQueue.peek() < internalNodesCount) {
			int cNodeId = pQueue.remove();
			pQueue.addAll(getImmediateChildren(cNodeId, noOfChildren));
		}
		return pQueue;
	}

	private Collection<Integer> getAllLeafNodeIds(Collection<Integer> nodeIds) {
		Collection<Integer> result = new ArrayList<Integer>();
		for (int nodeId : nodeIds) {
			result.addAll(getAllLeafNodeIds(nodeId));
		}
		return result;
	}

	/**
	 * 
	 * @param nodeId
	 *            , id of the internal node in the tree.
	 * @return
	 */
	private boolean isLeafNode(int nodeId) {
		return nodeId >= internalNodesCount;
	}

	private static int roundUpToPowerOf2(int number) {
		return (number >= MAX_NO_OF_BUCKETS) ? MAX_NO_OF_BUCKETS
				: ((number > 1) ? Integer.highestOneBit((number - 1) << 1) : 1);
	}

	@Override
	public long getLastFullyRebuiltTimeStamp(long treeId) {
		return hTStorage.getLastFullyTreeReBuiltTimestamp(treeId);
	}

	@Override
	public boolean enableNonblockingOperations() {
		boolean result;
		synchronized (nonBlockingCallsLock) {
			result = enabledNonBlockingCalls;
			if (enabledNonBlockingCalls) {
				LOGGER.info("Non blocking calls are already enabled.");
			} else {
				if (bgDataUpdater == null)
					bgDataUpdater = new NonBlockingHashTreeDataUpdater(this);
				new Thread(bgDataUpdater).start();
				enabledNonBlockingCalls = true;
				LOGGER.info("Non blocking calls are enabled.");
			}
		}
		return result;
	}

	@Override
	public boolean disableNonblockingOperations() {
		boolean result;
		synchronized (nonBlockingCallsLock) {
			result = enabledNonBlockingCalls;
			if (!enabledNonBlockingCalls) {
				LOGGER.info("Non blocking calls are already disabled.");
			} else {
				enabledNonBlockingCalls = false;
				CountDownLatch countDownLatch = new CountDownLatch(1);
				bgDataUpdater.stop(countDownLatch);
				try {
					countDownLatch.await();
				} catch (InterruptedException e) {
					LOGGER.warn(
							"Exception occurred while waiting data updater to stop",
							e);
				}
				LOGGER.info("Non blocking calls are disabled.");
			}
		}
		return result;
	}

	@Override
	public void stop() {
		disableNonblockingOperations();
	}

	@Override
	public boolean isNonBlockingCallsEnabled() {
		boolean result;
		synchronized (nonBlockingCallsLock) {
			result = enabledNonBlockingCalls;
		}
		return result;
	}
}
