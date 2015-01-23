package org.hashtrees.synch;

import java.io.IOException;

import org.hashtrees.thrift.generated.HashTreesSyncInterface;
import org.hashtrees.thrift.generated.RebuildHashTreeRequest;
import org.hashtrees.thrift.generated.RebuildHashTreeResponse;

public interface HashTreesSyncCallsObserver {

	/**
	 * This is a call forwarded by {@link HashTreesSyncInterface.Iface} to sync
	 * manager when it receives a request for rebuilding a particular tree id.
	 * 
	 * @param request
	 * @throws IOException
	 */
	void onRebuildHashTreeRequest(RebuildHashTreeRequest request)
			throws IOException;

	/**
	 * This is a call forwarded by {@link HashTreesSyncInterface.Iface} to sync
	 * manager when it receives a response for the previous rebuild request.
	 * 
	 * @param response
	 */
	void onRebuildHashTreeResponse(RebuildHashTreeResponse response);

}
