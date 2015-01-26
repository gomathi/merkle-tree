package org.hashtrees.manager;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.hashtrees.thrift.generated.HashTreesSyncInterface;
import org.hashtrees.thrift.generated.HashTreesSyncInterface.Client;
import org.hashtrees.thrift.generated.ServerName;

public class HashTreesClientPool extends
		GenericObjectPool<HashTreesSyncInterface.Client> {

	private static final GenericObjectPoolConfig POOL_CONFIG;

	static {
		POOL_CONFIG = new GenericObjectPoolConfig();
		POOL_CONFIG.setTestOnReturn(true);
	}

	public HashTreesClientPool(
			PooledObjectFactory<HashTreesSyncInterface.Client> factory) {
		super(factory, POOL_CONFIG);
	}

	public static HashTreesClientPool getThriftClientPool(ServerName sn) {
		return new HashTreesClientPool(new PooledObjectFactoryProvider(sn));
	}

	private static class PooledObjectFactoryProvider extends
			BasePooledObjectFactory<HashTreesSyncInterface.Client> {

		private final ServerName sn;

		public PooledObjectFactoryProvider(ServerName sn) {
			this.sn = sn;
		}

		@Override
		public void destroyObject(PooledObject<Client> pClient)
				throws Exception {
			Client client = pClient.getObject();
			if (client != null) {
				client.getInputProtocol().getTransport().close();
				client.getOutputProtocol().getTransport().close();
			}
		}

		@Override
		public boolean validateObject(PooledObject<Client> pClient) {
			try {
				pClient.getObject().ping();
				return true;
			} catch (TException e) {
				return false;
			}
		}

		@Override
		public Client create() throws Exception {
			TTransport transport = new TSocket(sn.getHostName(), sn.getPortNo());
			transport.open();
			TProtocol protocol = new TBinaryProtocol(transport);
			return new HashTreesSyncInterface.Client(protocol);
		}

		@Override
		public PooledObject<Client> wrap(Client client) {
			return new DefaultPooledObject<HashTreesSyncInterface.Client>(
					client);
		}
	}
}
