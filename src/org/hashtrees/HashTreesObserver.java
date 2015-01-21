package org.hashtrees;

import java.nio.ByteBuffer;

/**
 * This interfaces provides a way to hook into listen the operations of
 * {@link HashTrees}
 * 
 */
public interface HashTreesObserver {

	void postPut(ByteBuffer key, ByteBuffer value);

	void postRemove(ByteBuffer key);
}
