package org.hashtrees.store;

import static org.hashtrees.store.ByteKeyValueConverter.KVBYTES_TO_SEGDATA_CONVERTER;
import static org.hashtrees.store.ByteKeyValueConverter.KVBYTES_TO_SEGID_CONVERTER;
import static org.hashtrees.store.ByteKeyValueConverter.LEN_BASEKEY_AND_TREEID;
import static org.hashtrees.store.ByteKeyValueConverter.fillBaseKey;
import static org.hashtrees.store.ByteKeyValueConverter.generateBaseKey;
import static org.hashtrees.store.ByteKeyValueConverter.generateDirtySegmentKey;
import static org.hashtrees.store.ByteKeyValueConverter.generateMetaDataKey;
import static org.hashtrees.store.ByteKeyValueConverter.generateRebuildMarkerKey;
import static org.hashtrees.store.ByteKeyValueConverter.generateSegmentDataKey;
import static org.hashtrees.store.ByteKeyValueConverter.generateSegmentHashKey;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.fusesource.leveldbjni.JniDBFactory;
import org.hashtrees.store.ByteKeyValueConverter.BaseKey;
import org.hashtrees.store.ByteKeyValueConverter.MetaDataKey;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;
import org.hashtrees.util.ByteUtils;
import org.hashtrees.util.DataIterator;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

/**
 * Uses LevelDB for storing segment hashes and segment data. Dirty segment
 * markers are also stored on disk.
 * 
 * The byte keys and values are generated from {@link ByteKeyValueConverter}.
 * Look at the class for more information about internal key format.
 */

public class HashTreesPersistentStore extends HashTreesBaseStore {

	private static final Logger LOG = LoggerFactory
			.getLogger(HashTreesPersistentStore.class);
	private static final byte[] EMPTY_VALUE = new byte[0];

	private final String dbDir;
	private final DB dbObj;

	public HashTreesPersistentStore(String dbDir) throws IOException {
		this.dbDir = dbDir;
		this.dbObj = initDB(dbDir);
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

	public String getDbDir() {
		return dbDir;
	}

	@Override
	protected void setDirtySegmentInternal(long treeId, int segId) {
		dbObj.put(generateDirtySegmentKey(treeId, segId), EMPTY_VALUE);
	}

	@Override
	protected void clearDirtySegmentInternal(long treeId, int segId) {
		byte[] key = generateDirtySegmentKey(treeId, segId);
		dbObj.delete(key);
	}

	@Override
	protected List<Integer> getDirtySegmentsInternal(long treeId) {
		DBIterator itr = dbObj.iterator();
		byte[] prefixKey = generateBaseKey(BaseKey.DIRTY_SEG, treeId);
		itr.seek(prefixKey);

		Iterator<Integer> dirtySegmentsItr = new DataIterator<>(prefixKey,
				KVBYTES_TO_SEGID_CONVERTER, itr);

		List<Integer> dirtySegments = new ArrayList<>();
		while (dirtySegmentsItr.hasNext()) {
			Integer treeIdAndDirtySeg = dirtySegmentsItr.next();
			dirtySegments.add(treeIdAndDirtySeg);
		}
		return dirtySegments;
	}

	@Override
	public void putSegmentHash(long treeId, int nodeId, ByteBuffer digest) {
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
		byte[] dbKey = generateSegmentDataKey(treeId, segId, key);
		dbObj.put(dbKey, digest.array());
	}

	@Override
	public SegmentData getSegmentData(long treeId, int segId, ByteBuffer key) {
		byte[] dbKey = generateSegmentDataKey(treeId, segId, key);
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
		byte[] dbKey = generateSegmentDataKey(treeId, segId, key);
		dbObj.delete(dbKey);
	}

	@Override
	public Iterator<SegmentData> getSegmentDataIterator(long treeId) {
		final byte[] startKey = generateBaseKey(BaseKey.SEG_DATA, treeId);
		final DBIterator iterator = dbObj.iterator();
		iterator.seek(startKey);
		return new DataIterator<>(startKey, KVBYTES_TO_SEGDATA_CONVERTER,
				iterator);
	}

	@Override
	public List<SegmentData> getSegment(long treeId, int segId) {
		byte[] startKey = generateSegmentDataKey(treeId, segId);
		DBIterator iterator = dbObj.iterator();
		iterator.seek(startKey);
		return Lists.newArrayList(new DataIterator<>(startKey,
				KVBYTES_TO_SEGDATA_CONVERTER, iterator));
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
		byte[] startKey = generateBaseKey(BaseKey.REBUILD_MARKER, treeId);
		DBIterator itr = dbObj.iterator();
		itr.seek(startKey);

		return Lists.newArrayList(new DataIterator<>(startKey,
				KVBYTES_TO_SEGID_CONVERTER, itr));
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
	public void start() {
		// Nothing to do.
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
