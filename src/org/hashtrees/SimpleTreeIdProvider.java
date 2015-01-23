package org.hashtrees;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Default HashTreeIdProvider which always returns treeId as 1. This can be used
 * in places where only a single hash tree is maintained by {@link HashTrees}
 * 
 */
public class SimpleTreeIdProvider implements HashTreesIdProvider {

	public static final long TREE_ID = 1;
	private static final List<Long> TREE_IDS;

	static {
		List<Long> treeIds = new ArrayList<Long>();
		treeIds.add(TREE_ID);
		TREE_IDS = Collections.unmodifiableList(treeIds);
	}

	@Override
	public long getTreeId(byte[] key) {
		return TREE_ID;
	}

	@Override
	public Iterator<Long> getAllPrimaryTreeIds() {
		return TREE_IDS.iterator();
	}
}