package org.hashtrees.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.hashtrees.store.HashTreesManagerStore;
import org.hashtrees.test.utils.HashTreesImplTestUtils;
import org.hashtrees.thrift.generated.RemoteTreeInfo;
import org.hashtrees.thrift.generated.ServerName;
import org.junit.Test;

public class HashTreesManagerStoreTest {

	@Test
	public void testAddServer() throws Exception {
		HashTreesManagerStore[] syncMgrStores = HashTreesImplTestUtils
				.generateInMemoryAndPersistentSyncMgrStores();
		try {
			for (HashTreesManagerStore syncMgrStore : syncMgrStores) {

				List<RemoteTreeInfo> expected = new ArrayList<>();
				for (int i = 0; i < 10; i++)
					expected.add(new RemoteTreeInfo(new ServerName(
							HashTreesImplTestUtils.randomDirName(), 6000), i));

				for (RemoteTreeInfo rTree : expected)
					syncMgrStore.addToSyncList(rTree);

				List<RemoteTreeInfo> actual = new ArrayList<>();
				for (int i = 0; i < 10; i++)
					actual.addAll(syncMgrStore.getSyncList(i));
				Assert.assertNotNull(actual);
				Assert.assertEquals(expected.size(), actual.size());
				Collections.sort(actual);
				Collections.sort(expected);
				Assert.assertEquals(expected, actual);

				for (RemoteTreeInfo sn : expected)
					syncMgrStore.removeFromSyncList(sn);
				actual = new ArrayList<>();
				for (int i = 0; i < 10; i++)
					actual.addAll(syncMgrStore.getSyncList(i));
				Assert.assertNotNull(actual);
				Assert.assertEquals(0, actual.size());
			}
		} finally {
			HashTreesImplTestUtils.closeStores(syncMgrStores);
		}
	}
}
