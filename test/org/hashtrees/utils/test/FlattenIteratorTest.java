package org.hashtrees.utils.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.hashtrees.util.FlattenIterator;
import org.junit.Test;

public class FlattenIteratorTest {

	@Test
	public void testFlattenIteratorWithEmptyIterators() {
		List<Iterator<Integer>> emptyIteratorList = Collections.emptyList();
		Iterator<Iterator<Integer>> itrs = emptyIteratorList.iterator();
		Assert.assertFalse(new FlattenIterator<>(itrs).hasNext());
	}

	@Test
	public void testFlattenIteratorWithValidInput() {
		List<Integer> expected = new ArrayList<>();
		List<Integer> firstInput = new ArrayList<>();
		for (int i = 0; i <= 5; i++) {
			firstInput.add(i);
			expected.add(i);
		}
		List<Iterator<Integer>> inputItrs = new ArrayList<>();
		inputItrs.add(firstInput.iterator());
		List<Integer> emptyList = Collections.emptyList();
		inputItrs.add(emptyList.iterator());
		Iterator<Iterator<Integer>> itrs = inputItrs.iterator();

		FlattenIterator<Integer> fItr = new FlattenIterator<>(itrs);
		Assert.assertTrue(fItr.hasNext());
		List<Integer> actual = new ArrayList<>();
		while (fItr.hasNext())
			actual.add(fItr.next());
		Assert.assertEquals(expected, actual);
	}
}
