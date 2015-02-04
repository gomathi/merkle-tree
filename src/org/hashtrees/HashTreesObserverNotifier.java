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

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.hashtrees.thrift.generated.KeyValue;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

/**
 * A helper class that is used by {@link HashTreesImpl} to notify the observers
 * about certain events.
 * 
 */
class HashTreesObserverNotifier implements HashTreesObserver {

	private final ConcurrentLinkedQueue<HashTreesObserver> observers;

	public HashTreesObserverNotifier() {
		observers = new ConcurrentLinkedQueue<>();
	}

	public void addObserver(HashTreesObserver observer) {
		assert (observer != null);
		observers.add(observer);
	}

	public void removeObserver(HashTreesObserver observer) {
		assert (observer != null);
		observers.remove(observer);
	}

	@Override
	public void preSPut(final List<KeyValue> keyValuePairs) {
		notifyObservers(new Function<HashTreesObserver, Void>() {

			@Override
			public Void apply(HashTreesObserver input) {
				input.preSPut(keyValuePairs);
				return null;
			}
		});
	}

	@Override
	public void postSPut(final List<KeyValue> keyValuePairs) {
		notifyObservers(new Function<HashTreesObserver, Void>() {

			@Override
			public Void apply(HashTreesObserver input) {
				input.postSPut(keyValuePairs);
				return null;
			}
		});
	}

	@Override
	public void preSRemove(final List<ByteBuffer> keys) {
		notifyObservers(new Function<HashTreesObserver, Void>() {

			@Override
			public Void apply(HashTreesObserver input) {
				input.preSRemove(keys);
				return null;
			}
		});
	}

	@Override
	public void postSRemove(final List<ByteBuffer> keys) {
		notifyObservers(new Function<HashTreesObserver, Void>() {

			@Override
			public Void apply(HashTreesObserver input) {
				input.postSRemove(keys);
				return null;
			}
		});
	}

	@Override
	public void preHPut(final ByteBuffer key, final ByteBuffer value) {
		notifyObservers(new Function<HashTreesObserver, Void>() {

			@Override
			public Void apply(HashTreesObserver input) {
				input.preHPut(key, value);
				return null;
			}
		});
	}

	@Override
	public void postHPut(final ByteBuffer key, final ByteBuffer value) {
		notifyObservers(new Function<HashTreesObserver, Void>() {

			@Override
			public Void apply(HashTreesObserver input) {
				input.postHPut(key, value);
				return null;
			}
		});
	}

	@Override
	public void preHRemove(final ByteBuffer key) {
		notifyObservers(new Function<HashTreesObserver, Void>() {

			@Override
			public Void apply(HashTreesObserver input) {
				input.preHRemove(key);
				return null;
			}
		});
	}

	@Override
	public void postHRemove(final ByteBuffer key) {
		notifyObservers(new Function<HashTreesObserver, Void>() {

			@Override
			public Void apply(HashTreesObserver input) {
				input.postHRemove(key);
				return null;
			}
		});
	}

	@Override
	public void preRebuild(final long treeId, final boolean isFullRebuild) {
		notifyObservers(new Function<HashTreesObserver, Void>() {

			@Override
			public Void apply(HashTreesObserver input) {
				input.preRebuild(treeId, isFullRebuild);
				return null;
			}
		});
	}

	@Override
	public void postRebuild(final long treeId, final boolean isFullRebuild) {
		notifyObservers(new Function<HashTreesObserver, Void>() {

			@Override
			public Void apply(HashTreesObserver input) {
				input.postRebuild(treeId, isFullRebuild);
				return null;
			}
		});
	}

	private void notifyObservers(Function<HashTreesObserver, Void> function) {
		Iterator<Void> itr = Iterators
				.transform(observers.iterator(), function);
		while (itr.hasNext())
			itr.next();
	}

}
