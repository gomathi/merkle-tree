package org.hashtrees.store;

import static org.hashtrees.store.ByteKeyValueConverter.LEN_BASEKEY_AND_TREEID;
import static org.hashtrees.store.ByteKeyValueConverter.convertRemoteTreeInfoToBytes;
import static org.hashtrees.store.ByteKeyValueConverter.fillBaseKey;
import static org.hashtrees.store.ByteKeyValueConverter.generateBaseKey;
import static org.hashtrees.store.ByteKeyValueConverter.generateDirtySegmentKey;
import static org.hashtrees.store.ByteKeyValueConverter.generateMetaDataKey;
import static org.hashtrees.store.ByteKeyValueConverter.generateRebuildMarkerKey;
import static org.hashtrees.store.ByteKeyValueConverter.generateSegmentDataKey;
import static org.hashtrees.store.ByteKeyValueConverter.generateSegmentDataValue;
import static org.hashtrees.store.ByteKeyValueConverter.generateSegmentHashKey;
import static org.hashtrees.store.ByteKeyValueConverter.generateTreeIdKey;
import static org.hashtrees.store.ByteKeyValueConverter.readRemoteTreeInfoFrom;
import static org.hashtrees.store.ByteKeyValueConverter.readSegmentDataDigest;
import static org.hashtrees.store.ByteKeyValueConverter.readSegmentDataKey;
import static org.hashtrees.store.ByteKeyValueConverter.readSegmentDataValue;
import static org.hashtrees.store.ByteKeyValueConverter.readTreeIdFromBaseKey;

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

import org.apache.commons.io.FileUtils;
import org.fusesource.leveldbjni.JniDBFactory;
import org.hashtrees.store.ByteKeyValueConverter.BaseKey;
import org.hashtrees.store.ByteKeyValueConverter.MetaDataKey;
import org.hashtrees.thrift.generated.RemoteTreeInfo;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;
import org.hashtrees.util.ByteUtils;
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
 * ['M'|treeId|key] -> [value] 2) SegmentData, format is ['S'|treeId|segId|key]
 * -> [digest|marker|actualValue] 3) SegmentHash, format is ['H'|treeId|nodeId]
 * -> [value] 4) TreeId, format is ['T'|treeId] -> [EMPTY_VALUE] 5) Dirty
 * segment key ['D'|treeId|dirtySegId] -> [EMPTY_VALUE]
 * 
 */

public class HashTreesPersistentStore extends HashTreesBaseStore implements
		HashTreesManagerStore {

	private static final Logger LOG = LoggerFactory
			.getLogger(HashTreesPersistentStore.class);
	private static final byte[] EMPTY_VALUE = new byte[0];

	private final String dbDir;
	private final DB dbObj;

	public HashTreesPersistentStore(String dbDir) throws Exception {
		this.dbDir = dbDir;
		this.dbObj = initDB(dbDir);
		initDirtySegments();
	}

	private static boolean createDir(String dirName) {
		File file = new File(dirName);
		if (file.exists())
			return true;
		return file.mkdirs();
	}

	private static DB initDB(String dbDir) throws IOException {
		createDir(dbDir);
		Options options = new Options();
		options.createIfMissing(true);
		return new JniDBFactory().open(new File(dbDir), options);
	}

	/**
	 * Need to inform {@link HashTreesBaseStore} about dirty segments which are
	 * marked in the previous run.
	 */
	private void initDirtySegments() {
		DBIterator itr = dbObj.iterator();
		byte[] startKey = new byte[BaseKey.LENGTH];
		ByteBuffer bb = ByteBuffer.wrap(startKey);
		bb.put(BaseKey.DIRTY_SEG.key);
		itr.seek(startKey);

		while (itr.hasNext()) {
			Entry<byte[], byte[]> entry = itr.next();
			byte[] key = entry.getKey();
			if (ByteUtils.compareTo(startKey, 0, startKey.length, key, 0,
					startKey.length) != 0)
				break;
			bb = ByteBuffer.wrap(key);
			int segId = bb.getInt(LEN_BASEKEY_AND_TREEID);
			long treeId = readTreeIdFromBaseKey(key);
			super.setDirtySegment(treeId, segId);
		}
	}

	public String getDbDir() {
		return dbDir;
	}

	@Override
	public boolean setDirtySegment(long treeId, int segId) {
		boolean hasSet = super.setDirtySegment(treeId, segId);
		if (!hasSet)
			dbObj.put(generateDirtySegmentKey(treeId, segId), EMPTY_VALUE);
		return hasSet;
	}

	@Override
	public void clearDirtySegment(long treeId, int segId) {
		super.clearDirtySegment(treeId, segId);
		byte[] key = generateDirtySegmentKey(treeId, segId);
		dbObj.delete(key);
	}

	@Override
	public List<Integer> getDirtySegments(long treeId) {
		return super.getDirtySegments(treeId);
	}

	@Override
	public void putSegmentHash(long treeId, int nodeId, ByteBuffer digest) {
		dbObj.put(generateTreeIdKey(treeId), EMPTY_VALUE);
		dbObj.put(generateSegmentHashKey(treeId, nodeId), digest.array());
	}

	@Override
	public SegmentHash getSegmentHash(long treeId, int nodeId) {
		byte[] value = dbObj.get(generateSegmentHashKey(treeId, nodeId));
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
	public void setCompleteRebuiltTimestamp(long treeId, long ts) {
		byte[] value = new byte[ByteUtils.SIZEOF_LONG];
		ByteBuffer bbValue = ByteBuffer.wrap(value);
		bbValue.putLong(ts);
		byte[] key = generateMetaDataKey(MetaDataKey.FULL_REBUILT_TS, treeId);
		dbObj.put(key, value);
	}

	@Override
	public long getCompleteRebuiltTimestamp(long treeId) {
		byte[] key = generateMetaDataKey(MetaDataKey.FULL_REBUILT_TS, treeId);
		byte[] value = dbObj.get(key);
		return (value == null) ? 0 : ByteUtils.toLong(value, 0);
	}

	@Override
	public void deleteTree(long treeId) {
		DBIterator dbItr;
		byte[] temp = new byte[LEN_BASEKEY_AND_TREEID];
		for (BaseKey keyPrefix : BaseKey.values()) {
			dbItr = dbObj.iterator();
			ByteBuffer wrap = ByteBuffer.wrap(temp);
			fillBaseKey(wrap, keyPrefix, treeId);
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
		dbObj.put(generateTreeIdKey(treeId), EMPTY_VALUE);
		byte[] dbKey = generateSegmentDataKey(treeId, segId, key);
		dbObj.put(dbKey, digest.array());
	}

	@Override
	public void putSegmentData(long treeId, int segId, ByteBuffer key,
			ByteBuffer value, ByteBuffer digest) {
		putSegmentData(treeId, segId, key,
				ByteBuffer.wrap(generateSegmentDataValue(digest, value)));
	}

	@Override
	public ByteBuffer getValue(long treeId, int segId, ByteBuffer key) {
		byte[] dbKey = generateSegmentDataKey(treeId, segId, key);
		byte[] dbValue = dbObj.get(dbKey);
		return (dbValue == null) ? null : ByteBuffer
				.wrap(readSegmentDataValue(dbValue));
	}

	@Override
	public SegmentData getSegmentData(long treeId, int segId, ByteBuffer key) {
		byte[] dbKey = generateSegmentDataKey(treeId, segId, key);
		byte[] value = dbObj.get(dbKey);
		if (value != null) {
			ByteBuffer intKeyBB = ByteBuffer.wrap(key.array());
			ByteBuffer valueBB = ByteBuffer.wrap(readSegmentDataDigest(value));
			return new SegmentData(intKeyBB, valueBB);
		}
		return null;
	}

	@Override
	public void deleteSegmentData(long treeId, int segId, ByteBuffer key) {
		byte[] dbKey = generateSegmentDataKey(treeId, segId, key);
		dbObj.delete(dbKey);
	}

	@Override
	public Iterator<SegmentData> getSegmentDataIterator(long treeId) {
		final byte[] startKey = generateBaseKey(BaseKey.SEG_DATA, treeId);
		final DBIterator iterator = dbObj.iterator();
		iterator.seek(startKey);
		return new Iterator<SegmentData>() {

			private Queue<SegmentData> internalQue = new ArrayDeque<>();

			@Override
			public boolean hasNext() {
				loadNextElement();
				return !internalQue.isEmpty();
			}

			@Override
			public SegmentData next() {
				if (internalQue.isEmpty())
					throw new NoSuchElementException(
							"There is no next tree id.");
				return internalQue.remove();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException(
						"Remove is not supported.");
			}

			private void loadNextElement() {
				if (internalQue.isEmpty() && iterator.hasNext()) {
					byte[] key = readSegmentDataKey(iterator.peekNext()
							.getKey());
					byte[] value = iterator.peekNext().getValue();
					if (ByteUtils.compareTo(startKey, 0, startKey.length, key,
							0, startKey.length) != 0)
						return;
					SegmentData sd = new SegmentData();
					sd.setKey(key);
					sd.setDigest(readSegmentDataDigest(value));
					internalQue.add(sd);
				}
			}
		};
	}

	@Override
	public List<SegmentData> getSegment(long treeId, int segId) {
		List<SegmentData> result = new ArrayList<SegmentData>();
		byte[] startKey = generateSegmentDataKey(treeId, segId);
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

	@Override
	public void markSegments(long treeId, List<Integer> segIds) {
		for (int segId : segIds) {
			byte[] key = generateRebuildMarkerKey(treeId, segId);
			dbObj.put(key, EMPTY_VALUE);
		}
	}

	@Override
	public void unmarkSegments(long treeId, List<Integer> segIds) {
		for (int segId : segIds) {
			byte[] key = generateRebuildMarkerKey(treeId, segId);
			dbObj.delete(key);
		}
	}

	@Override
	public List<Integer> getMarkedSegments(long treeId) {
		DBIterator itr = dbObj.iterator();
		ByteBuffer bb;
		byte[] startKey = generateBaseKey(BaseKey.REBUILD_MARKER, treeId);
		itr.seek(startKey);

		List<Integer> result = new ArrayList<>();
		while (itr.hasNext()) {
			Entry<byte[], byte[]> entry = itr.next();
			byte[] key = entry.getKey();
			if (ByteUtils.compareTo(startKey, 0, startKey.length, key, 0,
					startKey.length) != 0)
				break;
			bb = ByteBuffer.wrap(key);
			int segId = bb.getInt(LEN_BASEKEY_AND_TREEID);
			result.add(segId);
		}
		return result;
	}

	/**
	 * The iterator returned by the this function is not thread safe.
	 */
	@Override
	public Iterator<Long> getAllTreeIds() {
		final byte[] keyToFill = new byte[LEN_BASEKEY_AND_TREEID];
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
					fillBaseKey(bb, BaseKey.TREEID, lastTreeId + 1);
					iterator.seek(bb.array());
					if (iterator.hasNext()) {
						byte[] key = iterator.next().getKey();
						internalQue.add(readTreeIdFromBaseKey(key));
					}
				}
			}
		};
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
		byte[] startKey = generateMetaDataKey(MetaDataKey.SERVER_NAME, treeId);
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

	/**
	 * Deletes the db files.
	 * 
	 */
	public void delete() {
		stop();
		File dbDirObj = new File(dbDir);
		if (dbDirObj.exists())
			FileUtils.deleteQuietly(dbDirObj);
	}

	@Override
	public void stop() {
		try {
			dbObj.close();
		} catch (IOException e) {
			LOG.warn("Exception occurred while closing leveldb connection.");
		}
	}
}
