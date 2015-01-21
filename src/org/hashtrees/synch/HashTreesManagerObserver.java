package org.hashtrees.synch;

import org.hashtrees.thrift.generated.ServerName;

public interface HashTreesManagerObserver {

	void preRebuild(long treeId);

	void postRebuild(long treeId);

	void preSync(long treeId, ServerName remoteServerName);

	void postSync(long treeId, ServerName remoteServerName);

}
