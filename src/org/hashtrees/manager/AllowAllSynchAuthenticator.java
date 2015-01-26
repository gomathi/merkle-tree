package org.hashtrees.manager;

import org.hashtrees.thrift.generated.ServerName;

public class AllowAllSynchAuthenticator implements HashTreesSynchAuthenticator {

	@Override
	public boolean canSynch(ServerName localNodeToSynch,
			ServerName remoteNodeToSynch) {
		return true;
	}

}
