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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * An abstract class which queues elements, and which can be dequeued while
 * running as a background thread. Multiple producers can add elements using
 * {@link #enque(Object)}. A single thread will remove the elements sequentially
 * and will perform the {@link #handleElement(Object)} task.
 * 
 * Mainly used as multiple producer and single consumer queue.
 * 
 * @param <T>
 */
public abstract class NonBlockingQueuingTask<T> extends StoppableTask {

	// A special marker to note down a stop operation has been requested on this
	// thread.
	private final T stopMarker;
	private final BlockingQueue<T> que;

	/**
	 * @param stopMarker
	 *            specifies a special data which is used for indicating stop
	 *            operation. Subclasses should not use this marker for any
	 *            operation.
	 * @param queueSize
	 *            how much data can the queue can hold. Should be higher enough
	 *            based on the number of producers, otherwise exception will be
	 *            thrown on adding the elements to the queue.
	 */
	public NonBlockingQueuingTask(T stopMarker, int queueSize) {
		this.stopMarker = stopMarker;
		que = new LinkedBlockingQueue<T>(queueSize);
	}

	public void enque(T element) {
		if (hasStopRequested() && element != stopMarker) {
			throw new QueuingTaskIsStoppedException();
		}
		boolean status = que.offer(element);
		if (!status)
			throw new QueueReachedMaxCapacityException();
	}

	@Override
	public synchronized void stopAsync() {
		super.stopAsync();
		enque(stopMarker);
	}

	@Override
	public synchronized void stopAsync(final CountDownLatch shutDownLatch) {
		super.stopAsync(shutDownLatch);
		enque(stopMarker);
	}

	@Override
	public void runImpl() {
		for (;;) {
			try {
				T data = que.take();
				if (data == stopMarker)
					continue;
				handleElement(data);
			} catch (InterruptedException e) {
				throw new RuntimeException(
						"Exception occurred while removing the element from the queue",
						e);
			} finally {
				if (hasStopRequested() && que.isEmpty()) {
					return;
				}
			}
		}
	}

	protected abstract void handleElement(T element);

	public static class QueueReachedMaxCapacityException extends
			RuntimeException {

		private static final long serialVersionUID = 1L;
		private static final String EXCEPTION_MSG = "Queue is full. Can not add more elements to the queue.";

		public QueueReachedMaxCapacityException() {
			super(EXCEPTION_MSG);
		}

		public QueueReachedMaxCapacityException(Throwable cause) {
			super(EXCEPTION_MSG, cause);
		}
	}

	public static class QueuingTaskIsStoppedException extends RuntimeException {

		private static final long serialVersionUID = 1L;
		private static final String EXCEPTION_MSG = "Queuing task is stopped. Can not add elements to the queue.";

		public QueuingTaskIsStoppedException() {
			super(EXCEPTION_MSG);
		}

		public QueuingTaskIsStoppedException(Throwable cause) {
			super(EXCEPTION_MSG, cause);
		}
	}
}
