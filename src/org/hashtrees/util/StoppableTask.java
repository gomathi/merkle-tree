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
package org.hashtrees.util;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.concurrent.ThreadSafe;

/**
 * A stoppable abstract class which can be scheduled through executors. This
 * abstract class makes sure only one task can run at any time. The
 * implementations are expected to provide code for {@link #runImpl()} method.
 * Also the callers of stop method, can pass a latch to
 * {@link #stopAsync(CountDownLatch)}, and asynchronously wait for this task to
 * complete.
 * 
 */
@ThreadSafe
public abstract class StoppableTask implements Runnable, Stoppable {

	private final AtomicBoolean lock = new AtomicBoolean();
	private final ConcurrentLinkedQueue<CountDownLatch> stopListenerLatches = new ConcurrentLinkedQueue<>();
	private volatile boolean stopRequested = false;

	/**
	 * If a task is already running or stop has been requested, this will return
	 * false. Otherwise enables running status to be true.
	 * 
	 * @return
	 */
	private synchronized boolean enableRunningStatus() {
		if (stopRequested)
			return false;
		return lock.compareAndSet(false, true);
	}

	private synchronized void disableRunningStatus() {
		lock.set(false);
		if (stopRequested) {
			notifyStopListeners();
		}
	}

	private void notifyStopListeners() {
		for (CountDownLatch stopListenerLatch : stopListenerLatches)
			stopListenerLatch.countDown();
		stopListenerLatches.clear();
	}

	protected boolean hasStopRequested() {
		return stopRequested;
	}

	@Override
	public synchronized void stopAsync() {
		stopRequested = true;
		if (!lock.get())
			notifyStopListeners();
	}

	public synchronized void stopAsync(final CountDownLatch stopListenerLatch) {
		stopListenerLatches.add(stopListenerLatch);
		stopAsync();
	}

	protected abstract void runImpl();

	public void run() {
		if (enableRunningStatus()) {
			try {
				runImpl();
			} finally {
				disableRunningStatus();
			}
		}
	}
}
