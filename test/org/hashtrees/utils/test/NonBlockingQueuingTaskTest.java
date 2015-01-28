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
