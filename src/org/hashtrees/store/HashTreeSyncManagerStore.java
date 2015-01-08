package org.hashtrees.store;

import java.util.List;

import org.hashtrees.synch.HashTreesSyncCallsObserver;
import org.hashtrees.thrift.generated.ServerName;

/**
 * Used by {@link HashTreesSyncCallsObserver} to persist information about which
 * remote hash trees to be synced.
 * 
 */
public interface HashTreeSyncManagerStore {

	/**
	 * Adds server to sync list. Hashtrees on the local server will be synched
	 * against the remote server.
	 * 
	 * @param sn
	 */
	void addServerToSyncList(ServerName sn);

	/**
	 * Removes a server from sync list. From the next iteration, the remote
	 * server will not be synched by the local server.
	 * 
	 * @param sn
	 */
	void removeServerFromSyncList(ServerName sn);

	/**
	 * Returns all the servers that are to be synced.
	 * 
	 * @return
	 */
	List<ServerName> getAllServers();

}
