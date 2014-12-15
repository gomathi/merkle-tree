package org.hashtrees.synch;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.hashtrees.HashTrees;
import org.hashtrees.thrift.generated.HashTreeSyncInterface;
import org.hashtrees.thrift.generated.ServerName;

/**
 * A hashtree client which talks to a hashtree server in another node through
 * thrift protocol.
 * 
 */

public class HashTreesThriftClientProvider {

	public static HashTreeSyncInterface.Iface getThriftHashTreeClient(
			ServerName sn) throws TTransportException {
		TTransport transport = new TSocket(sn.getHostName(), sn.getPortNo());
		transport.open();
		TProtocol protocol = new TBinaryProtocol(transport);
		return new HashTreeSyncInterface.Client(protocol);
	}

	public static HashTrees getHashTreeRemoteClient(ServerName sn)
			throws TTransportException {
		return new HashTreesRemoteClient(getThriftHashTreeClient(sn));
	}
}
