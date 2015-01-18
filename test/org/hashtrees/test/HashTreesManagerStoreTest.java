package org.hashtrees.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.hashtrees.store.HashTreesManagerStore;
import org.hashtrees.test.utils.HashTreesImplTestUtils;
import org.hashtrees.thrift.generated.ServerName;
import org.junit.Test;

public class HashTreesManagerStoreTest {

	@Test
	public void testAddServerNames() throws Exception {
		HashTreesManagerStore[] syncMgrStores = HashTreesImplTestUtils
				.generateInMemoryAndPersistentSyncMgrStores();
		try {
			for (HashTreesManagerStore syncMgrStore : syncMgrStores) {
				List<ServerName> expected = new ArrayList<>();
				for (int i = 0; i < 10; i++) {
					ServerName sn = new ServerName("test" + i, i);
					syncMgrStore.addServerNameToSyncList(sn);
					expected.add(sn);
				}
				List<ServerName> actual = syncMgrStore.getServerNameList();
				Assert.assertNotNull(actual);
				Collections.sort(expected);
				Collections.sort(actual);
				Assert.assertEquals(expected, actual);

				for (ServerName sn : expected)
					syncMgrStore.removeServerNameFromSyncList(sn);
				actual = syncMgrStore.getServerNameList();
				Assert.assertNotNull(actual);
				Assert.assertTrue(actual.isEmpty());
			}
		} finally {
			HashTreesImplTestUtils.closeStores(syncMgrStores);
		}
	}

	@Test
	public void testAddServerNameAndTreeId() throws Exception {
		HashTreesManagerStore[] syncMgrStores = HashTreesImplTestUtils
				.generateInMemoryAndPersistentSyncMgrStores();
		long treeId = 1;
		try {
			for (HashTreesManagerStore syncMgrStore : syncMgrStores) {
				List<ServerName> expected = new ArrayList<>();
				for (int i = 0; i < 10; i++) {
					ServerName sn = new ServerName("test" + i, i);
					syncMgrStore.addServerNameAndTreeIdToSyncList(sn, treeId);
					expected.add(sn);
				}
				List<ServerName> actual = syncMgrStore
						.getServerNameListFor(treeId);
				Assert.assertNotNull(actual);
				Collections.sort(expected);
				Collections.sort(actual);
				Assert.assertEquals(expected, actual);

				for (ServerName sn : expected)
					syncMgrStore.removeServerNameAndTreeIdFromSyncList(sn,
							treeId);
				actual = syncMgrStore.getServerNameListFor(treeId);
				Assert.assertNotNull(actual);
				Assert.assertTrue(actual.isEmpty());
			}
		} finally {
			HashTreesImplTestUtils.closeStores(syncMgrStores);
		}
	}
}
