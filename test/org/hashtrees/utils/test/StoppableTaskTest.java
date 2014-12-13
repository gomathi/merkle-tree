package org.hashtrees.utils.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

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
		myTask.stop(firstListener);
		myTask.stop(secondListener);
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
		myTask.stop(firstListener);
		myTask.stop(secondListener);

		Assert.assertFalse(firstListener.await(100, TimeUnit.MILLISECONDS));
		Assert.assertFalse(secondListener.await(100, TimeUnit.MILLISECONDS));
	}
}
