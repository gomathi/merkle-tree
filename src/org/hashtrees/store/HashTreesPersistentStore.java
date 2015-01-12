package org.hashtrees.store;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Queue;

import org.fusesource.leveldbjni.JniDBFactory;
import org.hashtrees.thrift.generated.RemoteTreeInfo;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;
import org.hashtrees.thrift.generated.ServerName;
import org.hashtrees.util.ByteUtils;
import org.hashtrees.util.Pair;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses LevelDB for storing segment hashes and segment data. Dirty segment
 * markers are also stored on disk.
 * 
 * Stores the following data
 * 
 * 1) Metadata info [Like when the tree was built fully last time]. Format is
 * ['M'|key] -> [value] 2) SegmentData, format is ['S'|treeId|segId|key] ->
 * [value] 3) SegmentHash, format is ['H'|treeId|nodeId] -> [value] 4) TreeId,
 * format is ['T'|treeId] -> [Dummy-value]
 * 
 */

public class HashTreesPersistentStore extends HashTreesBaseStore implements
		HashTreesManagerStore {

	private static final Logger LOG = LoggerFactory
			.getLogger(HashTreesPersistentStore.class);
	private static final byte[] EMPTY_VALUE = new byte[0];

	private static final int SIZE_TREEID = ByteUtils.SIZEOF_LONG;
	private static final int SIZE_SEGID = ByteUtils.SIZEOF_INT;
	private static final int SIZE_BASE_KEY_WITH_TID = BaseKeyPrefix.LENGTH
			+ SIZE_TREEID;
	private static final int SIZE_BASE_KEY_TID_A_SEGID = BaseKeyPrefix.LENGTH
			+ SIZE_TREEID + SIZE_SEGID;

	private static enum BaseKeyPrefix {

		META_DATA_KEY_PREFIX((byte) 'M'), SEG_HASH_KEY_PREFIX((byte) 'H'), SEG_DATA_KEY_PREFIX(
				(byte) 'S'), TREEID_KEY_PREFIX((byte) 'T'), DIRTY_SEG_KEY_PREFIX(
				(byte) 'D');

		private final byte baseKeyPrefix;
		private static final int LENGTH = 1; // in terms of bytes.

		private BaseKeyPrefix(byte baseKeyPrefix) {
			this.baseKeyPrefix = baseKeyPrefix;
		}

		public byte getBaseKeyPrefix() {
			return baseKeyPrefix;
		}
	}

	private static enum MetaDataKey {

		KEY_LAST_FULLY_TREE_BUILT_TS("ltfbTs".getBytes()), KEY_SERVERNAME("sn"
				.getBytes());

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

	/**
	 * Need to inform {@link HashTreesBaseStore} about dirty segments which are
	 * marked in the previous job.
	 */
	private void initDirtySegments() {
		DBIterator itr = dbObj.iterator();
		byte[] startKey = new byte[BaseKeyPrefix.LENGTH];
		ByteBuffer bb = ByteBuffer.wrap(startKey);
		bb.put(BaseKeyPrefix.DIRTY_SEG_KEY_PREFIX.baseKeyPrefix);
		itr.seek(startKey);

		while (itr.hasNext()) {
			Entry<byte[], byte[]> entry = itr.next();
			byte[] key = entry.getKey();
			if (ByteUtils.compareTo(startKey, 0, startKey.length, key, 0,
					startKey.length) != 0)
				break;
			Pair<Long, Integer> treeIdAndDirtySegId = readTreeIdAndDirtySegmentFrom(key);
			super.setDirtySegment(treeIdAndDirtySegId.getFirst(),
					treeIdAndDirtySegId.getSecond());
		}
	}

	public HashTreesPersistentStore(String dbDir) throws Exception {
		this.dbDir = dbDir;
		this.dbObj = initDatabase(dbDir);
		initDirtySegments();
	}

	private static long getTreeId(byte[] keyPrefix) {
		ByteBuffer bb = ByteBuffer.wrap(keyPrefix);
		return bb.getLong(BaseKeyPrefix.LENGTH);
	}

	private static void prepareKeyPrefix(ByteBuffer keyToFill,
			BaseKeyPrefix keyMarker, long treeId) {
		keyToFill.put(keyMarker.getBaseKeyPrefix());
		keyToFill.putLong(treeId);
	}

	private static byte[] prepareTreeId(long treeId) {
		byte[] result = new byte[SIZE_BASE_KEY_WITH_TID];
		ByteBuffer bb = ByteBuffer.wrap(result);
		prepareKeyPrefix(bb, BaseKeyPrefix.TREEID_KEY_PREFIX, treeId);
		return result;
	}

	private static void prepareKeyPrefix(ByteBuffer keyToFill,
			BaseKeyPrefix keyMarker, long treeId, int nodeId) {
		prepareKeyPrefix(keyToFill, keyMarker, treeId);
		keyToFill.putInt(nodeId);
	}

	private static byte[] prepareSegmentHashKey(long treeId, int nodeId) {
		byte[] key = new byte[SIZE_BASE_KEY_TID_A_SEGID];
		ByteBuffer bb = ByteBuffer.wrap(key);
		prepareKeyPrefix(bb, BaseKeyPrefix.SEG_HASH_KEY_PREFIX, treeId, nodeId);
		return key;
	}

	private static byte[] readSegmentDataKey(byte[] dbSegDataKey) {
		int from = SIZE_BASE_KEY_TID_A_SEGID;
		byte[] key = ByteUtils.copy(dbSegDataKey, from, dbSegDataKey.length);
		return key;
	}

	private static byte[] prepareSegmentDataKeyPrefix(long treeId, int segId) {
		byte[] byteKey = new byte[SIZE_BASE_KEY_TID_A_SEGID];
		ByteBuffer bb = ByteBuffer.wrap(byteKey);
		prepareKeyPrefix(bb, BaseKeyPrefix.SEG_DATA_KEY_PREFIX, treeId, segId);
		return byteKey;
	}

	private static byte[] prepareSegmentDataKey(long treeId, int segId,
			ByteBuffer key) {
		byte[] byteKey = new byte[SIZE_BASE_KEY_TID_A_SEGID
				+ (key.array().length)];
		ByteBuffer bb = ByteBuffer.wrap(byteKey);
		prepareKeyPrefix(bb, BaseKeyPrefix.SEG_DATA_KEY_PREFIX, treeId, segId);
		bb.put(key.array());
		return byteKey;
	}

	private static byte[] prepareMetaDataKey(long treeId,
			MetaDataKey metaDataKey) {
		byte[] byteKey = new byte[SIZE_BASE_KEY_WITH_TID
				+ metaDataKey.getKey().length];
		ByteBuffer bb = ByteBuffer.wrap(byteKey);
		prepareKeyPrefix(bb, BaseKeyPrefix.META_DATA_KEY_PREFIX, treeId);
		bb.put(metaDataKey.getKey());
		return byteKey;
	}

	private void updateMetaData(long treeId, MetaDataKey metaDataKey,
			byte[] value) {
		byte[] key = prepareMetaDataKey(treeId, metaDataKey);
		dbObj.put(key, value);
	}

	private static byte[] prepareDirtySegmentKey(long treeId, int segId) {
		byte[] key = new byte[BaseKeyPrefix.LENGTH + ByteUtils.SIZEOF_LONG
				+ ByteUtils.SIZEOF_INT];
		ByteBuffer bb = ByteBuffer.wrap(key);
		bb.put(BaseKeyPrefix.DIRTY_SEG_KEY_PREFIX.baseKeyPrefix);
		bb.putLong(treeId);
		bb.putInt(segId);
		return bb.array();
	}

	private static Pair<Long, Integer> readTreeIdAndDirtySegmentFrom(byte[] key) {
		ByteBuffer bb = ByteBuffer.wrap(key);
		int offset = BaseKeyPrefix.LENGTH;
		long treeId = bb.getLong(offset);
		offset += ByteUtils.SIZEOF_LONG;
		int dirtySegId = bb.getInt(offset);
		return Pair.create(treeId, dirtySegId);
	}

	@Override
	public boolean setDirtySegment(long treeId, int segId) {
		boolean hasSet = super.setDirtySegment(treeId, segId);
		if (!hasSet)
			dbObj.put(prepareDirtySegmentKey(treeId, segId), EMPTY_VALUE);
		return hasSet;
	}

	@Override
	public List<Integer> getDirtySegments(long treeId) {
		return super.getDirtySegments(treeId);
	}

	@Override
	public void clearDirtySegments(long treeId, List<Integer> segIds) {
		super.clearDirtySegments(treeId, segIds);
		for (int segId : segIds) {
			byte[] key = prepareDirtySegmentKey(treeId, segId);
			dbObj.delete(key);
		}
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
	public long getLastFullyTreeBuiltTimestamp(long treeId) {
		byte[] key = prepareMetaDataKey(treeId,
				MetaDataKey.KEY_LAST_FULLY_TREE_BUILT_TS);
		byte[] value = dbObj.get(key);
		if (value != null)
			return ByteUtils.toLong(value, 0);
		return 0;
	}

	@Override
	public void deleteTree(long treeId) {
		DBIterator dbItr;
		byte[] temp = new byte[SIZE_BASE_KEY_WITH_TID];
		for (BaseKeyPrefix keyPrefix : BaseKeyPrefix.values()) {
			dbItr = dbObj.iterator();
			ByteBuffer wrap = ByteBuffer.wrap(temp);
			prepareKeyPrefix(wrap, keyPrefix, treeId);
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
		if (value != null) {
			ByteBuffer intKeyBB = ByteBuffer.wrap(key.array());
			ByteBuffer valueBB = ByteBuffer.wrap(value);
			return new SegmentData(intKeyBB, valueBB);
		}
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
				SegmentData sd = new SegmentData();
				byte[] key = readSegmentDataKey(iterator.peekNext().getKey());
				byte[] digest = iterator.peekNext().getValue();
				sd.setKey(key);
				sd.setDigest(digest);
				result.add(sd);
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
		final byte[] keyToFill = new byte[SIZE_BASE_KEY_WITH_TID];
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
					prepareKeyPrefix(bb, BaseKeyPrefix.TREEID_KEY_PREFIX,
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

	private static byte[] convertRemoteTreeInfoToBytes(RemoteTreeInfo rTree) {
		byte[] keyPrefix = prepareMetaDataKey(rTree.treeId,
				MetaDataKey.KEY_SERVERNAME);
		byte[] serverNameInBytes = rTree.sn.hostName.getBytes();
		byte[] key = new byte[keyPrefix.length + serverNameInBytes.length
				+ ByteUtils.SIZEOF_INT];
		ByteBuffer keyBB = ByteBuffer.wrap(key);
		keyBB.put(keyPrefix);
		keyBB.putInt(rTree.sn.portNo);
		keyBB.put(serverNameInBytes);
		return keyBB.array();
	}

	private static RemoteTreeInfo readRemoteTreeInfoFrom(byte[] key) {
		long treeId = getTreeId(key);
		int offset = SIZE_BASE_KEY_WITH_TID
				+ MetaDataKey.KEY_SERVERNAME.getKey().length;
		ByteBuffer bb = ByteBuffer.wrap(key);
		int portNo = bb.getInt(offset);
		offset += ByteUtils.SIZEOF_INT;
		byte[] snInBytes = ByteUtils.copy(key, offset, key.length);
		String hostName = new String(snInBytes);
		return new RemoteTreeInfo(new ServerName(hostName, portNo), treeId);
	}

	@Override
	public void addToSyncList(RemoteTreeInfo rTree) {
		dbObj.put(convertRemoteTreeInfoToBytes(rTree), EMPTY_VALUE);
	}

	@Override
	public void removeFromSyncList(RemoteTreeInfo rTree) {
		dbObj.delete(convertRemoteTreeInfoToBytes(rTree));
	}

	@Override
	public List<RemoteTreeInfo> getSyncList(long treeId) {
		DBIterator itr = dbObj.iterator();
		byte[] startKey = prepareMetaDataKey(treeId, MetaDataKey.KEY_SERVERNAME);
		itr.seek(startKey);

		List<RemoteTreeInfo> result = new ArrayList<>();
		while (itr.hasNext()) {
			Entry<byte[], byte[]> entry = itr.next();
			byte[] key = entry.getKey();
			if (ByteUtils.compareTo(startKey, 0, startKey.length, key, 0,
					startKey.length) != 0)
				break;
			result.add(readRemoteTreeInfoFrom(key));
		}
		return result;
	}

	public void close() {
		try {
			dbObj.close();
		} catch (IOException e) {
			LOG.warn("Exception occurred while closing leveldb connection.");
		}
	}

	@Override
	public void stop() {
		close();
	}
}
