package org.hashtrees.manager;

import org.hashtrees.SyncDiffResult;
import org.hashtrees.thrift.generated.ServerName;

public interface HashTreesManagerObserver {

	void preSync(long treeId, ServerName remoteServerName);

	void postSync(long treeId, ServerName remoteServerName,
			SyncDiffResult result);
}
