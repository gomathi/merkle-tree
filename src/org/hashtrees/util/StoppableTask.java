package org.hashtrees.util;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.concurrent.ThreadSafe;

/**
 * A stoppable abstract class which can be scheduled through executors. This
 * abstract class makes sure only one task can run at any time. The
 * implementations are expected to provide code for {@link #runImpl()} method.
 * Also the callers of stop method, can use the latch {@link #stopListenerLatch}
 * to wait for the complete stop of this task.
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
