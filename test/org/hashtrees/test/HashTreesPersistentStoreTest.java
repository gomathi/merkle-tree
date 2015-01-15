package org.hashtrees.test;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.hashtrees.store.HashTreesPersistentStore;
import org.hashtrees.test.utils.HashTreesImplTestUtils;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;
import org.hashtrees.util.ByteUtils;
import org.junit.Assert;
import org.junit.Test;

public class HashTreesPersistentStoreTest {

	private static final int DEF_TREE_ID = 1;
	private static final int DEF_SEG_ID = 0;
	private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	private static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer
			.wrap(EMPTY_BYTE_ARRAY);

	@Test
	public void testSegmentData() throws Exception {
		String dbDir = HashTreesImplTestUtils.randomDirName();
		HashTreesPersistentStore dbObj = new HashTreesPersistentStore(dbDir);
		try {
			ByteBuffer key = ByteBuffer.wrap("key1".getBytes());
			ByteBuffer digest = ByteBuffer.wrap(ByteUtils.sha1("digest1"
					.getBytes()));

			dbObj.putSegmentData(DEF_TREE_ID, DEF_SEG_ID, key, digest);

			SegmentData sd = dbObj.getSegmentData(DEF_TREE_ID, DEF_SEG_ID, key);
			Assert.assertNotNull(sd);
			Assert.assertEquals(digest, sd.digest);

			dbObj.deleteSegmentData(DEF_TREE_ID, DEF_SEG_ID, key);
			sd = dbObj.getSegmentData(DEF_TREE_ID, DEF_SEG_ID, key);
			Assert.assertNull(sd);
		} finally {
			dbObj.delete();
		}
	}

	@Test
	public void testSegment() throws Exception {
		String dbDir = HashTreesImplTestUtils.randomDirName();
		HashTreesPersistentStore dbObj = new HashTreesPersistentStore(dbDir);
		try {
			List<SegmentData> list = new ArrayList<SegmentData>();
			SegmentData sd;
			for (int i = 0; i < 10; i++) {
				sd = new SegmentData(ByteBuffer.wrap(("test" + i).getBytes()),
						ByteBuffer.wrap(("value" + i).getBytes()));
				list.add(sd);
				dbObj.putSegmentData(DEF_TREE_ID, DEF_SEG_ID, sd.key, sd.digest);
			}

			List<SegmentData> actualResult = dbObj.getSegment(DEF_TREE_ID,
					DEF_SEG_ID);
			Assert.assertNotNull(actualResult);
			Assert.assertTrue(actualResult.size() != 0);
			Assert.assertEquals(list, actualResult);
		} finally {
			dbObj.delete();
		}
	}

	@Test
	public void testPutSegmentHash() throws Exception {
		String dbDir = HashTreesImplTestUtils.randomDirName();
		HashTreesPersistentStore dbObj = new HashTreesPersistentStore(dbDir);

		try {
			ByteBuffer digest = ByteBuffer.wrap("digest1".getBytes());
			dbObj.putSegmentHash(DEF_TREE_ID, DEF_SEG_ID, digest);

			SegmentHash sh = dbObj.getSegmentHash(DEF_TREE_ID, DEF_SEG_ID);
			Assert.assertNotNull(sh);
			Assert.assertEquals(digest, sh.hash);

			List<SegmentHash> expected = new ArrayList<SegmentHash>();
			expected.add(sh);

			List<Integer> nodeIds = new ArrayList<Integer>();
			nodeIds.add(DEF_SEG_ID);

			List<SegmentHash> actual = dbObj.getSegmentHashes(DEF_TREE_ID,
					nodeIds);
			Assert.assertNotNull(actual);
			Assert.assertEquals(expected, actual);
		} finally {
			dbObj.delete();
		}
	}

	@Test
	public void testDeleteTree() throws Exception {
		String dbDir = HashTreesImplTestUtils.randomDirName();
		HashTreesPersistentStore dbObj = new HashTreesPersistentStore(dbDir);
		try {
			ByteBuffer key = ByteBuffer.wrap("key1".getBytes());
			ByteBuffer digest = ByteBuffer.wrap("digest1".getBytes());

			dbObj.putSegmentData(DEF_TREE_ID, DEF_SEG_ID, key, digest);
			dbObj.deleteTree(DEF_TREE_ID);

			SegmentData sd = dbObj.getSegmentData(DEF_TREE_ID, DEF_SEG_ID, key);
			Assert.assertNull(sd);
		} finally {
			dbObj.delete();
		}
	}

	@Test
	public void testSetLastFullyTreeBuiltTimestamp() throws Exception {
		String dbDir = HashTreesImplTestUtils.randomDirName();
		HashTreesPersistentStore dbObj = new HashTreesPersistentStore(dbDir);

		try {
			long exTs = System.currentTimeMillis();
			dbObj.setCompleteRebuiltTimestamp(DEF_TREE_ID, exTs);
			long dbTs = dbObj.getCompleteRebuiltTimestamp(DEF_TREE_ID);
			Assert.assertEquals(exTs, dbTs);
		} finally {
			dbObj.delete();
		}
	}

	@Test
	public void testGetAllTreeIds() throws Exception {
		String dbDir = HashTreesImplTestUtils.randomDirName();
		HashTreesPersistentStore dbObj = new HashTreesPersistentStore(dbDir);

		try {
			int totTreeIdsCounter = 20;
			for (long treeId = 1; treeId <= totTreeIdsCounter; treeId++)
				dbObj.putSegmentData(treeId, DEF_SEG_ID, EMPTY_BYTE_BUFFER,
						EMPTY_BYTE_BUFFER);
			Iterator<Long> treeIdItr = dbObj.getAllTreeIds();
			int actualTreeIdCount = 0;
			while (treeIdItr.hasNext()) {
				treeIdItr.next();
				actualTreeIdCount++;
			}

			Assert.assertEquals(totTreeIdsCounter, actualTreeIdCount);
		} finally {
			dbObj.delete();
		}
	}

	@Test
	public void testSegmentRebuildMarkers() throws Exception {
		String dbDir = HashTreesImplTestUtils.randomDirName();
		HashTreesPersistentStore dbObj = new HashTreesPersistentStore(dbDir);

		try {
			List<Integer> expectedSegs = new ArrayList<>();
			for (int i = 0; i < 10; i++)
				expectedSegs.add(i);
			dbObj.markSegments(DEF_TREE_ID, expectedSegs);
			List<Integer> actualMarkedSegs = dbObj
					.getMarkedSegments(DEF_TREE_ID);
			Assert.assertNotNull(actualMarkedSegs);
			Assert.assertEquals(10, actualMarkedSegs.size());
			Collections.sort(actualMarkedSegs);
			Assert.assertEquals(expectedSegs, actualMarkedSegs);

			dbObj.unmarkSegments(DEF_TREE_ID, expectedSegs);
			actualMarkedSegs = dbObj.getMarkedSegments(DEF_TREE_ID);
			Assert.assertNotNull(actualMarkedSegs);
			Assert.assertEquals(0, actualMarkedSegs.size());
		} finally {
			dbObj.delete();
		}
	}

	@Test
	public void testDirtySegmentsPersistenceBetweenRestarts() throws Exception {
		String dbDir = HashTreesImplTestUtils.randomDirName();
		HashTreesPersistentStore dbObj = new HashTreesPersistentStore(dbDir);

		try {
			dbObj.setDirtySegment(DEF_TREE_ID, DEF_SEG_ID);
			dbObj.stop();
			dbObj = new HashTreesPersistentStore(dbDir);
			List<Integer> dirtySegments = dbObj.getDirtySegments(DEF_TREE_ID);
			Assert.assertNotNull(dirtySegments);
			Assert.assertEquals(1, dirtySegments.size());
			Assert.assertEquals(DEF_SEG_ID, dirtySegments.get(0).intValue());
		} finally {
			dbObj.delete();
		}
	}

}
