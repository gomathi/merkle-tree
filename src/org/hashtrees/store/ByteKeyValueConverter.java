package org.hashtrees.store;

import java.nio.ByteBuffer;

import org.hashtrees.thrift.generated.RemoteTreeInfo;
import org.hashtrees.thrift.generated.ServerName;
import org.hashtrees.util.ByteUtils;

public class ByteKeyValueConverter {

	public static final int LEN_BASEKEY_AND_TREEID = BaseKey.LENGTH
			+ ByteUtils.SIZEOF_LONG;

	public static enum BaseKey {

		META_DATA((byte) 'M'), SEG_HASH((byte) 'H'), SEG_DATA((byte) 'S'), TREEID(
				(byte) 'T'), DIRTY_SEG((byte) 'D'), REBUILD_MARKER((byte) 'R');

		public static final int LENGTH = 1; // in terms of bytes.
		public final byte key;

		private BaseKey(byte key) {
			this.key = key;
		}
	}

	public static enum MetaDataKey {

		LAST_FULLY_TREE_BUILT_TS("ltfbTs".getBytes()), SERVER_NAME("sn"
				.getBytes());

		public final byte[] key;
		public final int length;

		private MetaDataKey(byte[] key) {
			this.key = key;
			this.length = key.length;
		}
	}

	public static long readTreeIdFromBaseKey(byte[] baseKey) {
		ByteBuffer bb = ByteBuffer.wrap(baseKey);
		return bb.getLong(BaseKey.LENGTH);
	}

	public static void fillBaseKey(ByteBuffer keyToFill, BaseKey keyMarker,
			long treeId) {
		keyToFill.put(keyMarker.key);
		keyToFill.putLong(treeId);
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

	public static byte[] convertRemoteTreeInfoToBytes(RemoteTreeInfo rTree) {
		byte[] keyPrefix = generateMetaDataKey(MetaDataKey.SERVER_NAME,
				rTree.treeId);
		byte[] serverNameInBytes = rTree.sn.hostName.getBytes();
		byte[] key = new byte[keyPrefix.length + ByteUtils.SIZEOF_INT
				+ serverNameInBytes.length];
		ByteBuffer bb = ByteBuffer.wrap(key);
		bb.put(keyPrefix);
		bb.putInt(rTree.sn.portNo);
		bb.put(serverNameInBytes);
		return bb.array();
	}

	public static RemoteTreeInfo readRemoteTreeInfoFrom(byte[] key) {
		long treeId = readTreeIdFromBaseKey(key);
		int offset = LEN_BASEKEY_AND_TREEID + MetaDataKey.SERVER_NAME.length;
		ByteBuffer bb = ByteBuffer.wrap(key);
		int portNo = bb.getInt(offset);
		offset += ByteUtils.SIZEOF_INT;
		byte[] snInBytes = ByteUtils.copy(key, offset, key.length);
		String hostName = new String(snInBytes);
		return new RemoteTreeInfo(new ServerName(hostName, portNo), treeId);
	}
}
