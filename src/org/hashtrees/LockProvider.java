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
package org.hashtrees;

/**
 * If two {@link HashTrees#rebuildHashTree(long, boolean)} of a same hashtree
 * are happening at the same time, then there can be race conditions, the result
 * will be unexpected. To avoid {@link HashTreesImpl} uses this instance to
 * maintain the locks.
 * 
 * {@link HashTreesImpl#rebuildHashTree(long, boolean)},
 * {@link HashTreesImpl#synch(long, HashTrees)}
 * {@link HashTreesImpl#synch(long, HashTrees, SyncType)} are using this
 * instance to perform the operations.
 * 
 */
public interface LockProvider {

	/**
	 * Tries to acquire the lock, and returns true/false to indicate
	 * success/failure.
	 * 
	 * @param treeId
	 * @return
	 */
	boolean acquireLock(long treeId);

	/**
	 * Releases the previously acquired lock.
	 * 
	 * @param treeId
	 */
	void releaseLock(long treeId);
}
