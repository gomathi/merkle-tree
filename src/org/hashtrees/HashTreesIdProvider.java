package org.hashtrees;

import java.nio.ByteBuffer;
import java.util.Iterator;

import org.hashtrees.manager.HashTreesManager;

/**
 * There can be multiple hash trees. Given a key we need to know which hash tree
 * that the key belongs to. This interface defines that method
 * {@link #getTreeId(ByteBuffer)} to get the corresponding information.
 * 
 * Also some nodes may act as replica nodes for some of the hash trees. In that
 * case replica nodes should not synch primary nodes of those hash trees. To
 * avoid this problem, {@link HashTreesManager} should know which hash trees are
 * managed by the local node. {@link #getAllPrimaryTreeIds()} serves this
 * purpose.
 * 
 * This interface defines methods which will be used by {@link HashTrees} class.
 * The implementation has to be thread safe.
 * 
 */
public interface HashTreesIdProvider {

	long getTreeId(byte[] key);

	/**
	 * Returns treeIds for which the current node is responsible for.
	 * 
	 * @return
	 */
	Iterator<Long> getAllPrimaryTreeIds();
}
