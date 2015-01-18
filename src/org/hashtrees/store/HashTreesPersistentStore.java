package org.hashtrees.store;

import static org.hashtrees.store.ByteKeyValueConverter.KVBYTES_TO_SERVERNAME_FROM_METADATAKEY;
import static org.hashtrees.store.ByteKeyValueConverter.KVBYTES_TO_SEGDATA_CONVERTER;
import static org.hashtrees.store.ByteKeyValueConverter.KVBYTES_TO_SEGID_CONVERTER;
import static org.hashtrees.store.ByteKeyValueConverter.KVBYTES_TO_SERVERNAME_CONVERTER;
import static org.hashtrees.store.ByteKeyValueConverter.KVBYTES_TO_TREEID_CONVERTER;
import static org.hashtrees.store.ByteKeyValueConverter.KVBYTES_TO_TREEID_SEGID_CONVERTER;
import static org.hashtrees.store.ByteKeyValueConverter.LEN_BASEKEY_AND_TREEID;
import static org.hashtrees.store.ByteKeyValueConverter.convertServerNameAndTreeIdToBytes;
import static org.hashtrees.store.ByteKeyValueConverter.convertServerNameToBytes;
import static org.hashtrees.store.ByteKeyValueConverter.fillBaseKey;
import static org.hashtrees.store.ByteKeyValueConverter.generateBaseKey;
import static org.hashtrees.store.ByteKeyValueConverter.generateDirtySegmentKey;
import static org.hashtrees.store.ByteKeyValueConverter.generateMetaDataKey;
import static org.hashtrees.store.ByteKeyValueConverter.generateRebuildMarkerKey;
import static org.hashtrees.store.ByteKeyValueConverter.generateSegmentDataKey;
import static org.hashtrees.store.ByteKeyValueConverter.generateSegmentHashKey;
import static org.hashtrees.store.ByteKeyValueConverter.generateTreeIdKey;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;

import javax.annotation.concurrent.NotThreadSafe;

import org.apache.commons.io.FileUtils;
import org.fusesource.leveldbjni.JniDBFactory;
import org.hashtrees.store.ByteKeyValueConverter.BaseKey;
import org.hashtrees.store.ByteKeyValueConverter.MetaDataKey;
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

import com.google.common.base.Function;
import com.google.common.collect.Lists;

/**
 * Uses LevelDB for storing segment hashes and segment data. Dirty segment
 * markers are also stored on disk.
 * 
 * Stores the following data
 * 
 * 1) Metadata info [Like when the tree was built fully last time]. Format is
 * ['M'|treeId|key] -> [value] 2) SegmentData, format is ['S'|treeId|segId|key]
 * -> [digest] 3) SegmentHash, format is ['H'|treeId|nodeId] -> [value] 4)
 * TreeId, format is ['T'|treeId] -> [EMPTY_VALUE] 5) Dirty segment key
 * ['D'|treeId|dirtySegId] -> [EMPTY_VALUE]
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
		byte[] prefixKey = new byte[BaseKey.LENGTH];
		ByteBuffer bb = ByteBuffer.wrap(prefixKey);
		bb.put(BaseKey.DIRTY_SEG.key);
		itr.seek(prefixKey);

		Iterator<Pair<Long, Integer>> dirtySegments = new DataIterator<>(
				prefixKey, KVBYTES_TO_TREEID_SEGID_CONVERTER, itr);

		while (dirtySegments.hasNext()) {
			Pair<Long, Integer> treeIdAndDirtySeg = dirtySegments.next();
			super.setDirtySegment(treeIdAndDirtySeg.getFirst(),
					treeIdAndDirtySeg.getSecond());
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
	 * The iterator returned by the this function is not thread safe.
	 */
	@Override
	public Iterator<Long> getAllTreeIds() {
		final byte[] prefixKey = new byte[BaseKey.LENGTH];
		ByteBuffer bb = ByteBuffer.wrap(prefixKey);
		bb.put(BaseKey.TREEID.key);
		final DBIterator itr = dbObj.iterator();
		itr.seek(prefixKey);
		return new DataIterator<>(prefixKey, KVBYTES_TO_TREEID_CONVERTER, itr);
	}

	@Override
	public void addServerNameAndTreeIdToSyncList(ServerName rTree, long treeId) {
		dbObj.put(convertServerNameAndTreeIdToBytes(rTree, treeId), EMPTY_VALUE);
	}

	@Override
	public void removeServerNameAndTreeIdFromSyncList(ServerName sn, long treeId) {
		dbObj.delete(convertServerNameAndTreeIdToBytes(sn, treeId));
	}

	@Override
	public List<ServerName> getServerNameListFor(long treeId) {
		byte[] prefixKey = generateMetaDataKey(MetaDataKey.SERVER_NAME, treeId);
		DBIterator itr = dbObj.iterator();
		itr.seek(prefixKey);
		return Lists.newArrayList(new DataIterator<>(prefixKey,
				KVBYTES_TO_SERVERNAME_FROM_METADATAKEY, itr));
	}

	@Override
	public void addServerNameToSyncList(ServerName sn) {
		dbObj.put(convertServerNameToBytes(sn), EMPTY_VALUE);
	}

	@Override
	public void removeServerNameFromSyncList(ServerName sn) {
		dbObj.delete(convertServerNameToBytes(sn));
	}

	@Override
	public List<ServerName> getServerNameList() {
		byte[] prefixKey = new byte[BaseKey.LENGTH];
		ByteBuffer bb = ByteBuffer.wrap(prefixKey);
		bb.put(BaseKey.SERVER_NAME.key);

		DBIterator itr = dbObj.iterator();
		itr.seek(prefixKey);
		return Lists.newArrayList(new DataIterator<>(prefixKey,
				KVBYTES_TO_SERVERNAME_CONVERTER, itr));
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

	@NotThreadSafe
	private static class DataIterator<T> implements Iterator<T> {

		private final Queue<T> dataQueue = new ArrayDeque<>(1);
		private final byte[] prefixKey;
		private final Function<Map.Entry<byte[], byte[]>, T> converter;
		private final Iterator<Map.Entry<byte[], byte[]>> kvBytesItr;

		public DataIterator(byte[] prefixKey,
				Function<Map.Entry<byte[], byte[]>, T> converter,
				Iterator<Map.Entry<byte[], byte[]>> kvBytesItr) {
			this.prefixKey = prefixKey;
			this.converter = converter;
			this.kvBytesItr = kvBytesItr;
		}

		@Override
		public boolean hasNext() {
			loadNextElement();
			return dataQueue.size() > 0;
		}

		@Override
		public T next() {
			if (!hasNext())
				throw new NoSuchElementException("No more elements exist.");
			return dataQueue.remove();
		}

		private void loadNextElement() {
			if (dataQueue.isEmpty() && kvBytesItr.hasNext()) {
				Map.Entry<byte[], byte[]> entry = kvBytesItr.next();
				if (ByteUtils.compareTo(prefixKey, 0, prefixKey.length,
						entry.getKey(), 0, prefixKey.length) != 0)
					return;
				dataQueue.add(converter.apply(entry));
			}
		}
	}
}
