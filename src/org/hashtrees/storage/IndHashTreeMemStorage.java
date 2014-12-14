package org.hashtrees.storage;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.ThreadSafe;

import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;

/**
 * Hash tree can host multiple similar hash trees. This is mainly used for unit
 * testing.
 * 
 */
@ThreadSafe
class IndHashTreeMemStorage {

	private final ConcurrentMap<Integer, ByteBuffer> segmentHashes = new ConcurrentSkipListMap<Integer, ByteBuffer>();
	private final ConcurrentMap<Integer, ConcurrentSkipListMap<ByteBuffer, ByteBuffer>> segDataBlocks = new ConcurrentHashMap<Integer, ConcurrentSkipListMap<ByteBuffer, ByteBuffer>>();
	private final AtomicLong fullyRebuiltTreeTs = new AtomicLong(0);
	private final AtomicLong rebuiltTreeTs = new AtomicLong(0);

	public void putSegmentHash(int nodeId, ByteBuffer digest) {
		segmentHashes.put(nodeId, digest);
	}

	public void putSegmentData(int segId, ByteBuffer key, ByteBuffer digest) {
		if (!segDataBlocks.containsKey(segId))
			segDataBlocks.putIfAbsent(segId,
					new ConcurrentSkipListMap<ByteBuffer, ByteBuffer>());
		segDataBlocks.get(segId).put(key, digest);
	}

	public SegmentData getSegmentData(int segId, ByteBuffer key) {
		ConcurrentSkipListMap<ByteBuffer, ByteBuffer> segDataBlock = segDataBlocks
				.get(segId);
		if (segDataBlock != null) {
			ByteBuffer value = segDataBlock.get(key);
			if (value != null)
				return new SegmentData(key, value);
		}
		return null;
	}

	public void deleteSegmentData(int segId, ByteBuffer key) {
		Map<ByteBuffer, ByteBuffer> segDataBlock = segDataBlocks.get(segId);
		if (segDataBlock != null)
			segDataBlock.remove(key);
	}

	public List<SegmentData> getSegment(int segId) {
		ConcurrentMap<ByteBuffer, ByteBuffer> segDataBlock = segDataBlocks
				.get(segId);
		if (segDataBlock == null)
			return Collections.emptyList();
		List<SegmentData> result = new ArrayList<SegmentData>();
		for (Map.Entry<ByteBuffer, ByteBuffer> entry : segDataBlock.entrySet()) {
			result.add(new SegmentData(entry.getKey(), entry.getValue()));
		}
		return result;
	}

	public List<SegmentHash> getSegmentHashes(Collection<Integer> nodeIds) {
		List<SegmentHash> result = new ArrayList<SegmentHash>();
		for (int nodeId : nodeIds) {
			ByteBuffer hash = segmentHashes.get(nodeId);
			if (hash != null)
				result.add(new SegmentHash(nodeId, hash));
		}
		return result;
	}

	public SegmentHash getSegmentHash(int nodeId) {
		ByteBuffer hash = segmentHashes.get(nodeId);
		if (hash == null)
			return null;
		return new SegmentHash(nodeId, hash);
	}

	private static void setValueIfNewValueIsGreater(AtomicLong val, long value) {
		long oldValue = val.get();
		while (oldValue < value) {
			if (val.compareAndSet(oldValue, value))
				break;
			oldValue = val.get();
		}
	}

	public void setLastFullyRebuiltTimestamp(long timestamp) {
		setValueIfNewValueIsGreater(fullyRebuiltTreeTs, timestamp);
	}

	public long getLastTreeFullyRebuiltTimestamp() {
		long value = fullyRebuiltTreeTs.get();
		return value;
	}

	public void setLastHashTreeUpdatedTimestamp(long timestamp) {
		setValueIfNewValueIsGreater(rebuiltTreeTs, timestamp);
	}

	public long getLastHashTreeUpdatedTimestamp() {
		return rebuiltTreeTs.get();
	}
}
