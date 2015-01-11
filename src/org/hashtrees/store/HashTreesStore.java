package org.hashtrees.store;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hashtrees.HashTrees;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;

/**
 * Defines store interface for storing tree and segments and is used by
 * {@link HashTrees}.
 * 
 * {@link HashTreesMemStore} provides in memory store implementation.
 * {@link HashTreesPersistentStore} provides persistent store implementation.
 * 
 */
public interface HashTreesStore {

	/**
	 * A segment data is the value inside a segment block.
	 * 
	 * @param treeId
	 * @param segId
	 * @param key
	 * @param digest
	 */
	void putSegmentData(long treeId, int segId, ByteBuffer key,
			ByteBuffer digest);

	/**
	 * Deletes the given segement data from the block.
	 * 
	 * @param treeId
	 * @param segId
	 * @param key
	 */
	void deleteSegmentData(long treeId, int segId, ByteBuffer key);

	/**
	 * Returns the SegmentData for the given key if available, otherwise returns
	 * null.
	 * 
	 * @param treeId
	 * @param segId
	 * @param key
	 * @return
	 */
	SegmentData getSegmentData(long treeId, int segId, ByteBuffer key);

	/**
	 * Given a segment id, returns the list of all segment data in the
	 * individual segment block.
	 * 
	 * @param treeId
	 * @param segId
	 * @return
	 */
	List<SegmentData> getSegment(long treeId, int segId);

	/**
	 * Segment hash is the hash of all data inside a segment block. A segment
	 * hash is stored on a tree node.
	 * 
	 * @param treeId
	 * @param nodeId
	 *            , identifier of the node in the hash tree.
	 * @param digest
	 */
	void putSegmentHash(long treeId, int nodeId, ByteBuffer digest);

	/**
	 * 
	 * @param treeId
	 * @param nodeId
	 * @return
	 */
	SegmentHash getSegmentHash(long treeId, int nodeId);

	/**
	 * Returns the data inside the nodes of the hash tree. If the node id is not
	 * present in the hash tree, the entry will be missing in the result.
	 * 
	 * @param treeId
	 * @param nodeIds
	 *            , internal tree node ids.
	 * @return
	 */
	List<SegmentHash> getSegmentHashes(long treeId, Collection<Integer> nodeIds);

	/**
	 * Marks a segment as a dirty.
	 * 
	 * @param treeId
	 * @param segId
	 */
	void setDirtySegment(long treeId, int segId);

	/**
	 * Returns all the ids of the dirty segments. Dirty markers are reset while
	 * returning the result. This operation is atomic.
	 * 
	 * @param treeId
	 * @return
	 */
	List<Integer> clearAndGetDirtySegments(long treeId);

	/**
	 * Deletes the segment hashes, and segment data for the given treeId.
	 * 
	 * @param treeId
	 */
	void deleteTree(long treeId);

	/**
	 * Stores the timestamp at which the complete HashTree was rebuilt. This
	 * method updates the value in store only if the given value is higher than
	 * the existing timestamp, otherwise a noop.
	 * 
	 * @param timestamp
	 */
	void setLastFullyTreeBuiltTimestamp(long treeId, long timestamp);

	/**
	 * Returns the timestamp at which the complete HashTree was rebuilt.
	 * 
	 * @return
	 */
	long getLastFullyTreeBuiltTimestamp(long treeId);

	/**
	 * Returns all tree ids that are stored.
	 * 
	 * @return
	 */
	Iterator<Long> getAllTreeIds();
}
