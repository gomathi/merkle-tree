package org.hashtrees.utils.test;

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
}
