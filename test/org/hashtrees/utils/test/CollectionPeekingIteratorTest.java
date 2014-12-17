package org.hashtrees.utils.test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.Assert;

import org.hashtrees.util.CollectionPeekingIterator;
import org.junit.Test;

public class CollectionPeekingIteratorTest {

	@Test(expected = NoSuchElementException.class)
	public void testRemoveWithNoSuchElementExists() {
		List<Integer> input = new ArrayList<>();
		CollectionPeekingIterator<Integer> cpItr = new CollectionPeekingIterator<>(
				input);
		cpItr.next();
	}

	@Test(expected = NoSuchElementException.class)
	public void testPeekWithNoSuchElementExists() {
		List<Integer> input = new ArrayList<>();
		CollectionPeekingIterator<Integer> cpItr = new CollectionPeekingIterator<>(
				input);
		cpItr.peek();
	}

	@Test
	public void testPeek() {
		List<Integer> input = new ArrayList<>();
		for (int i = 0; i < 10; i++)
			input.add(i);
		CollectionPeekingIterator<Integer> cpItr = new CollectionPeekingIterator<>(
				input);
		for (int i = 0; i < 10; i++) {
			Assert.assertEquals(i, cpItr.peek().intValue());
			cpItr.next();
		}
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testRemove() {
		List<Integer> input = new ArrayList<>();
		for (int i = 0; i < 10; i++)
			input.add(i);
		CollectionPeekingIterator<Integer> cpItr = new CollectionPeekingIterator<>(
				input);
		cpItr.remove();
	}
}
