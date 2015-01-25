package org.hashtrees;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.hashtrees.thrift.generated.KeyValue;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;

/**
 * Defines Hash tree methods. Hash tree provides a way for nodes to synch up
 * quickly by exchanging very little information.
 * 
 * This hosts multiple hash trees. Each hash tree is differentiated by a tree
 * id.
 * 
 */
public interface HashTrees {

	/**
	 * Adds the (key,value) pair to the store. Intended to be used while synch
	 * operation.
	 * 
	 * @param keyValuePairs
	 */
	void sPut(List<KeyValue> keyValuePairs) throws IOException;

	/**
	 * Deletes the keys from the store. Intended to be used while synch
	 * operation.
	 * 
	 * @param keys
	 * 
	 */
	void sRemove(List<ByteBuffer> keys) throws IOException;

	/**
	 * Hash tree internal nodes store the hash of their children nodes. Given a
	 * set of internal node ids, this returns the hashes that are stored on the
	 * internal node.
	 * 
	 * @param treeId
	 * @param nodeIds
	 *            , internal tree node ids.
	 * @return
	 * 
	 */
	List<SegmentHash> getSegmentHashes(long treeId, List<Integer> nodeIds)
			throws IOException;

	/**
	 * Returns the segment hash that is stored on the tree.
	 * 
	 * @param treeId
	 *            , hash tree id.
	 * @param nodeId
	 *            , node id
	 * @return
	 * 
	 */
	SegmentHash getSegmentHash(long treeId, int nodeId) throws IOException;

	/**
	 * Hash tree data is stored on the leaf blocks. Given a segment id this
	 * method is supposed to return (key,hash) pairs.
	 * 
	 * @param treeId
	 * @param segId
	 *            , id of the segment block.
	 * @return
	 * 
	 */
	List<SegmentData> getSegment(long treeId, int segId) throws IOException;

	/**
	 * Returns the (key,digest) for the given key in the given segment.
	 * 
	 * 
	 * @param treeId
	 * @param segId
	 * @param key
	 */
	SegmentData getSegmentData(long treeId, int segId, ByteBuffer key)
			throws IOException;

	/**
	 * Deletes the given tree node.
	 * 
	 * @param treeId
	 * @param nodeId
	 * @throws IOException
	 */
	void deleteTreeNode(long treeId, int nodeId) throws IOException;

	/**
	 * Adds the key, and digest of value to the segment block in HashTree.
	 * 
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	void hPut(ByteBuffer key, ByteBuffer value) throws IOException;

	/**
	 * Deletes the key from the hash tree.
	 * 
	 * @param key
	 * @throws IOException
	 */
	void hRemove(ByteBuffer key) throws IOException;

	/**
	 * Updates the other HTree based on the differences with local objects. This
	 * function should be running on primary to synch with other replicas, and
	 * not the other way.
	 * 
	 * @param remoteTree
	 * @param syncType
	 * @return, gives stats about total number of differences between local node
	 *          and remote node.
	 */
	SyncDiffResult synch(long treeId, HashTrees remoteTree, SyncType syncType)
			throws IOException;

	/**
	 * Same as {@link #synch(long, HashTrees, SyncType)} except that, syncType
	 * is passed as SyncType.UPDATE.
	 * 
	 * @param treeId
	 * @param remoteTree
	 * @return
	 */
	SyncDiffResult synch(long treeId, HashTrees remoteTree) throws IOException;

	/**
	 * Updates segment hashes based on the dirty entries.
	 * 
	 * @param treeId
	 * @param fullRebuildPeriod
	 *            , indicates a time interval in milliseconds, and if a tree is
	 *            not fully rebuilt for more than this value, a full rebuild
	 *            will be triggered. Otherwise only dirty segments will be
	 *            updated. If there is no information about when was the last
	 *            time the tree was fully rebuilt, then a full rebuild will be
	 *            triggered. A negative value indicates disabling full rebuild.
	 */
	void rebuildHashTree(long treeId, long fullRebuildPeriod)
			throws IOException;

	/**
	 * Similar to {@link #rebuildHashTree(long, long)}, except that allows to
	 * specify whether a full rebuild should be triggered or not.
	 * 
	 * @param treeId
	 * @param fullRebuild
	 * @throws IOException
	 */
	void rebuildHashTree(long treeId, boolean fullRebuild) throws IOException;

	void addObserver(HashTreesObserver observer);

	void removeObserver(HashTreesObserver observer);
}
