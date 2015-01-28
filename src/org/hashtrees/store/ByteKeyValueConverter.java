/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.hashtrees.store;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;

import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.util.ByteUtils;

import com.google.common.base.Function;

/**
 * Key and value formats are as following
 * 
 * 1) Meta data info [Like when the tree was built fully last time]. Format is
 * [treeId|'M'|key] -> [value]
 * 
 * 2) SegmentData, format is [treeId|'S'|segId|key] -> [digest]
 * 
 * 3) SegmentHash, format is [treeId|'H'|nodeId] -> [value]
 * 
 * 4) Dirty segment key [treeId|'D'|dirtySegId] -> [EMPTY_VALUE]
 * 
 * 5) Rebuild marker key [treeId|'R'|segId] -> [EMPTY_VALUE]
 * 
 */
public class ByteKeyValueConverter {

	public static final int LEN_BASEKEY_AND_TREEID = BaseKey.LENGTH
			+ ByteUtils.SIZEOF_LONG;

	public static enum BaseKey {

		META_DATA((byte) 'M'), SEG_HASH((byte) 'H'), SEG_DATA((byte) 'S'), DIRTY_SEG(
				(byte) 'D'), REBUILD_MARKER((byte) 'R');

		public static final int LENGTH = 1; // in terms of bytes.
		public final byte key;

		private BaseKey(byte key) {
			this.key = key;
		}
	}

	public static enum MetaDataKey {

		FULL_REBUILT_TS("ltfbTs".getBytes());

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
			int segId = readSegmentIdFrom(kv.getKey());
			return new SegmentData(segId, ByteBuffer.wrap(key),
					ByteBuffer.wrap(kv.getValue()));
		}
	};

	public static Function<Map.Entry<byte[], byte[]>, Integer> KVBYTES_TO_SEGID_CONVERTER = new Function<Map.Entry<byte[], byte[]>, Integer>() {

		@Override
		public Integer apply(Entry<byte[], byte[]> kv) {
			return readSegmentIdFrom(kv.getKey());
		}
	};

	public static int readSegmentIdFrom(byte[] key) {
		ByteBuffer bb = ByteBuffer.wrap(key);
		return bb.getInt(LEN_BASEKEY_AND_TREEID);
	}

	public static void fillBaseKey(ByteBuffer keyToFill, BaseKey keyMarker,
			long treeId) {
		keyToFill.putLong(treeId);
		keyToFill.put(keyMarker.key);
	}

	public static byte[] generateBaseKey(BaseKey keyMarker, long treeId) {
		byte[] key = new byte[LEN_BASEKEY_AND_TREEID];
		ByteBuffer bb = ByteBuffer.wrap(key);
		fillBaseKey(bb, keyMarker, treeId);
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

	public static void fillSegmentKey(ByteBuffer keyToFill, BaseKey baseKey,
			long treeId, int id) {
		fillBaseKey(keyToFill, baseKey, treeId);
		keyToFill.putInt(id);
	}

	public static void fillSegmentDataKey(ByteBuffer keyToFill, long treeId,
			int segId) {
		fillSegmentKey(keyToFill, BaseKey.SEG_DATA, treeId, segId);
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

	public static byte[] readSegmentDataKey(byte[] segDataKey) {
		int from = LEN_BASEKEY_AND_TREEID + ByteUtils.SIZEOF_INT;
		byte[] key = ByteUtils.copy(segDataKey, from, segDataKey.length);
		return key;
	}

	public static byte[] generateSegmentHashKey(long treeId, int nodeId) {
		byte[] key = new byte[LEN_BASEKEY_AND_TREEID + ByteUtils.SIZEOF_INT];
		ByteBuffer bb = ByteBuffer.wrap(key);
		fillSegmentKey(bb, BaseKey.SEG_HASH, treeId, nodeId);
		return key;
	}

	public static byte[] generateDirtySegmentKey(long treeId, int segId) {
		byte[] key = new byte[LEN_BASEKEY_AND_TREEID + ByteUtils.SIZEOF_INT];
		ByteBuffer bb = ByteBuffer.wrap(key);
		fillSegmentKey(bb, BaseKey.DIRTY_SEG, treeId, segId);
		return key;
	}

	public static byte[] generateRebuildMarkerKey(long treeId, int segId) {
		byte[] key = new byte[LEN_BASEKEY_AND_TREEID + ByteUtils.SIZEOF_INT];
		ByteBuffer bb = ByteBuffer.wrap(key);
		fillSegmentKey(bb, BaseKey.REBUILD_MARKER, treeId, segId);
		return key;
	}
}
