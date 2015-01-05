package org.hashtrees.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.hashtrees.store.HashTreeSyncManagerStore;
import org.hashtrees.thrift.generated.ServerName;
import org.junit.Test;

public class HashTreeSyncManagerStoreTest {

	@Test
	public void testAddServer() throws Exception {
		HashTreeSyncManagerStore[] syncMgrStores = HashTreesImplTestUtils
				.generateInMemoryAndPersistentSyncMgrStores();
		try {
			for (HashTreeSyncManagerStore syncMgrStore : syncMgrStores) {

				List<ServerName> expected = new ArrayList<>();
				for (int i = 0; i < 10; i++)
					expected.add(new ServerName(HashTreesImplTestUtils
							.randomDirName(), 6000));

				for (ServerName sn : expected)
					syncMgrStore.addServerToSyncList(sn);

				List<ServerName> actual = syncMgrStore.getAllServers();
				Assert.assertNotNull(actual);
				Assert.assertEquals(expected.size(), actual.size());
				Collections.sort(actual);
				Collections.sort(expected);
				Assert.assertEquals(expected, actual);

				for (ServerName sn : expected)
					syncMgrStore.removeServerFromSyncList(sn);
				actual = syncMgrStore.getAllServers();
				Assert.assertNotNull(actual);
				Assert.assertEquals(0, actual.size());
			}
		} finally {
			HashTreesImplTestUtils.closeStores(syncMgrStores);
		}
	}
}
