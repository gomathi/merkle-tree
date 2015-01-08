package org.hashtrees.synch;

import org.hashtrees.thrift.generated.HashTreesSyncInterface;
import org.hashtrees.thrift.generated.ServerName;

public interface HashTreesSyncCallsObserver {

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
	 * This is a call forwarded by {@link HashTreesSyncInterface.Iface} to sync
	 * manager when it receives a response for the previous rebuild request.
	 * 
	 * @param sn
	 *            , server which has sent this notification.
	 * @param treeId
	 *            , hash tree id for which the notification is sent
	 * 
	 * @param tokenNo
	 *            , a unique identifier that should match previous identifier
	 *            which was sent by the primary to secondary.
	 */
	void onRebuildHashTreeResponse(ServerName sn, long treeId, long tokenNo);

	/**
	 * This is a call forwarded by {@link HashTreesSyncInterface.Iface} to sync
	 * manager when it receives a request for rebuilding a particular tree id.
	 * 
	 * @param sn
	 * @param treeId
	 * @param tokenNo
	 * @param expFullRebuildTimeInt
	 * @throws Exception
	 */
	void onRebuildHashTreeRequest(ServerName sn, long treeId, long tokenNo,
			long expFullRebuildTimeInt) throws Exception;

}
