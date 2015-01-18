package org.hashtrees.store;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;

import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.ServerName;
import org.hashtrees.util.ByteUtils;
import org.hashtrees.util.Pair;

import com.google.common.base.Function;

public class ByteKeyValueConverter {

	public static final int LEN_BASEKEY_AND_TREEID = BaseKey.LENGTH
			+ ByteUtils.SIZEOF_LONG;
	public static final int DIGEST_LENTH = 20; // Sha1 is used for digest
												// calculation, which provides a
												// 20 bytes digest.

	public static enum BaseKey {

		META_DATA((byte) 'M'), SEG_HASH((byte) 'H'), SEG_DATA((byte) 'S'), TREEID(
				(byte) 'T'), DIRTY_SEG((byte) 'D'), REBUILD_MARKER((byte) 'R'), SERVER_NAME(
				(byte) 'G');

		public static final int LENGTH = 1; // in terms of bytes.
		public final byte key;

		private BaseKey(byte key) {
			this.key = key;
		}
	}

	public static enum MetaDataKey {

		FULL_REBUILT_TS("ltfbTs".getBytes()), SERVER_NAME("sn".getBytes());

		public final byte[] key;
		public final int length;

		private MetaDataKey(byte[] key) {
			this.key = key;
			this.length = key.length;
		}
	}

	public static Function<Map.Entry<byte[], byte[]>, SegmentData> KVBYTES_TO_SEGDATA_CONVERTER = new Function<Map.Entry<byte[], byte[]>, SegmentData>() {

		@Override
		public SegmentData apply(Entry<byte[], byte[]> kv) {
			byte[] key = readSegmentDataKey(kv.getKey());
			return new SegmentData(ByteBuffer.wrap(key), ByteBuffer.wrap(kv
					.getValue()));
		}
	};

	public static Function<Map.Entry<byte[], byte[]>, ServerName> KVBYTES_TO_SERVERNAME_FROM_METADATAKEY = new Function<Map.Entry<byte[], byte[]>, ServerName>() {

		@Override
		public ServerName apply(Entry<byte[], byte[]> kv) {
			return readServerNameAndTreeIdFrom(kv.getKey()).getFirst();
		}
	};

	public static Function<Map.Entry<byte[], byte[]>, ServerName> KVBYTES_TO_SERVERNAME_CONVERTER = new Function<Map.Entry<byte[], byte[]>, ServerName>() {

		@Override
		public ServerName apply(Entry<byte[], byte[]> kv) {
			return readServerNameFrom(kv.getKey());
		}
	};

	public static Function<Map.Entry<byte[], byte[]>, Long> KVBYTES_TO_TREEID_CONVERTER = new Function<Map.Entry<byte[], byte[]>, Long>() {

		@Override
		public Long apply(Entry<byte[], byte[]> kv) {
			return readTreeIdFromBaseKey(kv.getKey());
		}
	};

	public static Function<Map.Entry<byte[], byte[]>, Integer> KVBYTES_TO_SEGID_CONVERTER = new Function<Map.Entry<byte[], byte[]>, Integer>() {

		@Override
		public Integer apply(Entry<byte[], byte[]> kv) {
			return readSegmentIdFrom(kv.getKey());
		}
	};

	public static Function<Map.Entry<byte[], byte[]>, Pair<Long, Integer>> KVBYTES_TO_TREEID_SEGID_CONVERTER = new Function<Map.Entry<byte[], byte[]>, Pair<Long, Integer>>() {

		@Override
		public Pair<Long, Integer> apply(Entry<byte[], byte[]> kv) {
			long treeId = readTreeIdFromBaseKey(kv.getKey());
			int segId = readSegmentIdFrom(kv.getKey());
			return Pair.create(treeId, segId);
		}
	};

	public static long readTreeIdFromBaseKey(byte[] baseKey) {
		ByteBuffer bb = ByteBuffer.wrap(baseKey);
		return bb.getLong(BaseKey.LENGTH);
	}

	public static int readSegmentIdFrom(byte[] key) {
		ByteBuffer bb = ByteBuffer.wrap(key);
		return bb.getInt(LEN_BASEKEY_AND_TREEID);
	}

	public static void fillBaseKey(ByteBuffer keyToFill, BaseKey keyMarker,
			long treeId) {
		keyToFill.put(keyMarker.key);
		keyToFill.putLong(treeId);
	}

	public static byte[] generateBaseKey(BaseKey keyMarker, long treeId) {
		byte[] key = new byte[BaseKey.LENGTH + ByteUtils.SIZEOF_LONG];
		ByteBuffer bb = ByteBuffer.wrap(key);
		fillBaseKey(bb, keyMarker, treeId);
		return key;
	}

	public static byte[] readSegmentDataKey(byte[] dbSegDataKey) {
		int from = LEN_BASEKEY_AND_TREEID + ByteUtils.SIZEOF_INT;
		byte[] key = ByteUtils.copy(dbSegDataKey, from, dbSegDataKey.length);
		return key;
	}

	public static void fillSegmentKey(ByteBuffer keyToFill, BaseKey baseKey,
			long treeId, int id) {
		fillBaseKey(keyToFill, baseKey, treeId);
		keyToFill.putInt(id);
	}

	public static void fillSegmentDataKey(ByteBuffer keyToFill, long treeId,
			int segId) {
		fillSegmentKey(keyToFill, BaseKey.SEG_DATA, treeId, segId);
	}

	public static byte[] generateRebuildMarkerKey(long treeId, int segId) {
		byte[] key = new byte[LEN_BASEKEY_AND_TREEID + ByteUtils.SIZEOF_INT];
		ByteBuffer bb = ByteBuffer.wrap(key);
		fillSegmentKey(bb, BaseKey.REBUILD_MARKER, treeId, segId);
		return key;
	}

	public static byte[] generateSegmentDataKey(long treeId, int segId) {
		byte[] key = new byte[LEN_BASEKEY_AND_TREEID + ByteUtils.SIZEOF_INT];
		ByteBuffer bb = ByteBuffer.wrap(key);
		fillSegmentDataKey(bb, treeId, segId);
		return key;
	}

	public static byte[] generateSegmentDataKey(long treeId, int segId,
			ByteBuffer segDataKey) {
		byte[] key = new byte[LEN_BASEKEY_AND_TREEID + ByteUtils.SIZEOF_INT
				+ segDataKey.array().length];
		ByteBuffer bb = ByteBuffer.wrap(key);
		fillSegmentDataKey(bb, treeId, segId);
		bb.put(segDataKey.array());
		return key;
	}

	public static byte[] generateSegmentDataValue(ByteBuffer digest,
			ByteBuffer actualValue) {
		byte[] value = new byte[digest.array().length
				+ actualValue.array().length];
		ByteBuffer bb = ByteBuffer.wrap(value);
		bb.put(digest.array());
		bb.put(actualValue.array());
		return value;
	}

	public static byte[] generateSegmentHashKey(long treeId, int nodeId) {
		byte[] key = new byte[LEN_BASEKEY_AND_TREEID + ByteUtils.SIZEOF_INT];
		ByteBuffer bb = ByteBuffer.wrap(key);
		fillSegmentKey(bb, BaseKey.SEG_HASH, treeId, nodeId);
		return key;
	}

	public static byte[] generateMetaDataKey(MetaDataKey metaDataKey,
			long treeId) {
		byte[] key = new byte[LEN_BASEKEY_AND_TREEID + metaDataKey.length];
		ByteBuffer bb = ByteBuffer.wrap(key);
		fillBaseKey(bb, BaseKey.META_DATA, treeId);
		bb.put(metaDataKey.key);
		return key;
	}

	public static byte[] generateDirtySegmentKey(long treeId, int segId) {
		byte[] key = new byte[BaseKey.LENGTH + ByteUtils.SIZEOF_LONG
				+ ByteUtils.SIZEOF_INT];
		ByteBuffer bb = ByteBuffer.wrap(key);
		fillSegmentKey(bb, BaseKey.DIRTY_SEG, treeId, segId);
		return key;
	}

	public static byte[] generateTreeIdKey(long treeId) {
		byte[] key = new byte[LEN_BASEKEY_AND_TREEID];
		ByteBuffer bb = ByteBuffer.wrap(key);
		fillBaseKey(bb, BaseKey.TREEID, treeId);
		return key;
	}

	public static byte[] convertServerNameToBytes(ServerName sn) {
		byte[] serverNameInBytes = sn.hostName.getBytes();
		byte[] key = new byte[BaseKey.LENGTH + ByteUtils.SIZEOF_INT
				+ serverNameInBytes.length];
		ByteBuffer bb = ByteBuffer.wrap(key);
		bb.put(BaseKey.SERVER_NAME.key);
		bb.putInt(sn.portNo);
		bb.put(serverNameInBytes);
		return key;
	}

	public static ServerName readServerNameFrom(byte[] key) {
		int offset = BaseKey.LENGTH;
		ByteBuffer bb = ByteBuffer.wrap(key);
		int portNo = bb.getInt(offset);
		offset += ByteUtils.SIZEOF_INT;
		byte[] snInBytes = ByteUtils.copy(key, offset, key.length);
		String hostName = new String(snInBytes);
		return new ServerName(hostName, portNo);
	}

	public static byte[] convertServerNameAndTreeIdToBytes(ServerName rTree,
			long treeId) {
		byte[] keyPrefix = generateMetaDataKey(MetaDataKey.SERVER_NAME, treeId);
		byte[] serverNameInBytes = rTree.hostName.getBytes();
		byte[] key = new byte[keyPrefix.length + ByteUtils.SIZEOF_INT
				+ serverNameInBytes.length];
		ByteBuffer bb = ByteBuffer.wrap(key);
		bb.put(keyPrefix);
		bb.putInt(rTree.portNo);
		bb.put(serverNameInBytes);
		return bb.array();
	}

	public static Pair<ServerName, Long> readServerNameAndTreeIdFrom(byte[] key) {
		long treeId = readTreeIdFromBaseKey(key);
		int offset = LEN_BASEKEY_AND_TREEID + MetaDataKey.SERVER_NAME.length;
		ByteBuffer bb = ByteBuffer.wrap(key);
		int portNo = bb.getInt(offset);
		offset += ByteUtils.SIZEOF_INT;
		byte[] snInBytes = ByteUtils.copy(key, offset, key.length);
		String hostName = new String(snInBytes);
		return Pair.create(new ServerName(hostName, portNo), treeId);
	}
}
