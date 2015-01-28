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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Uses {@link CompletionService} and {@link ExecutorService} to schedule tasks.
 * Initially schedules a fixed no of tasks, and then it schedules a new task
 * when the result of a previous task is removed from the task queue.
 * 
 * If a stop has been requested, new tasks will not submitted. Even after the
 * stop request, the previously submitted tasks will continue to run. A call to
 * next() operation will block.
 * 
 */
@NotThreadSafe
public class TaskQueue<T> implements Iterator<Future<T>>, Stoppable {

	private final CompletionService<T> completionService;
	private final Iterator<Callable<T>> tasksItr;
	private volatile int noOfTasksSubmitted;
	private volatile boolean stopRequested;
	private volatile int sucCount, failCount;

	public TaskQueue(final ExecutorService fixedExecutors,
			final Iterator<Callable<T>> tasksItr, int initTasksToExecute) {
		this.completionService = new ExecutorCompletionService<T>(
				fixedExecutors);
		this.tasksItr = tasksItr;
		while (initTasksToExecute > 0 && tasksItr.hasNext()) {
			completionService.submit(tasksItr.next());
			initTasksToExecute--;
			noOfTasksSubmitted++;
		}
	}

	@Override
	public boolean hasNext() {
		if (noOfTasksSubmitted > 0)
			return true;
		return false;
	}

	@Override
	public Future<T> next() {
		if (!hasNext())
			throw new NoSuchElementException();
		try {
			noOfTasksSubmitted--;
			Future<T> result = completionService.take();
			try {
				result.get();
				sucCount++;
			} catch (ExecutionException e) {
				failCount++;
			}
			if (tasksItr.hasNext() && !stopRequested) {
				completionService.submit(tasksItr.next());
				noOfTasksSubmitted++;
			}
			return result;
		} catch (InterruptedException e) {
			throw new RuntimeException(
					"Exception occurred while waiting to remove the element from the queue.");
		}
	}

	public int getPasseTasksCount() {
		return sucCount;
	}

	public int getFailedTasksCount() {
		return failCount;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void stopAsync() {
		stopRequested = true;
	}

}
