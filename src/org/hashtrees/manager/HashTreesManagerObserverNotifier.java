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
 */package org.hashtrees.manager;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.hashtrees.SyncDiffResult;
import org.hashtrees.thrift.generated.ServerName;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

/**
 * A helper class that is used by {@link HashTreesManager} to notify its
 * observers about certain events.
 * 
 */
public class HashTreesManagerObserverNotifier implements
		HashTreesManagerObserver {

	private final ConcurrentLinkedQueue<HashTreesManagerObserver> observers = new ConcurrentLinkedQueue<>();

	@Override
	public void preSync(final long treeId, final ServerName remoteServerName) {
		notifyObservers(new Function<HashTreesManagerObserver, Void>() {

			@Override
			public Void apply(HashTreesManagerObserver input) {
				input.preSync(treeId, remoteServerName);
				return null;
			}
		});
	}

	public void addObserver(HashTreesManagerObserver observer) {
		assert (observer != null);
		observers.add(observer);
	}

	public void removeObserver(HashTreesManagerObserver observer) {
		assert (observer != null);
		observers.remove(observer);
	}

	@Override
	public void postSync(final long treeId, final ServerName remoteServerName,
			final SyncDiffResult result, final boolean synced) {
		notifyObservers(new Function<HashTreesManagerObserver, Void>() {

			@Override
			public Void apply(HashTreesManagerObserver input) {
				input.postSync(treeId, remoteServerName, result, synced);
				return null;
			}
		});
	}

	private void notifyObservers(
			Function<HashTreesManagerObserver, Void> function) {
		Iterator<Void> itr = Iterators
				.transform(observers.iterator(), function);
		while (itr.hasNext())
			itr.next();
	}

}
