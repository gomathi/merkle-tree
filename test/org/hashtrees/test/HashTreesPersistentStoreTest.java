package org.hashtrees.test;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.hashtrees.store.HashTreesPersistentStore;
import org.hashtrees.thrift.generated.SegmentData;
import org.hashtrees.thrift.generated.SegmentHash;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HashTreesPersistentStoreTest {

	private static final int DEF_TREE_ID = 1;
	private static final int DEF_SEG_ID = 0;
	private static String dbDir;
	private static HashTreesPersistentStore dbObj;
	private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
	private static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer
			.wrap(EMPTY_BYTE_ARRAY);

	@Before
	public void init() throws Exception {
		dbDir = "/tmp/random" + new Random().nextInt();
		dbObj = new HashTreesPersistentStore(dbDir);
	}

	@After
	public void deleteDBDir() {
		if (dbObj != null)
			dbObj.close();
		File dbDirObj = new File(dbDir);
		if (dbDirObj.exists())
			FileUtils.deleteQuietly(dbDirObj);
	}

	@Test
	public void testSegmentData() {
		ByteBuffer key = ByteBuffer.wrap("key1".getBytes());
		ByteBuffer digest = ByteBuffer.wrap("digest1".getBytes());

		dbObj.putSegmentData(DEF_TREE_ID, DEF_SEG_ID, key, digest);

		SegmentData sd = dbObj.getSegmentData(DEF_TREE_ID, DEF_SEG_ID, key);
		Assert.assertNotNull(sd);
		Assert.assertEquals(digest, sd.digest);

		dbObj.deleteSegmentData(DEF_TREE_ID, DEF_SEG_ID, key);
		sd = dbObj.getSegmentData(DEF_TREE_ID, DEF_SEG_ID, key);
		Assert.assertNull(sd);

		dbObj.deleteTree(DEF_TREE_ID);
	}

	@Test
	public void testSegment() {
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

		dbObj.deleteTree(DEF_TREE_ID);
	}

	@Test
	public void testPutSegmentHash() {
		ByteBuffer digest = ByteBuffer.wrap("digest1".getBytes());
		dbObj.putSegmentHash(DEF_TREE_ID, DEF_SEG_ID, digest);

		SegmentHash sh = dbObj.getSegmentHash(DEF_TREE_ID, DEF_SEG_ID);
		Assert.assertNotNull(sh);
		Assert.assertEquals(digest, sh.hash);

		List<SegmentHash> expected = new ArrayList<SegmentHash>();
		expected.add(sh);

		List<Integer> nodeIds = new ArrayList<Integer>();
		nodeIds.add(DEF_SEG_ID);

		List<SegmentHash> actual = dbObj.getSegmentHashes(DEF_TREE_ID, nodeIds);
		Assert.assertNotNull(actual);

		Assert.assertEquals(expected, actual);
		dbObj.deleteTree(DEF_TREE_ID);
	}

	@Test
	public void testDeleteTree() {
		ByteBuffer key = ByteBuffer.wrap("key1".getBytes());
		ByteBuffer digest = ByteBuffer.wrap("digest1".getBytes());

		dbObj.putSegmentData(DEF_TREE_ID, DEF_SEG_ID, key, digest);
		dbObj.deleteTree(DEF_TREE_ID);

		SegmentData sd = dbObj.getSegmentData(DEF_TREE_ID, DEF_SEG_ID, key);
		Assert.assertNull(sd);
	}

	@Test
	public void testSetLastFullyTreeBuiltTimestamp() {
		long exTs = System.currentTimeMillis();
		dbObj.setLastFullyTreeBuiltTimestamp(DEF_TREE_ID, exTs);
		long dbTs = dbObj.getLastFullyTreeBuiltTimestamp(DEF_TREE_ID);
		Assert.assertEquals(exTs, dbTs);
	}

	@Test
	public void testGetAllTreeIds() {
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
		for (long treeId = 1; treeId <= 20; treeId++)
			dbObj.deleteTree(treeId);
	}

}
