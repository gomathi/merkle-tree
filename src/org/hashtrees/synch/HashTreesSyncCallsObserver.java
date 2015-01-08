package org.hashtrees.synch;

import org.hashtrees.thrift.generated.HashTreesSyncInterface;
import org.hashtrees.thrift.generated.RebuildHashTreeRequest;
import org.hashtrees.thrift.generated.RebuildHashTreeResponse;
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
	 * @param response
	 */
	void onRebuildHashTreeResponse(RebuildHashTreeResponse response);

	/**
	 * This is a call forwarded by {@link HashTreesSyncInterface.Iface} to sync
	 * manager when it receives a request for rebuilding a particular tree id.
	 * 
	 * @param request
	 * @throws Exception
	 */
	void onRebuildHashTreeRequest(RebuildHashTreeRequest request)
			throws Exception;
}
