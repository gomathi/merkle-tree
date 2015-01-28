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
 */package org.hashtrees.util;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Pair<F, S> {

	private final F f;
	private final S s;

	public Pair(F f, S s) {
		this.f = f;
		this.s = s;
	}

	public static <F, S> Pair<F, S> create(F f, S s) {
		return new Pair<F, S>(f, s);
	}

	public F getFirst() {
		return f;
	}

	public S getSecond() {
		return s;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null || !(that instanceof Pair<?, ?>))
			return false;
		Pair<?, ?> thatObj = (Pair<?, ?>) that;
		return Objects.equal(f, thatObj.f) && Objects.equal(s, thatObj.s);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(f, s);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("first", f)
				.add("second", s).toString();
	}
}
