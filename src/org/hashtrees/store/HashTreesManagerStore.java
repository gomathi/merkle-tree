package org.hashtrees.store;

import java.util.List;

import org.hashtrees.synch.HashTreesManager;
import org.hashtrees.thrift.generated.ServerName;

/**
 * Used by {@link HashTreesManager} to persist information about which remote
 * hash trees to be synced.
 * 
 */
public interface HashTreesManagerStore {

	/**
	 * Adds a server to sync list. Hashtree with treeId on the local server will
	 * only be synched against this server.
	 * 
	 * 
	 * @param sn
	 * @param treeId
	 */
	public void addServerNameAndTreeIdToSyncList(ServerName sn, long treeId);

	/**
	 * Removes a server from sync list. From the next iteration, the remote
	 * server will not be synched by the local server for only the given treeId.
	 * 
	 * 
	 * @param sn
	 * @param treeId
	 */
	public void removeServerNameAndTreeIdFromSyncList(ServerName sn, long treeId);

	/**
	 * Returns servers which are in sync list for the given treeId. The result
	 * will contain servers which are added through addServerToSyncList(treeId,
	 * sn) and removeServerFromSyncList(sn).
	 * 
	 * 
	 * 
	 * @param treeId
	 */
	public List<ServerName> getServerNameListFor(long treeId);

	/**
	 * All local hashtrees will be synced against this server from the next
	 * iteration.
	 * 
	 * 
	 * @param sn
	 */
	public void addServerNameToSyncList(ServerName sn);

	public void removeServerNameFromSyncList(ServerName sn);

	public List<ServerName> getServerNameList();
}
