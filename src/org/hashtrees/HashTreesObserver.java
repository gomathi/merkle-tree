package org.hashtrees;

import java.nio.ByteBuffer;
import java.util.List;

import org.hashtrees.thrift.generated.KeyValue;

/**
 * This interfaces provides a way to hook into listen to the operations on
 * {@link HashTrees}
 * 
 */
public interface HashTreesObserver {

	void preSPut(List<KeyValue> keyValuePairs);

	void postSPut(List<KeyValue> keyValuePairs);

	void preSRemove(List<ByteBuffer> keys);

	void postSRemove(List<ByteBuffer> keys);

	void preHPut(ByteBuffer key, ByteBuffer value);

	void postHPut(ByteBuffer key, ByteBuffer value);

	void preHRemove(ByteBuffer key);

	void postHRemove(ByteBuffer key);

	void preRebuild(long treeId, boolean isFullRebuild);

	void postRebuild(long treeId, boolean isFullRebuild);

}
