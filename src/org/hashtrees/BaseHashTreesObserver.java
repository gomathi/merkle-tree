package org.hashtrees;

import java.nio.ByteBuffer;
import java.util.List;

import org.hashtrees.thrift.generated.KeyValue;

/**
 * Implements {@link HashTreesObserver}, and all the operations are noop.
 * Extensions of these classes can just override only methods they are
 * interested in.
 * 
 */
public class BaseHashTreesObserver implements HashTreesObserver {

	@Override
	public void preSPut(List<KeyValue> keyValuePairs) {

	}

	@Override
	public void postSPut(List<KeyValue> keyValuePairs) {

	}

	@Override
	public void preSRemove(List<ByteBuffer> keys) {

	}

	@Override
	public void postSRemove(List<ByteBuffer> keys) {

	}

	@Override
	public void preHPut(ByteBuffer key, ByteBuffer value) {

	}

	@Override
	public void postHPut(ByteBuffer key, ByteBuffer value) {

	}

	@Override
	public void preHRemove(ByteBuffer key) {

	}

	@Override
	public void postHRemove(ByteBuffer key) {

	}

	@Override
	public void preRebuild(long treeId, boolean isFullRebuild) {

	}

	@Override
	public void postRebuild(long treeId, boolean isFullRebuild) {

	}

}
