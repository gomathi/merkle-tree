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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Maintains set of reentrant locks for each treeId. Requires the caller to call
 * {@link #acquireLock(long)} before using {@link #releaseLock(long)}.
 * 
 */
public class HTReentrantLockProvider implements LockProvider {

	private final ConcurrentHashMap<Long, ReentrantLock> internalLocks = new ConcurrentHashMap<>();

	private ReentrantLock getLock(long treeId) {
		if (!internalLocks.contains(treeId)) {
			internalLocks.putIfAbsent(treeId, new ReentrantLock());
		}
		return internalLocks.get(treeId);
	}

	@Override
	public boolean acquireLock(long treeId) {
		ReentrantLock lock = getLock(treeId);
		return lock.tryLock();
	}

	@Override
	public void releaseLock(long treeId) {
		ReentrantLock lock = getLock(treeId);
		lock.unlock();
	}

}
