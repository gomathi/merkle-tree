package org.hashtrees;

import java.nio.ByteBuffer;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import org.hashtrees.util.NonBlockingQueuingTask;
import org.hashtrees.util.Pair;

/**
 * A task to enable non blocking calls on all
 * {@link HashTreesImpl#hPut(ByteArray, ByteArray)} and
 * {@link HashTreesImpl#hRemove(ByteArray)} operation.
 * 
 */
@ThreadSafe
class NonBlockingHTDataUpdater extends
		NonBlockingQueuingTask<Pair<HTOperation, List<ByteBuffer>>> {

	private static final Pair<HTOperation, List<ByteBuffer>> STOP_MARKER = new Pair<HTOperation, List<ByteBuffer>>(
			HTOperation.PUT, null);
	private final HashTreesImpl hTree;

	public NonBlockingHTDataUpdater(final HashTreesImpl hTree,
			int maxElementsToQue) {
		super(STOP_MARKER, maxElementsToQue);
		this.hTree = hTree;
	}

	@Override
	public void handleElement(Pair<HTOperation, List<ByteBuffer>> pair) {
		switch (pair.getFirst()) {
		case PUT:
			hTree.hPutInternal(pair.getSecond().get(0), pair.getSecond().get(1));
			break;
		case REMOVE:
			hTree.hRemoveInternal(pair.getSecond().get(0));
			break;
		}
	}

}