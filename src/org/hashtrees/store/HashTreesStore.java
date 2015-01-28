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
package org.hashtrees.store;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hashtrees.HashTrees;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;
import org.hashtrees.util.Service;

/**
 * Defines store interface for storing tree and segments and is used by
 * {@link HashTrees}.
 * 
 * {@link HashTreesMemStore} provides in memory store implementation.
 * {@link HashTreesPersistentStore} provides persistent store implementation.
 * 
 */
public interface HashTreesStore extends Service {

	/**
	 * A segment data is the value inside a segment block.
	 * 
	 * @param treeId
	 * @param segId
	 * @param key
	 * @param digest
	 */
	void putSegmentData(long treeId, int segId, ByteBuffer key,
			ByteBuffer digest) throws IOException;

	/**
	 * Returns the SegmentData for the given key if available, otherwise returns
	 * null.
	 * 
	 * @param treeId
	 * @param segId
	 * @param key
	 * @return
	 */
	SegmentData getSegmentData(long treeId, int segId, ByteBuffer key)
			throws IOException;

	/**
	 * Deletes the given segement data from the block.
	 * 
	 * @param treeId
	 * @param segId
	 * @param key
	 */
	void deleteSegmentData(long treeId, int segId, ByteBuffer key)
			throws IOException;

	/**
	 * Returns an iterator to read all the segment data of the given tree id.
	 * 
	 * @param treeId
	 * @return
	 */
	Iterator<SegmentData> getSegmentDataIterator(long treeId)
			throws IOException;

	/**
	 * Returns an iterator to read all the segment data of the given tree id,
	 * and starting from a given segId and to segId.
	 * 
	 * @param treeId
	 * @param fromSegId
	 *            , inclusive
	 * @param toSegId
	 *            , inclusive
	 * @return
	 * @throws IOException
	 */
	Iterator<SegmentData> getSegmentDataIterator(long treeId, int fromSegId,
			int toSegId) throws IOException;

	/**
	 * Given a segment id, returns the list of all segment data in the
	 * individual segment block.
	 * 
	 * @param treeId
	 * @param segId
	 * @return
	 */
	List<SegmentData> getSegment(long treeId, int segId) throws IOException;

	/**
	 * Segment hash is the hash of all data inside a segment block. A segment
	 * hash is stored on a tree node.
	 * 
	 * @param treeId
	 * @param nodeId
	 *            , identifier of the node in the hash tree.
	 * @param digest
	 */
	void putSegmentHash(long treeId, int nodeId, ByteBuffer digest)
			throws IOException;

	/**
	 * 
	 * @param treeId
	 * @param nodeId
	 * @return
	 */
	SegmentHash getSegmentHash(long treeId, int nodeId) throws IOException;

	/**
	 * Returns the data inside the nodes of the hash tree. If the node id is not
	 * present in the hash tree, the entry will be missing in the result.
	 * 
	 * @param treeId
	 * @param nodeIds
	 *            , internal tree node ids.
	 * @return
	 */
	List<SegmentHash> getSegmentHashes(long treeId, Collection<Integer> nodeIds)
			throws IOException;

	/**
	 * Marks a segment as a dirty.
	 * 
	 * @param treeId
	 * @param segId
	 * @return previousValue
	 */
	boolean setDirtySegment(long treeId, int segId) throws IOException;

	/**
	 * Clears the segments, which are passed as an argument.
	 * 
	 * @param treeId
	 * @param segId
	 * @return previousValue
	 */
	boolean clearDirtySegment(long treeId, int segId) throws IOException;

	/**
	 * Gets the dirty segments without clearing those bits.
	 * 
	 * @param treeId
	 * @return
	 */
	List<Integer> getDirtySegments(long treeId) throws IOException;

	/**
	 * Sets flags for the given segments. Used during rebuild process by
	 * {@link HashTrees#rebuildHashTree(long, boolean)}. If the process crashes
	 * in the middle of rebuilding we don't want to loose track. This has to
	 * persist that information, so that we can reuse it after the process
	 * recovery.
	 * 
	 * @param segIds
	 */
	void markSegments(long treeId, List<Integer> segIds) throws IOException;

	/**
	 * Gets the marked segments for the given treeId.
	 * 
	 * @param treeId
	 * @return
	 */
	List<Integer> getMarkedSegments(long treeId) throws IOException;

	/**
	 * Unsets flags for the given segments. Used during rebuild process by
	 * {@link HashTrees#rebuildHashTree(long, boolean)}. After rebuilding is
	 * done, this will be cleared.
	 * 
	 * @param segIds
	 */
	void unmarkSegments(long treeId, List<Integer> segIds) throws IOException;

	/**
	 * Deletes the segment hashes, and segment data for the given treeId.
	 * 
	 * @param treeId
	 */
	void deleteTree(long treeId) throws IOException;

	/**
	 * Stores the timestamp at which the complete HashTree was rebuilt. This
	 * method updates the value in store only if the given value is higher than
	 * the existing timestamp, otherwise a noop.
	 * 
	 * @param timestamp
	 */
	void setCompleteRebuiltTimestamp(long treeId, long timestamp)
			throws IOException;

	/**
	 * Returns the timestamp at which the complete HashTree was rebuilt.
	 * 
	 * @return
	 */
	long getCompleteRebuiltTimestamp(long treeId) throws IOException;
}
