package org.hashtrees.utils.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.hashtrees.util.NonBlockingQueuingTask;
import org.hashtrees.util.NonBlockingQueuingTask.QueuingTaskIsStoppedException;
import org.junit.Test;

public class NonBlockingQueuingTaskTest {

	@Test
	public void testEnqueOperations() throws InterruptedException {
		Integer marker = new Integer(0);
		final List<Integer> queuedElements = new ArrayList<>();
		NonBlockingQueuingTask<Integer> nbqTask = new NonBlockingQueuingTask<Integer>(
				marker, 100) {

			@Override
			protected void handleElement(Integer element) {
				queuedElements.add(element);
			}
		};

		new Thread(nbqTask).start();
		for (int i = 0; i < 10; i++)
			nbqTask.enque(i);
		CountDownLatch stopLatch = new CountDownLatch(1);
		nbqTask.stopAsync(stopLatch);
		stopLatch.await(10000, TimeUnit.MILLISECONDS);
		Assert.assertEquals(10, queuedElements.size());
		for (int i = 0; i < 10; i++)
			Assert.assertEquals(i, queuedElements.get(i).intValue());
	}

	@Test(expected = QueuingTaskIsStoppedException.class)
	public void testEnqueOperationsOnStop() throws InterruptedException {
		Integer marker = new Integer(0);
		NonBlockingQueuingTask<Integer> nbqTask = new NonBlockingQueuingTask<Integer>(
				marker, 100) {

			@Override
			protected void handleElement(Integer element) {
			}
		};

		new Thread(nbqTask).start();

		CountDownLatch stopLatch = new CountDownLatch(1);
		nbqTask.stopAsync(stopLatch);
		stopLatch.await(10000, TimeUnit.MILLISECONDS);
		nbqTask.enque(10);
	}
}
