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
 */package org.hashtrees.utils.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.hashtrees.util.StoppableTask;
import org.junit.Assert;
import org.junit.Test;

public class StoppableTaskTest {

	private static class StoppableTaskTestImpl extends StoppableTask {

		volatile boolean ran = false;
		final CountDownLatch taskStartedLatch = new CountDownLatch(1);

		@Override
		public void runImpl() {
			taskStartedLatch.countDown();
			ran = true;
		}

	}

	@Test
	public void testStoppableTask() throws InterruptedException {
		StoppableTaskTestImpl task = new StoppableTaskTestImpl();
		new Thread(task).start();
		Assert.assertTrue(task.taskStartedLatch.await(10000,
				TimeUnit.MILLISECONDS));
		Assert.assertTrue(task.ran);
	}

	@Test
	public void testStoppableTaskWithMultipleListeners()
			throws InterruptedException {
		final CountDownLatch taskWaitLatch = new CountDownLatch(1);
		final CountDownLatch taskStarted = new CountDownLatch(1);
		StoppableTask myTask = new StoppableTask() {

			@Override
			protected void runImpl() {
				try {
					taskStarted.countDown();
					taskWaitLatch.await();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		};

		new Thread(myTask).start();
		Assert.assertTrue(taskStarted.await(10000, TimeUnit.MILLISECONDS));

		final CountDownLatch firstListener = new CountDownLatch(1);
		final CountDownLatch secondListener = new CountDownLatch(1);
		myTask.stopAsync(firstListener);
		myTask.stopAsync(secondListener);
		taskWaitLatch.countDown();

		Assert.assertTrue(firstListener.await(10000, TimeUnit.MILLISECONDS));
		Assert.assertTrue(secondListener.await(10000, TimeUnit.MILLISECONDS));
	}

	@Test
	public void testStoppableTaskWithMultipleListenersWithTimeout()
			throws InterruptedException {
		final CountDownLatch taskWaitLatch = new CountDownLatch(1);
		final CountDownLatch taskStarted = new CountDownLatch(1);
		StoppableTask myTask = new StoppableTask() {

			@Override
			protected void runImpl() {
				try {
					taskStarted.countDown();
					taskWaitLatch.await();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		};

		new Thread(myTask).start();
		Assert.assertTrue(taskStarted.await(10000, TimeUnit.MILLISECONDS));

		final CountDownLatch firstListener = new CountDownLatch(1);
		final CountDownLatch secondListener = new CountDownLatch(1);
		myTask.stopAsync(firstListener);
		myTask.stopAsync(secondListener);

		Assert.assertFalse(firstListener.await(100, TimeUnit.MILLISECONDS));
		Assert.assertFalse(secondListener.await(100, TimeUnit.MILLISECONDS));
	}

	@Test
	public void testStoppableTaskWithCallbackOnStop()
			throws InterruptedException {
		final CountDownLatch latch = new CountDownLatch(1);
		final CountDownLatch listenerAddedLatch = new CountDownLatch(1);
		final CountDownLatch listenerLatch = new CountDownLatch(1);
		final AtomicBoolean stopped = new AtomicBoolean();
		final StoppableTask task = new StoppableTask() {

			@Override
			protected void runImpl() {
				try {
					latch.await();
				} catch (InterruptedException e) {
					Assert.fail(e.getMessage());
				}
			}
		};
		new Thread(task).start();
		Runnable stopListenerTask = new Runnable() {

			@Override
			public void run() {
				task.stopAsync(listenerLatch);
				listenerAddedLatch.countDown();
				try {
					listenerLatch.await();
				} catch (InterruptedException e) {
					Assert.fail(e.getMessage());
				}
				stopped.set(true);
			}
		};
		new Thread(stopListenerTask).start();
		listenerAddedLatch.await();
		latch.countDown();
		listenerLatch.await(10000, TimeUnit.MILLISECONDS);
		Assert.assertTrue(stopped.get());
	}
}
