package org.hashtrees.utils.test;

import org.hashtrees.util.AtomicBitSet;
import org.junit.Assert;
import org.junit.Test;

public class AtomicBitSetTest {

	@Test
	public void testSetBit() {
		AtomicBitSet obj = new AtomicBitSet();
		obj.set(0);
		Assert.assertTrue(obj.get(0));
		obj.clear(0);
		Assert.assertFalse(obj.get(0));

		obj = new AtomicBitSet();
		obj.set(1023);
		Assert.assertTrue(obj.get(1023));
	}

}
