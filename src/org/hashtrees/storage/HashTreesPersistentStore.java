package org.hashtrees.storage;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.fusesource.leveldbjni.JniDBFactory;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;
import org.hashtrees.util.ByteUtils;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;

/**
 * Uses LevelDB for storing segment hashes and segment data. Dirty segment
 * markers are stored in memory.
 * 
 * Stores the following data
 * 
 * 1) Metadata info [Like when the tree was built fully last time]. Format is
 * ['M'|key] -> [value] 2) SegmentData, format is ['S'|treeId|segId|key] ->
 * [value] 3) SegmentHash, format is ['H'|treeId|nodeId] -> [value] 4) TreeId,
 * format is ['T'|treeId] -> [Dummy-value]
 * 
 */

public class HashTreesPersistentStore extends HashTreesBaseStore {

	private static final Logger LOG = Logger
			.getLogger(HashTreesPersistentStore.class);

	private static final int SIZE_TREEID = ByteUtils.SIZEOF_LONG;
	private static final int SIZE_SEGID = ByteUtils.SIZEOF_INT;
	private static final int SIZE_PREFIX_KEY_TID = 1 + SIZE_TREEID;
	private static final int SIZE_PREFIX_KEY_TID_AND_SEGID = 1 + SIZE_TREEID
			+ SIZE_SEGID;
	private static final byte[] EMPTY_VALUE = new byte[0];

	private static enum KeyMarker {

		META_DATA_MARKER((byte) 'M'), SEG_HASH_MARKER((byte) 'H'), SEG_DATA_MARKER(
				(byte) 'S'), TREEID_MARKER((byte) 'T');

		private final byte keyMarker;

		private KeyMarker(byte keyMarker) {
			this.keyMarker = keyMarker;
		}

		public byte getKeyMarker() {
			return keyMarker;
		}
	}

	private static enum MetaDataKey {

		KEY_LAST_FULLY_TREE_BUILT_TS("ltfbTs".getBytes()), KEY_LAST_TREE_BUILT_TS(
				"ltbTs".getBytes());

		private final byte[] key;

		private MetaDataKey(byte[] key) {
			this.key = key;
		}

		public byte[] getKey() {
			return key;
		}
	}

	private final String dbDir;
	private final DB dbObj;

	public HashTreesPersistentStore(String dbDir) throws Exception {
		this.dbDir = dbDir;
		this.dbObj = initDatabase(dbDir);
	}

	private static boolean createDir(String dirName) {
		File file = new File(dirName);
		if (file.exists())
			return true;
		return file.mkdirs();
	}

	private static DB initDatabase(String dbDir) throws IOException {
		createDir(dbDir);
		Options options = new Options();
		options.createIfMissing(true);
		return new JniDBFactory().open(new File(dbDir), options);
	}

	public void close() {
		try {
			dbObj.close();
		} catch (IOException e) {
			LOG.warn("Exception occurred while closing leveldb connection.");
		}
	}

	private static long getTreeId(byte[] keyPrefix) {
		ByteBuffer bb = ByteBuffer.wrap(keyPrefix);
		return bb.getLong(1);
	}

	private static byte[] prepareTreeId(long treeId) {
		byte[] result = new byte[SIZE_PREFIX_KEY_TID];
		ByteBuffer bb = ByteBuffer.wrap(result);
		prepareKeyPrefix(bb, KeyMarker.TREEID_MARKER, treeId);
		return result;
	}

	private static void prepareKeyPrefix(ByteBuffer keyToFill,
			KeyMarker keyMarker, long treeId) {
		keyToFill.put(keyMarker.getKeyMarker());
		keyToFill.putLong(treeId);
	}

	private static void prepareKeyPrefix(ByteBuffer keyToFill,
			KeyMarker keyMarker, long treeId, int nodeId) {
		prepareKeyPrefix(keyToFill, keyMarker, treeId);
		keyToFill.putInt(nodeId);
	}

	private static byte[] prepareSegmentHashKey(long treeId, int nodeId) {
		byte[] key = new byte[SIZE_PREFIX_KEY_TID_AND_SEGID];
		ByteBuffer bb = ByteBuffer.wrap(key);
		prepareKeyPrefix(bb, KeyMarker.SEG_HASH_MARKER, treeId, nodeId);
		return key;
	}

	private static byte[] readSegmentDataKey(byte[] dbSegDataKey) {
		int from = SIZE_PREFIX_KEY_TID_AND_SEGID;
		byte[] key = ByteUtils.copy(dbSegDataKey, from, dbSegDataKey.length);
		return key;
	}

	private static byte[] prepareSegmentDataKeyPrefix(long treeId, int segId) {
		byte[] byteKey = new byte[SIZE_PREFIX_KEY_TID_AND_SEGID];
		ByteBuffer bb = ByteBuffer.wrap(byteKey);
		prepareKeyPrefix(bb, KeyMarker.SEG_DATA_MARKER, treeId, segId);
		return byteKey;
	}

	private static byte[] prepareSegmentDataKey(long treeId, int segId,
			ByteBuffer key) {
		byte[] byteKey = new byte[SIZE_PREFIX_KEY_TID_AND_SEGID
				+ (key.array().length)];
		ByteBuffer bb = ByteBuffer.wrap(byteKey);
		prepareKeyPrefix(bb, KeyMarker.SEG_DATA_MARKER, treeId, segId);
		bb.put(key.array());
		return byteKey;
	}

	private static byte[] prepareMetaDataKey(long treeId,
			MetaDataKey metaDataKey) {
		byte[] byteKey = new byte[SIZE_PREFIX_KEY_TID
				+ metaDataKey.getKey().length];
		ByteBuffer bb = ByteBuffer.wrap(byteKey);
		prepareKeyPrefix(bb, KeyMarker.META_DATA_MARKER, treeId);
		bb.put(metaDataKey.getKey());
		return byteKey;
	}

	private void updateMetaData(long treeId, MetaDataKey metaDataKey,
			byte[] value) {
		byte[] key = prepareMetaDataKey(treeId, metaDataKey);
		dbObj.put(key, value);
	}

	@Override
	public void putSegmentHash(long treeId, int nodeId, ByteBuffer digest) {
		dbObj.put(prepareTreeId(treeId), EMPTY_VALUE);
		dbObj.put(prepareSegmentHashKey(treeId, nodeId), digest.array());
	}

	@Override
	public SegmentHash getSegmentHash(long treeId, int nodeId) {
		byte[] value = dbObj.get(prepareSegmentHashKey(treeId, nodeId));
		if (value != null)
			return new SegmentHash(nodeId, ByteBuffer.wrap(value));
		return null;
	}

	@Override
	public List<SegmentHash> getSegmentHashes(long treeId,
			Collection<Integer> nodeIds) {
		List<SegmentHash> result = new ArrayList<SegmentHash>();
		SegmentHash temp;
		for (int nodeId : nodeIds) {
			temp = getSegmentHash(treeId, nodeId);
			if (temp != null)
				result.add(temp);
		}
		return result;
	}

	@Override
	public void setLastFullyTreeBuiltTimestamp(long treeId, long timestamp) {
		byte[] value = new byte[ByteUtils.SIZEOF_LONG];
		ByteBuffer bbValue = ByteBuffer.wrap(value);
		bbValue.putLong(timestamp);
		updateMetaData(treeId, MetaDataKey.KEY_LAST_FULLY_TREE_BUILT_TS, value);
	}

	@Override
	public long getLastFullyTreeReBuiltTimestamp(long treeId) {
		byte[] key = prepareMetaDataKey(treeId,
				MetaDataKey.KEY_LAST_FULLY_TREE_BUILT_TS);
		byte[] value = dbObj.get(key);
		if (value != null)
			return ByteUtils.toLong(value, 0);
		return 0;
	}

	@Override
	public void setLastHashTreeUpdatedTimestamp(long treeId, long timestamp) {
		byte[] value = new byte[ByteUtils.SIZEOF_LONG];
		ByteBuffer bbValue = ByteBuffer.wrap(value);
		bbValue.putLong(timestamp);
		updateMetaData(treeId, MetaDataKey.KEY_LAST_TREE_BUILT_TS, value);
	}

	@Override
	public long getLastHashTreeUpdatedTimestamp(long treeId) {
		byte[] key = prepareMetaDataKey(treeId,
				MetaDataKey.KEY_LAST_TREE_BUILT_TS);
		byte[] value = dbObj.get(key);
		if (value != null)
			return ByteUtils.toLong(value, 0);
		return 0;
	}

	@Override
	public void deleteTree(long treeId) {
		DBIterator dbItr;
		byte[] temp = new byte[SIZE_PREFIX_KEY_TID];
		for (KeyMarker keyMarker : KeyMarker.values()) {
			dbItr = dbObj.iterator();
			ByteBuffer wrap = ByteBuffer.wrap(temp);
			prepareKeyPrefix(wrap, keyMarker, treeId);
			dbItr.seek(wrap.array());
			for (; dbItr.hasNext(); dbItr.next()) {
				if (ByteUtils.compareTo(temp, 0, temp.length, dbItr.peekNext()
						.getKey(), 0, temp.length) != 0)
					break;
				dbObj.delete(dbItr.peekNext().getKey());
			}
		}
	}

	@Override
	public void putSegmentData(long treeId, int segId, ByteBuffer key,
			ByteBuffer digest) {
		dbObj.put(prepareTreeId(treeId), EMPTY_VALUE);
		byte[] dbKey = prepareSegmentDataKey(treeId, segId, key);
		dbObj.put(dbKey, digest.array());
	}

	@Override
	public SegmentData getSegmentData(long treeId, int segId, ByteBuffer key) {
		byte[] dbKey = prepareSegmentDataKey(treeId, segId, key);
		byte[] value = dbObj.get(dbKey);
		if (value != null)
			return new SegmentData(key, ByteBuffer.wrap(value));
		return null;
	}

	@Override
	public void deleteSegmentData(long treeId, int segId, ByteBuffer key) {
		byte[] dbKey = prepareSegmentDataKey(treeId, segId, key);
		dbObj.delete(dbKey);
	}

	@Override
	public List<SegmentData> getSegment(long treeId, int segId) {
		List<SegmentData> result = new ArrayList<SegmentData>();
		byte[] startKey = prepareSegmentDataKeyPrefix(treeId, segId);
		DBIterator iterator = dbObj.iterator();
		try {
			for (iterator.seek(startKey); iterator.hasNext(); iterator.next()) {
				if (ByteUtils.compareTo(startKey, 0, startKey.length, iterator
						.peekNext().getKey(), 0, startKey.length) != 0)
					break;
				ByteBuffer key = ByteBuffer.wrap(readSegmentDataKey(iterator
						.peekNext().getKey()));
				ByteBuffer digest = ByteBuffer.wrap(iterator.peekNext()
						.getValue());
				result.add(new SegmentData(key, digest));
			}
		} finally {
			try {
				iterator.close();
			} catch (IOException e) {
				LOG.warn("Exception occurred while closing the DBIterator.", e);
			}
		}
		return result;
	}

	/**
	 * The iterator returned by the this function is not thread safe.
	 */
	@Override
	public Iterator<Long> getAllTreeIds() {
		final byte[] keyToFill = new byte[SIZE_PREFIX_KEY_TID];
		final DBIterator iterator = dbObj.iterator();
		return new Iterator<Long>() {

			private Queue<Long> internalQue = new ArrayDeque<>();
			private long lastTreeId = -1;

			@Override
			public boolean hasNext() {
				loadNextElement();
				return !internalQue.isEmpty();
			}

			@Override
			public Long next() {
				if (internalQue.isEmpty())
					throw new NoSuchElementException(
							"There is no next tree id.");
				lastTreeId = internalQue.remove();
				return lastTreeId;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException(
						"Remove is not supported.");
			}

			private void loadNextElement() {
				if (internalQue.isEmpty()) {
					ByteBuffer bb = ByteBuffer.wrap(keyToFill);
					prepareKeyPrefix(bb, KeyMarker.TREEID_MARKER,
							lastTreeId + 1);
					iterator.seek(bb.array());
					if (iterator.hasNext()) {
						byte[] key = iterator.next().getKey();
						internalQue.add(getTreeId(key));
					}
				}
			}
		};
	}

	public String getDbDir() {
		return dbDir;
	}
}
