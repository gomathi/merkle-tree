package org.hashtrees;

import java.nio.ByteBuffer;

/**
 * This interfaces provides a way to hook into listen to the operations on
 * {@link HashTrees}
 * 
 */
public interface HashTreesObserver {

	void postHPut(ByteBuffer key, ByteBuffer value);

	void postHRemove(ByteBuffer key);
}
