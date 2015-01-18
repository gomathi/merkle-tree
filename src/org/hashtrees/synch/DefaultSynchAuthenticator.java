package org.hashtrees.synch;

import org.hashtrees.thrift.generated.ServerName;

public class DefaultSynchAuthenticator implements HashTreesSynchAuthenticator {

	@Override
	public boolean canSynch(ServerName localNodeToSynch,
			ServerName remoteNodeToSynch) {
		return true;
	}

}
