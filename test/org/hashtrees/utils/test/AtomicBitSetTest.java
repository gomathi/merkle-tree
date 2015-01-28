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

import org.hashtrees.util.AtomicBitSet;
import org.junit.Assert;
import org.junit.Test;

public class AtomicBitSetTest {

	@Test
	public void testGetBit() {
		AtomicBitSet obj = new AtomicBitSet();
		List<Integer> setBits = obj.clearAndGetAllSetBits();
		Assert.assertEquals(0, setBits.size());
	}

	@Test
	public void testSetBit() {
		AtomicBitSet obj = new AtomicBitSet();
		obj.set(0);
		Assert.assertTrue(obj.get(0));
		obj.clear(0);
		Assert.assertFalse(obj.get(0));

		obj = new AtomicBitSet();
		obj.set(10000000);
		Assert.assertTrue(obj.get(10000000));
		obj.clear(10000000);
		Assert.assertFalse(obj.get(10000000));
	}

	@Test
	public void testSetBits() {
		AtomicBitSet obj = new AtomicBitSet();
		obj.set(1025);
		obj.set(1026);
		List<Integer> setBits = obj.clearAndGetAllSetBits();
		Assert.assertEquals(2, setBits.size());
		Assert.assertTrue(setBits.contains(1025));
		Assert.assertTrue(setBits.contains(1026));
	}

	@Test
	public void testClearBit() {
		AtomicBitSet obj = new AtomicBitSet();
		obj.set(1025);
		obj.set(1026);

		obj.clear(1025);
		Assert.assertFalse(obj.get(1025));
		Assert.assertTrue(obj.get(1026));

		List<Integer> setBits = obj.clearAndGetAllSetBits();
		Assert.assertEquals(1, setBits.size());
		Assert.assertTrue(setBits.contains(1026));
	}

	@Test
	public void testClearBits() {
		AtomicBitSet obj = new AtomicBitSet();
		obj.set(1025);
		obj.set(1026);

		obj.clear();
		List<Integer> setBits = obj.clearAndGetAllSetBits();
		Assert.assertEquals(0, setBits.size());
	}

	@Test
	public void testClearAndGetAllSetBits() {
		AtomicBitSet obj = new AtomicBitSet();
		for (int i = 0; i < 10000; i++)
			obj.set(i);

		List<Integer> setBits = obj.clearAndGetAllSetBits();
		Assert.assertEquals(10000, setBits.size());
		for (int i = 0; i < 1000; i++)
			Assert.assertEquals(i, setBits.get(i).intValue());
	}

	@Test
	public void testClearBitsWithParameters() {
		AtomicBitSet obj = new AtomicBitSet();
		for (int i = 0; i < 10000; i++)
			obj.set(i);

		List<Integer> expected = new ArrayList<>();
		for (int i = 100; i < 200; i++)
			expected.add(i);
		for (int i = 5000; i < 6000; i++)
			expected.add(i);

		obj.clearBits(expected);
		List<Integer> setBits = obj.getAllSetBits();
		Assert.assertEquals(10000 - (100 + 1000), setBits.size());
	}

	@Test
	public void testGetSetBitsWithDiffArgs() {
		AtomicBitSet obj = new AtomicBitSet();
		obj.set(1025);
		obj.set(1026);

		obj.clear();
		List<Integer> setBits = obj.getAllSetBits();
		Assert.assertEquals(0, setBits.size());
	}

	@Test
	public void testGetAllSetBits() {
		AtomicBitSet obj = new AtomicBitSet();
		for (int i = 0; i < 10000; i++)
			obj.set(i);

		List<Integer> setBits = obj.getAllSetBits();
		Assert.assertEquals(10000, setBits.size());
		for (int i = 0; i < 1000; i++)
			Assert.assertEquals(i, setBits.get(i).intValue());
	}
}
