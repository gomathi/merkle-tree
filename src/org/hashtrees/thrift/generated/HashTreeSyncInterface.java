package org.hashtrees.thrift.generated;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;
import org.apache.thrift.scheme.TupleScheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HashTreeSyncInterface {

	public interface Iface {

		public String ping() throws org.apache.thrift.TException;

		/**
		 * Adds the (key,value) pair to the original storage. Intended to be
		 * used while synch operation.
		 * 
		 * @param keyValuePairs
		 * 
		 * @param keyValuePairs
		 */
		public void sPut(Map<ByteBuffer, ByteBuffer> keyValuePairs)
				throws org.apache.thrift.TException;

		/**
		 * Deletes the keys from the storage. While synching this function is
		 * used.
		 * 
		 * @param keys
		 * 
		 * @param keys
		 */
		public void sRemove(List<ByteBuffer> keys)
				throws org.apache.thrift.TException;

		/**
		 * Hash tree internal nodes store the hash of their children nodes.
		 * Given a set of internal node ids, this returns the hashes that are
		 * stored on the internal node.
		 * 
		 * @param treeId
		 * @param nodeIds
		 *            , internal tree node ids.
		 * @return
		 * 
		 * @param treeId
		 * @param nodeIds
		 */
		public List<SegmentHash> getSegmentHashes(long treeId,
				List<Integer> nodeIds) throws org.apache.thrift.TException;

		/**
		 * Returns the segment hash that is stored on the tree.
		 * 
		 * @param treeId
		 *            , hash tree id.
		 * @param nodeId
		 *            , node id
		 * @return
		 * 
		 * @param treeId
		 * @param nodeId
		 */
		public SegmentHash getSegmentHash(long treeId, int nodeId)
				throws org.apache.thrift.TException;

		/**
		 * Hash tree data is stored on the leaf blocks. Given a segment id this
		 * method is supposed to return (key,hash) pairs.
		 * 
		 * @param treeId
		 * @param segId
		 *            , id of the segment block.
		 * @return
		 * 
		 * @param treeId
		 * @param segId
		 */
		public List<SegmentData> getSegment(long treeId, int segId)
				throws org.apache.thrift.TException;

		/**
		 * Returns the (key,digest) for the given key in the given segment.
		 * 
		 * 
		 * @param treeId
		 * @param segId
		 * @param key
		 */
		public SegmentData getSegmentData(long treeId, int segId, ByteBuffer key)
				throws org.apache.thrift.TException;

		/**
		 * Deletes tree nodes from the hash tree, and the corresponding
		 * segments.
		 * 
		 * 
		 * @param treeId
		 * @param nodeIds
		 */
		public void deleteTreeNodes(long treeId, List<Integer> nodeIds)
				throws org.apache.thrift.TException;

		/**
		 * Requests a rebuild of the hash tree on the remote node.
		 * 
		 * @param sn
		 *            , servername which requests for the rebuild
		 * @param treeId
		 * @param tokenNo
		 *            a unique tokenNo to differentiate similar requests.
		 * @param expFullRebuildTimeInt
		 *            , if the remote tree is not fully rebuilt within this
		 *            interval, then remote tree is expected to do a full
		 *            rebuild, otherwise just dirty segments rebuild.
		 * 
		 * @param sn
		 * @param treeId
		 * @param tokenNo
		 * @param expFullRebuildTimeInt
		 */
		public void rebuildHashTree(ServerName sn, long treeId, long tokenNo,
				long expFullRebuildTimeInt) throws org.apache.thrift.TException;

		/**
		 * This method posts a response on completion of the rebuild of the hash
		 * tree.
		 * 
		 * @param sn
		 *            , the server which posts the response
		 * @param treeId
		 *            ,
		 * @param tokenNo
		 *            which was passed in the request for rebuild.
		 * 
		 * @param sn
		 * @param treeId
		 * @param tokenNo
		 */
		public void postRebuildHashTreeResponse(ServerName sn, long treeId,
				long tokenNo) throws org.apache.thrift.TException;

	}

	public interface AsyncIface {

		public void ping(
				org.apache.thrift.async.AsyncMethodCallback<AsyncClient.ping_call> resultHandler)
				throws org.apache.thrift.TException;

		public void sPut(
				Map<ByteBuffer, ByteBuffer> keyValuePairs,
				org.apache.thrift.async.AsyncMethodCallback<AsyncClient.sPut_call> resultHandler)
				throws org.apache.thrift.TException;

		public void sRemove(
				List<ByteBuffer> keys,
				org.apache.thrift.async.AsyncMethodCallback<AsyncClient.sRemove_call> resultHandler)
				throws org.apache.thrift.TException;

		public void getSegmentHashes(
				long treeId,
				List<Integer> nodeIds,
				org.apache.thrift.async.AsyncMethodCallback<AsyncClient.getSegmentHashes_call> resultHandler)
				throws org.apache.thrift.TException;

		public void getSegmentHash(
				long treeId,
				int nodeId,
				org.apache.thrift.async.AsyncMethodCallback<AsyncClient.getSegmentHash_call> resultHandler)
				throws org.apache.thrift.TException;

		public void getSegment(
				long treeId,
				int segId,
				org.apache.thrift.async.AsyncMethodCallback<AsyncClient.getSegment_call> resultHandler)
				throws org.apache.thrift.TException;

		public void getSegmentData(
				long treeId,
				int segId,
				ByteBuffer key,
				org.apache.thrift.async.AsyncMethodCallback<AsyncClient.getSegmentData_call> resultHandler)
				throws org.apache.thrift.TException;

		public void deleteTreeNodes(
				long treeId,
				List<Integer> nodeIds,
				org.apache.thrift.async.AsyncMethodCallback<AsyncClient.deleteTreeNodes_call> resultHandler)
				throws org.apache.thrift.TException;

		public void rebuildHashTree(
				ServerName sn,
				long treeId,
				long tokenNo,
				long expFullRebuildTimeInt,
				org.apache.thrift.async.AsyncMethodCallback<AsyncClient.rebuildHashTree_call> resultHandler)
				throws org.apache.thrift.TException;

		public void postRebuildHashTreeResponse(
				ServerName sn,
				long treeId,
				long tokenNo,
				org.apache.thrift.async.AsyncMethodCallback<AsyncClient.postRebuildHashTreeResponse_call> resultHandler)
				throws org.apache.thrift.TException;

	}

	public static class Client extends org.apache.thrift.TServiceClient
			implements Iface {
		public static class Factory implements
				org.apache.thrift.TServiceClientFactory<Client> {
			public Factory() {
			}

			public Client getClient(org.apache.thrift.protocol.TProtocol prot) {
				return new Client(prot);
			}

			public Client getClient(org.apache.thrift.protocol.TProtocol iprot,
					org.apache.thrift.protocol.TProtocol oprot) {
				return new Client(iprot, oprot);
			}
		}

		public Client(org.apache.thrift.protocol.TProtocol prot) {
			super(prot, prot);
		}

		public Client(org.apache.thrift.protocol.TProtocol iprot,
				org.apache.thrift.protocol.TProtocol oprot) {
			super(iprot, oprot);
		}

		public String ping() throws org.apache.thrift.TException {
			send_ping();
			return recv_ping();
		}

		public void send_ping() throws org.apache.thrift.TException {
			ping_args args = new ping_args();
			sendBase("ping", args);
		}

		public String recv_ping() throws org.apache.thrift.TException {
			ping_result result = new ping_result();
			receiveBase(result, "ping");
			if (result.isSetSuccess()) {
				return result.success;
			}
			throw new org.apache.thrift.TApplicationException(
					org.apache.thrift.TApplicationException.MISSING_RESULT,
					"ping failed: unknown result");
		}

		public void sPut(Map<ByteBuffer, ByteBuffer> keyValuePairs)
				throws org.apache.thrift.TException {
			send_sPut(keyValuePairs);
			recv_sPut();
		}

		public void send_sPut(Map<ByteBuffer, ByteBuffer> keyValuePairs)
				throws org.apache.thrift.TException {
			sPut_args args = new sPut_args();
			args.setKeyValuePairs(keyValuePairs);
			sendBase("sPut", args);
		}

		public void recv_sPut() throws org.apache.thrift.TException {
			sPut_result result = new sPut_result();
			receiveBase(result, "sPut");
			return;
		}

		public void sRemove(List<ByteBuffer> keys)
				throws org.apache.thrift.TException {
			send_sRemove(keys);
			recv_sRemove();
		}

		public void send_sRemove(List<ByteBuffer> keys)
				throws org.apache.thrift.TException {
			sRemove_args args = new sRemove_args();
			args.setKeys(keys);
			sendBase("sRemove", args);
		}

		public void recv_sRemove() throws org.apache.thrift.TException {
			sRemove_result result = new sRemove_result();
			receiveBase(result, "sRemove");
			return;
		}

		public List<SegmentHash> getSegmentHashes(long treeId,
				List<Integer> nodeIds) throws org.apache.thrift.TException {
			send_getSegmentHashes(treeId, nodeIds);
			return recv_getSegmentHashes();
		}

		public void send_getSegmentHashes(long treeId, List<Integer> nodeIds)
				throws org.apache.thrift.TException {
			getSegmentHashes_args args = new getSegmentHashes_args();
			args.setTreeId(treeId);
			args.setNodeIds(nodeIds);
			sendBase("getSegmentHashes", args);
		}

		public List<SegmentHash> recv_getSegmentHashes()
				throws org.apache.thrift.TException {
			getSegmentHashes_result result = new getSegmentHashes_result();
			receiveBase(result, "getSegmentHashes");
			if (result.isSetSuccess()) {
				return result.success;
			}
			throw new org.apache.thrift.TApplicationException(
					org.apache.thrift.TApplicationException.MISSING_RESULT,
					"getSegmentHashes failed: unknown result");
		}

		public SegmentHash getSegmentHash(long treeId, int nodeId)
				throws org.apache.thrift.TException {
			send_getSegmentHash(treeId, nodeId);
			return recv_getSegmentHash();
		}

		public void send_getSegmentHash(long treeId, int nodeId)
				throws org.apache.thrift.TException {
			getSegmentHash_args args = new getSegmentHash_args();
			args.setTreeId(treeId);
			args.setNodeId(nodeId);
			sendBase("getSegmentHash", args);
		}

		public SegmentHash recv_getSegmentHash()
				throws org.apache.thrift.TException {
			getSegmentHash_result result = new getSegmentHash_result();
			receiveBase(result, "getSegmentHash");
			if (result.isSetSuccess()) {
				return result.success;
			}
			throw new org.apache.thrift.TApplicationException(
					org.apache.thrift.TApplicationException.MISSING_RESULT,
					"getSegmentHash failed: unknown result");
		}

		public List<SegmentData> getSegment(long treeId, int segId)
				throws org.apache.thrift.TException {
			send_getSegment(treeId, segId);
			return recv_getSegment();
		}

		public void send_getSegment(long treeId, int segId)
				throws org.apache.thrift.TException {
			getSegment_args args = new getSegment_args();
			args.setTreeId(treeId);
			args.setSegId(segId);
			sendBase("getSegment", args);
		}

		public List<SegmentData> recv_getSegment()
				throws org.apache.thrift.TException {
			getSegment_result result = new getSegment_result();
			receiveBase(result, "getSegment");
			if (result.isSetSuccess()) {
				return result.success;
			}
			throw new org.apache.thrift.TApplicationException(
					org.apache.thrift.TApplicationException.MISSING_RESULT,
					"getSegment failed: unknown result");
		}

		public SegmentData getSegmentData(long treeId, int segId, ByteBuffer key)
				throws org.apache.thrift.TException {
			send_getSegmentData(treeId, segId, key);
			return recv_getSegmentData();
		}

		public void send_getSegmentData(long treeId, int segId, ByteBuffer key)
				throws org.apache.thrift.TException {
			getSegmentData_args args = new getSegmentData_args();
			args.setTreeId(treeId);
			args.setSegId(segId);
			args.setKey(key);
			sendBase("getSegmentData", args);
		}

		public SegmentData recv_getSegmentData()
				throws org.apache.thrift.TException {
			getSegmentData_result result = new getSegmentData_result();
			receiveBase(result, "getSegmentData");
			if (result.isSetSuccess()) {
				return result.success;
			}
			throw new org.apache.thrift.TApplicationException(
					org.apache.thrift.TApplicationException.MISSING_RESULT,
					"getSegmentData failed: unknown result");
		}

		public void deleteTreeNodes(long treeId, List<Integer> nodeIds)
				throws org.apache.thrift.TException {
			send_deleteTreeNodes(treeId, nodeIds);
			recv_deleteTreeNodes();
		}

		public void send_deleteTreeNodes(long treeId, List<Integer> nodeIds)
				throws org.apache.thrift.TException {
			deleteTreeNodes_args args = new deleteTreeNodes_args();
			args.setTreeId(treeId);
			args.setNodeIds(nodeIds);
			sendBase("deleteTreeNodes", args);
		}

		public void recv_deleteTreeNodes() throws org.apache.thrift.TException {
			deleteTreeNodes_result result = new deleteTreeNodes_result();
			receiveBase(result, "deleteTreeNodes");
			return;
		}

		public void rebuildHashTree(ServerName sn, long treeId, long tokenNo,
				long expFullRebuildTimeInt) throws org.apache.thrift.TException {
			send_rebuildHashTree(sn, treeId, tokenNo, expFullRebuildTimeInt);
		}

		public void send_rebuildHashTree(ServerName sn, long treeId,
				long tokenNo, long expFullRebuildTimeInt)
				throws org.apache.thrift.TException {
			rebuildHashTree_args args = new rebuildHashTree_args();
			args.setSn(sn);
			args.setTreeId(treeId);
			args.setTokenNo(tokenNo);
			args.setExpFullRebuildTimeInt(expFullRebuildTimeInt);
			sendBase("rebuildHashTree", args);
		}

		public void postRebuildHashTreeResponse(ServerName sn, long treeId,
				long tokenNo) throws org.apache.thrift.TException {
			send_postRebuildHashTreeResponse(sn, treeId, tokenNo);
		}

		public void send_postRebuildHashTreeResponse(ServerName sn,
				long treeId, long tokenNo) throws org.apache.thrift.TException {
			postRebuildHashTreeResponse_args args = new postRebuildHashTreeResponse_args();
			args.setSn(sn);
			args.setTreeId(treeId);
			args.setTokenNo(tokenNo);
			sendBase("postRebuildHashTreeResponse", args);
		}

	}

	public static class AsyncClient extends
			org.apache.thrift.async.TAsyncClient implements AsyncIface {
		public static class Factory implements
				org.apache.thrift.async.TAsyncClientFactory<AsyncClient> {
			private org.apache.thrift.async.TAsyncClientManager clientManager;
			private org.apache.thrift.protocol.TProtocolFactory protocolFactory;

			public Factory(
					org.apache.thrift.async.TAsyncClientManager clientManager,
					org.apache.thrift.protocol.TProtocolFactory protocolFactory) {
				this.clientManager = clientManager;
				this.protocolFactory = protocolFactory;
			}

			public AsyncClient getAsyncClient(
					org.apache.thrift.transport.TNonblockingTransport transport) {
				return new AsyncClient(protocolFactory, clientManager,
						transport);
			}
		}

		public AsyncClient(
				org.apache.thrift.protocol.TProtocolFactory protocolFactory,
				org.apache.thrift.async.TAsyncClientManager clientManager,
				org.apache.thrift.transport.TNonblockingTransport transport) {
			super(protocolFactory, clientManager, transport);
		}

		public void ping(
				org.apache.thrift.async.AsyncMethodCallback<ping_call> resultHandler)
				throws org.apache.thrift.TException {
			checkReady();
			ping_call method_call = new ping_call(resultHandler, this,
					___protocolFactory, ___transport);
			this.___currentMethod = method_call;
			___manager.call(method_call);
		}

		public static class ping_call extends
				org.apache.thrift.async.TAsyncMethodCall {
			public ping_call(
					org.apache.thrift.async.AsyncMethodCallback<ping_call> resultHandler,
					org.apache.thrift.async.TAsyncClient client,
					org.apache.thrift.protocol.TProtocolFactory protocolFactory,
					org.apache.thrift.transport.TNonblockingTransport transport)
					throws org.apache.thrift.TException {
				super(client, protocolFactory, transport, resultHandler, false);
			}

			public void write_args(org.apache.thrift.protocol.TProtocol prot)
					throws org.apache.thrift.TException {
				prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage(
						"ping", org.apache.thrift.protocol.TMessageType.CALL, 0));
				ping_args args = new ping_args();
				args.write(prot);
				prot.writeMessageEnd();
			}

			public String getResult() throws org.apache.thrift.TException {
				if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
					throw new IllegalStateException("Method call not finished!");
				}
				org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(
						getFrameBuffer().array());
				org.apache.thrift.protocol.TProtocol prot = client
						.getProtocolFactory().getProtocol(memoryTransport);
				return (new Client(prot)).recv_ping();
			}
		}

		public void sPut(
				Map<ByteBuffer, ByteBuffer> keyValuePairs,
				org.apache.thrift.async.AsyncMethodCallback<sPut_call> resultHandler)
				throws org.apache.thrift.TException {
			checkReady();
			sPut_call method_call = new sPut_call(keyValuePairs, resultHandler,
					this, ___protocolFactory, ___transport);
			this.___currentMethod = method_call;
			___manager.call(method_call);
		}

		public static class sPut_call extends
				org.apache.thrift.async.TAsyncMethodCall {
			private Map<ByteBuffer, ByteBuffer> keyValuePairs;

			public sPut_call(
					Map<ByteBuffer, ByteBuffer> keyValuePairs,
					org.apache.thrift.async.AsyncMethodCallback<sPut_call> resultHandler,
					org.apache.thrift.async.TAsyncClient client,
					org.apache.thrift.protocol.TProtocolFactory protocolFactory,
					org.apache.thrift.transport.TNonblockingTransport transport)
					throws org.apache.thrift.TException {
				super(client, protocolFactory, transport, resultHandler, false);
				this.keyValuePairs = keyValuePairs;
			}

			public void write_args(org.apache.thrift.protocol.TProtocol prot)
					throws org.apache.thrift.TException {
				prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage(
						"sPut", org.apache.thrift.protocol.TMessageType.CALL, 0));
				sPut_args args = new sPut_args();
				args.setKeyValuePairs(keyValuePairs);
				args.write(prot);
				prot.writeMessageEnd();
			}

			public void getResult() throws org.apache.thrift.TException {
				if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
					throw new IllegalStateException("Method call not finished!");
				}
				org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(
						getFrameBuffer().array());
				org.apache.thrift.protocol.TProtocol prot = client
						.getProtocolFactory().getProtocol(memoryTransport);
				(new Client(prot)).recv_sPut();
			}
		}

		public void sRemove(
				List<ByteBuffer> keys,
				org.apache.thrift.async.AsyncMethodCallback<sRemove_call> resultHandler)
				throws org.apache.thrift.TException {
			checkReady();
			sRemove_call method_call = new sRemove_call(keys, resultHandler,
					this, ___protocolFactory, ___transport);
			this.___currentMethod = method_call;
			___manager.call(method_call);
		}

		public static class sRemove_call extends
				org.apache.thrift.async.TAsyncMethodCall {
			private List<ByteBuffer> keys;

			public sRemove_call(
					List<ByteBuffer> keys,
					org.apache.thrift.async.AsyncMethodCallback<sRemove_call> resultHandler,
					org.apache.thrift.async.TAsyncClient client,
					org.apache.thrift.protocol.TProtocolFactory protocolFactory,
					org.apache.thrift.transport.TNonblockingTransport transport)
					throws org.apache.thrift.TException {
				super(client, protocolFactory, transport, resultHandler, false);
				this.keys = keys;
			}

			public void write_args(org.apache.thrift.protocol.TProtocol prot)
					throws org.apache.thrift.TException {
				prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage(
						"sRemove",
						org.apache.thrift.protocol.TMessageType.CALL, 0));
				sRemove_args args = new sRemove_args();
				args.setKeys(keys);
				args.write(prot);
				prot.writeMessageEnd();
			}

			public void getResult() throws org.apache.thrift.TException {
				if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
					throw new IllegalStateException("Method call not finished!");
				}
				org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(
						getFrameBuffer().array());
				org.apache.thrift.protocol.TProtocol prot = client
						.getProtocolFactory().getProtocol(memoryTransport);
				(new Client(prot)).recv_sRemove();
			}
		}

		public void getSegmentHashes(
				long treeId,
				List<Integer> nodeIds,
				org.apache.thrift.async.AsyncMethodCallback<getSegmentHashes_call> resultHandler)
				throws org.apache.thrift.TException {
			checkReady();
			getSegmentHashes_call method_call = new getSegmentHashes_call(
					treeId, nodeIds, resultHandler, this, ___protocolFactory,
					___transport);
			this.___currentMethod = method_call;
			___manager.call(method_call);
		}

		public static class getSegmentHashes_call extends
				org.apache.thrift.async.TAsyncMethodCall {
			private long treeId;
			private List<Integer> nodeIds;

			public getSegmentHashes_call(
					long treeId,
					List<Integer> nodeIds,
					org.apache.thrift.async.AsyncMethodCallback<getSegmentHashes_call> resultHandler,
					org.apache.thrift.async.TAsyncClient client,
					org.apache.thrift.protocol.TProtocolFactory protocolFactory,
					org.apache.thrift.transport.TNonblockingTransport transport)
					throws org.apache.thrift.TException {
				super(client, protocolFactory, transport, resultHandler, false);
				this.treeId = treeId;
				this.nodeIds = nodeIds;
			}

			public void write_args(org.apache.thrift.protocol.TProtocol prot)
					throws org.apache.thrift.TException {
				prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage(
						"getSegmentHashes",
						org.apache.thrift.protocol.TMessageType.CALL, 0));
				getSegmentHashes_args args = new getSegmentHashes_args();
				args.setTreeId(treeId);
				args.setNodeIds(nodeIds);
				args.write(prot);
				prot.writeMessageEnd();
			}

			public List<SegmentHash> getResult()
					throws org.apache.thrift.TException {
				if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
					throw new IllegalStateException("Method call not finished!");
				}
				org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(
						getFrameBuffer().array());
				org.apache.thrift.protocol.TProtocol prot = client
						.getProtocolFactory().getProtocol(memoryTransport);
				return (new Client(prot)).recv_getSegmentHashes();
			}
		}

		public void getSegmentHash(
				long treeId,
				int nodeId,
				org.apache.thrift.async.AsyncMethodCallback<getSegmentHash_call> resultHandler)
				throws org.apache.thrift.TException {
			checkReady();
			getSegmentHash_call method_call = new getSegmentHash_call(treeId,
					nodeId, resultHandler, this, ___protocolFactory,
					___transport);
			this.___currentMethod = method_call;
			___manager.call(method_call);
		}

		public static class getSegmentHash_call extends
				org.apache.thrift.async.TAsyncMethodCall {
			private long treeId;
			private int nodeId;

			public getSegmentHash_call(
					long treeId,
					int nodeId,
					org.apache.thrift.async.AsyncMethodCallback<getSegmentHash_call> resultHandler,
					org.apache.thrift.async.TAsyncClient client,
					org.apache.thrift.protocol.TProtocolFactory protocolFactory,
					org.apache.thrift.transport.TNonblockingTransport transport)
					throws org.apache.thrift.TException {
				super(client, protocolFactory, transport, resultHandler, false);
				this.treeId = treeId;
				this.nodeId = nodeId;
			}

			public void write_args(org.apache.thrift.protocol.TProtocol prot)
					throws org.apache.thrift.TException {
				prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage(
						"getSegmentHash",
						org.apache.thrift.protocol.TMessageType.CALL, 0));
				getSegmentHash_args args = new getSegmentHash_args();
				args.setTreeId(treeId);
				args.setNodeId(nodeId);
				args.write(prot);
				prot.writeMessageEnd();
			}

			public SegmentHash getResult() throws org.apache.thrift.TException {
				if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
					throw new IllegalStateException("Method call not finished!");
				}
				org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(
						getFrameBuffer().array());
				org.apache.thrift.protocol.TProtocol prot = client
						.getProtocolFactory().getProtocol(memoryTransport);
				return (new Client(prot)).recv_getSegmentHash();
			}
		}

		public void getSegment(
				long treeId,
				int segId,
				org.apache.thrift.async.AsyncMethodCallback<getSegment_call> resultHandler)
				throws org.apache.thrift.TException {
			checkReady();
			getSegment_call method_call = new getSegment_call(treeId, segId,
					resultHandler, this, ___protocolFactory, ___transport);
			this.___currentMethod = method_call;
			___manager.call(method_call);
		}

		public static class getSegment_call extends
				org.apache.thrift.async.TAsyncMethodCall {
			private long treeId;
			private int segId;

			public getSegment_call(
					long treeId,
					int segId,
					org.apache.thrift.async.AsyncMethodCallback<getSegment_call> resultHandler,
					org.apache.thrift.async.TAsyncClient client,
					org.apache.thrift.protocol.TProtocolFactory protocolFactory,
					org.apache.thrift.transport.TNonblockingTransport transport)
					throws org.apache.thrift.TException {
				super(client, protocolFactory, transport, resultHandler, false);
				this.treeId = treeId;
				this.segId = segId;
			}

			public void write_args(org.apache.thrift.protocol.TProtocol prot)
					throws org.apache.thrift.TException {
				prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage(
						"getSegment",
						org.apache.thrift.protocol.TMessageType.CALL, 0));
				getSegment_args args = new getSegment_args();
				args.setTreeId(treeId);
				args.setSegId(segId);
				args.write(prot);
				prot.writeMessageEnd();
			}

			public List<SegmentData> getResult()
					throws org.apache.thrift.TException {
				if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
					throw new IllegalStateException("Method call not finished!");
				}
				org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(
						getFrameBuffer().array());
				org.apache.thrift.protocol.TProtocol prot = client
						.getProtocolFactory().getProtocol(memoryTransport);
				return (new Client(prot)).recv_getSegment();
			}
		}

		public void getSegmentData(
				long treeId,
				int segId,
				ByteBuffer key,
				org.apache.thrift.async.AsyncMethodCallback<getSegmentData_call> resultHandler)
				throws org.apache.thrift.TException {
			checkReady();
			getSegmentData_call method_call = new getSegmentData_call(treeId,
					segId, key, resultHandler, this, ___protocolFactory,
					___transport);
			this.___currentMethod = method_call;
			___manager.call(method_call);
		}

		public static class getSegmentData_call extends
				org.apache.thrift.async.TAsyncMethodCall {
			private long treeId;
			private int segId;
			private ByteBuffer key;

			public getSegmentData_call(
					long treeId,
					int segId,
					ByteBuffer key,
					org.apache.thrift.async.AsyncMethodCallback<getSegmentData_call> resultHandler,
					org.apache.thrift.async.TAsyncClient client,
					org.apache.thrift.protocol.TProtocolFactory protocolFactory,
					org.apache.thrift.transport.TNonblockingTransport transport)
					throws org.apache.thrift.TException {
				super(client, protocolFactory, transport, resultHandler, false);
				this.treeId = treeId;
				this.segId = segId;
				this.key = key;
			}

			public void write_args(org.apache.thrift.protocol.TProtocol prot)
					throws org.apache.thrift.TException {
				prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage(
						"getSegmentData",
						org.apache.thrift.protocol.TMessageType.CALL, 0));
				getSegmentData_args args = new getSegmentData_args();
				args.setTreeId(treeId);
				args.setSegId(segId);
				args.setKey(key);
				args.write(prot);
				prot.writeMessageEnd();
			}

			public SegmentData getResult() throws org.apache.thrift.TException {
				if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
					throw new IllegalStateException("Method call not finished!");
				}
				org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(
						getFrameBuffer().array());
				org.apache.thrift.protocol.TProtocol prot = client
						.getProtocolFactory().getProtocol(memoryTransport);
				return (new Client(prot)).recv_getSegmentData();
			}
		}

		public void deleteTreeNodes(
				long treeId,
				List<Integer> nodeIds,
				org.apache.thrift.async.AsyncMethodCallback<deleteTreeNodes_call> resultHandler)
				throws org.apache.thrift.TException {
			checkReady();
			deleteTreeNodes_call method_call = new deleteTreeNodes_call(treeId,
					nodeIds, resultHandler, this, ___protocolFactory,
					___transport);
			this.___currentMethod = method_call;
			___manager.call(method_call);
		}

		public static class deleteTreeNodes_call extends
				org.apache.thrift.async.TAsyncMethodCall {
			private long treeId;
			private List<Integer> nodeIds;

			public deleteTreeNodes_call(
					long treeId,
					List<Integer> nodeIds,
					org.apache.thrift.async.AsyncMethodCallback<deleteTreeNodes_call> resultHandler,
					org.apache.thrift.async.TAsyncClient client,
					org.apache.thrift.protocol.TProtocolFactory protocolFactory,
					org.apache.thrift.transport.TNonblockingTransport transport)
					throws org.apache.thrift.TException {
				super(client, protocolFactory, transport, resultHandler, false);
				this.treeId = treeId;
				this.nodeIds = nodeIds;
			}

			public void write_args(org.apache.thrift.protocol.TProtocol prot)
					throws org.apache.thrift.TException {
				prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage(
						"deleteTreeNodes",
						org.apache.thrift.protocol.TMessageType.CALL, 0));
				deleteTreeNodes_args args = new deleteTreeNodes_args();
				args.setTreeId(treeId);
				args.setNodeIds(nodeIds);
				args.write(prot);
				prot.writeMessageEnd();
			}

			public void getResult() throws org.apache.thrift.TException {
				if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
					throw new IllegalStateException("Method call not finished!");
				}
				org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(
						getFrameBuffer().array());
				org.apache.thrift.protocol.TProtocol prot = client
						.getProtocolFactory().getProtocol(memoryTransport);
				(new Client(prot)).recv_deleteTreeNodes();
			}
		}

		public void rebuildHashTree(
				ServerName sn,
				long treeId,
				long tokenNo,
				long expFullRebuildTimeInt,
				org.apache.thrift.async.AsyncMethodCallback<rebuildHashTree_call> resultHandler)
				throws org.apache.thrift.TException {
			checkReady();
			rebuildHashTree_call method_call = new rebuildHashTree_call(sn,
					treeId, tokenNo, expFullRebuildTimeInt, resultHandler,
					this, ___protocolFactory, ___transport);
			this.___currentMethod = method_call;
			___manager.call(method_call);
		}

		public static class rebuildHashTree_call extends
				org.apache.thrift.async.TAsyncMethodCall {
			private ServerName sn;
			private long treeId;
			private long tokenNo;
			private long expFullRebuildTimeInt;

			public rebuildHashTree_call(
					ServerName sn,
					long treeId,
					long tokenNo,
					long expFullRebuildTimeInt,
					org.apache.thrift.async.AsyncMethodCallback<rebuildHashTree_call> resultHandler,
					org.apache.thrift.async.TAsyncClient client,
					org.apache.thrift.protocol.TProtocolFactory protocolFactory,
					org.apache.thrift.transport.TNonblockingTransport transport)
					throws org.apache.thrift.TException {
				super(client, protocolFactory, transport, resultHandler, true);
				this.sn = sn;
				this.treeId = treeId;
				this.tokenNo = tokenNo;
				this.expFullRebuildTimeInt = expFullRebuildTimeInt;
			}

			public void write_args(org.apache.thrift.protocol.TProtocol prot)
					throws org.apache.thrift.TException {
				prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage(
						"rebuildHashTree",
						org.apache.thrift.protocol.TMessageType.CALL, 0));
				rebuildHashTree_args args = new rebuildHashTree_args();
				args.setSn(sn);
				args.setTreeId(treeId);
				args.setTokenNo(tokenNo);
				args.setExpFullRebuildTimeInt(expFullRebuildTimeInt);
				args.write(prot);
				prot.writeMessageEnd();
			}

			public void getResult() throws org.apache.thrift.TException {
				if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
					throw new IllegalStateException("Method call not finished!");
				}
				org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(
						getFrameBuffer().array());
				org.apache.thrift.protocol.TProtocol prot = client
						.getProtocolFactory().getProtocol(memoryTransport);
			}
		}

		public void postRebuildHashTreeResponse(
				ServerName sn,
				long treeId,
				long tokenNo,
				org.apache.thrift.async.AsyncMethodCallback<postRebuildHashTreeResponse_call> resultHandler)
				throws org.apache.thrift.TException {
			checkReady();
			postRebuildHashTreeResponse_call method_call = new postRebuildHashTreeResponse_call(
					sn, treeId, tokenNo, resultHandler, this,
					___protocolFactory, ___transport);
			this.___currentMethod = method_call;
			___manager.call(method_call);
		}

		public static class postRebuildHashTreeResponse_call extends
				org.apache.thrift.async.TAsyncMethodCall {
			private ServerName sn;
			private long treeId;
			private long tokenNo;

			public postRebuildHashTreeResponse_call(
					ServerName sn,
					long treeId,
					long tokenNo,
					org.apache.thrift.async.AsyncMethodCallback<postRebuildHashTreeResponse_call> resultHandler,
					org.apache.thrift.async.TAsyncClient client,
					org.apache.thrift.protocol.TProtocolFactory protocolFactory,
					org.apache.thrift.transport.TNonblockingTransport transport)
					throws org.apache.thrift.TException {
				super(client, protocolFactory, transport, resultHandler, true);
				this.sn = sn;
				this.treeId = treeId;
				this.tokenNo = tokenNo;
			}

			public void write_args(org.apache.thrift.protocol.TProtocol prot)
					throws org.apache.thrift.TException {
				prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage(
						"postRebuildHashTreeResponse",
						org.apache.thrift.protocol.TMessageType.CALL, 0));
				postRebuildHashTreeResponse_args args = new postRebuildHashTreeResponse_args();
				args.setSn(sn);
				args.setTreeId(treeId);
				args.setTokenNo(tokenNo);
				args.write(prot);
				prot.writeMessageEnd();
			}

			public void getResult() throws org.apache.thrift.TException {
				if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
					throw new IllegalStateException("Method call not finished!");
				}
				org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(
						getFrameBuffer().array());
				org.apache.thrift.protocol.TProtocol prot = client
						.getProtocolFactory().getProtocol(memoryTransport);
			}
		}

	}

	public static class Processor<I extends Iface> extends
			org.apache.thrift.TBaseProcessor<I> implements
			org.apache.thrift.TProcessor {
		private static final Logger LOGGER = LoggerFactory
				.getLogger(Processor.class.getName());

		public Processor(I iface) {
			super(
					iface,
					getProcessMap(new HashMap<String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>>()));
		}

		protected Processor(
				I iface,
				Map<String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>> processMap) {
			super(iface, getProcessMap(processMap));
		}

		private static <I extends Iface> Map<String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>> getProcessMap(
				Map<String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>> processMap) {
			processMap.put("ping", new ping());
			processMap.put("sPut", new sPut());
			processMap.put("sRemove", new sRemove());
			processMap.put("getSegmentHashes", new getSegmentHashes());
			processMap.put("getSegmentHash", new getSegmentHash());
			processMap.put("getSegment", new getSegment());
			processMap.put("getSegmentData", new getSegmentData());
			processMap.put("deleteTreeNodes", new deleteTreeNodes());
			processMap.put("rebuildHashTree", new rebuildHashTree());
			processMap.put("postRebuildHashTreeResponse",
					new postRebuildHashTreeResponse());
			return processMap;
		}

		private static class ping<I extends Iface> extends
				org.apache.thrift.ProcessFunction<I, ping_args> {
			public ping() {
				super("ping");
			}

			protected ping_args getEmptyArgsInstance() {
				return new ping_args();
			}

			protected ping_result getResult(I iface, ping_args args)
					throws org.apache.thrift.TException {
				ping_result result = new ping_result();
				result.success = iface.ping();
				return result;
			}
		}

		private static class sPut<I extends Iface> extends
				org.apache.thrift.ProcessFunction<I, sPut_args> {
			public sPut() {
				super("sPut");
			}

			protected sPut_args getEmptyArgsInstance() {
				return new sPut_args();
			}

			protected sPut_result getResult(I iface, sPut_args args)
					throws org.apache.thrift.TException {
				sPut_result result = new sPut_result();
				iface.sPut(args.keyValuePairs);
				return result;
			}
		}

		private static class sRemove<I extends Iface> extends
				org.apache.thrift.ProcessFunction<I, sRemove_args> {
			public sRemove() {
				super("sRemove");
			}

			protected sRemove_args getEmptyArgsInstance() {
				return new sRemove_args();
			}

			protected sRemove_result getResult(I iface, sRemove_args args)
					throws org.apache.thrift.TException {
				sRemove_result result = new sRemove_result();
				iface.sRemove(args.keys);
				return result;
			}
		}

		private static class getSegmentHashes<I extends Iface> extends
				org.apache.thrift.ProcessFunction<I, getSegmentHashes_args> {
			public getSegmentHashes() {
				super("getSegmentHashes");
			}

			protected getSegmentHashes_args getEmptyArgsInstance() {
				return new getSegmentHashes_args();
			}

			protected getSegmentHashes_result getResult(I iface,
					getSegmentHashes_args args)
					throws org.apache.thrift.TException {
				getSegmentHashes_result result = new getSegmentHashes_result();
				result.success = iface.getSegmentHashes(args.treeId,
						args.nodeIds);
				return result;
			}
		}

		private static class getSegmentHash<I extends Iface> extends
				org.apache.thrift.ProcessFunction<I, getSegmentHash_args> {
			public getSegmentHash() {
				super("getSegmentHash");
			}

			protected getSegmentHash_args getEmptyArgsInstance() {
				return new getSegmentHash_args();
			}

			protected getSegmentHash_result getResult(I iface,
					getSegmentHash_args args)
					throws org.apache.thrift.TException {
				getSegmentHash_result result = new getSegmentHash_result();
				result.success = iface.getSegmentHash(args.treeId, args.nodeId);
				return result;
			}
		}

		private static class getSegment<I extends Iface> extends
				org.apache.thrift.ProcessFunction<I, getSegment_args> {
			public getSegment() {
				super("getSegment");
			}

			protected getSegment_args getEmptyArgsInstance() {
				return new getSegment_args();
			}

			protected getSegment_result getResult(I iface, getSegment_args args)
					throws org.apache.thrift.TException {
				getSegment_result result = new getSegment_result();
				result.success = iface.getSegment(args.treeId, args.segId);
				return result;
			}
		}

		private static class getSegmentData<I extends Iface> extends
				org.apache.thrift.ProcessFunction<I, getSegmentData_args> {
			public getSegmentData() {
				super("getSegmentData");
			}

			protected getSegmentData_args getEmptyArgsInstance() {
				return new getSegmentData_args();
			}

			protected getSegmentData_result getResult(I iface,
					getSegmentData_args args)
					throws org.apache.thrift.TException {
				getSegmentData_result result = new getSegmentData_result();
				result.success = iface.getSegmentData(args.treeId, args.segId,
						args.key);
				return result;
			}
		}

		private static class deleteTreeNodes<I extends Iface> extends
				org.apache.thrift.ProcessFunction<I, deleteTreeNodes_args> {
			public deleteTreeNodes() {
				super("deleteTreeNodes");
			}

			protected deleteTreeNodes_args getEmptyArgsInstance() {
				return new deleteTreeNodes_args();
			}

			protected deleteTreeNodes_result getResult(I iface,
					deleteTreeNodes_args args)
					throws org.apache.thrift.TException {
				deleteTreeNodes_result result = new deleteTreeNodes_result();
				iface.deleteTreeNodes(args.treeId, args.nodeIds);
				return result;
			}
		}

		private static class rebuildHashTree<I extends Iface> extends
				org.apache.thrift.ProcessFunction<I, rebuildHashTree_args> {
			public rebuildHashTree() {
				super("rebuildHashTree");
			}

			protected rebuildHashTree_args getEmptyArgsInstance() {
				return new rebuildHashTree_args();
			}

			protected org.apache.thrift.TBase getResult(I iface,
					rebuildHashTree_args args)
					throws org.apache.thrift.TException {
				iface.rebuildHashTree(args.sn, args.treeId, args.tokenNo,
						args.expFullRebuildTimeInt);
				return null;
			}
		}

		private static class postRebuildHashTreeResponse<I extends Iface>
				extends
				org.apache.thrift.ProcessFunction<I, postRebuildHashTreeResponse_args> {
			public postRebuildHashTreeResponse() {
				super("postRebuildHashTreeResponse");
			}

			protected postRebuildHashTreeResponse_args getEmptyArgsInstance() {
				return new postRebuildHashTreeResponse_args();
			}

			protected org.apache.thrift.TBase getResult(I iface,
					postRebuildHashTreeResponse_args args)
					throws org.apache.thrift.TException {
				iface.postRebuildHashTreeResponse(args.sn, args.treeId,
						args.tokenNo);
				return null;
			}
		}

	}

	public static class ping_args implements
			org.apache.thrift.TBase<ping_args, ping_args._Fields>,
			java.io.Serializable, Cloneable {
		private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
				"ping_args");

		private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
		static {
			schemes.put(StandardScheme.class,
					new ping_argsStandardSchemeFactory());
			schemes.put(TupleScheme.class, new ping_argsTupleSchemeFactory());
		}

		/**
		 * The set of fields this struct contains, along with convenience
		 * methods for finding and manipulating them.
		 */
		public enum _Fields implements org.apache.thrift.TFieldIdEnum {
			;

			private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

			static {
				for (_Fields field : EnumSet.allOf(_Fields.class)) {
					byName.put(field.getFieldName(), field);
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, or null if its
			 * not found.
			 */
			public static _Fields findByThriftId(int fieldId) {
				switch (fieldId) {
				default:
					return null;
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, throwing an
			 * exception if it is not found.
			 */
			public static _Fields findByThriftIdOrThrow(int fieldId) {
				_Fields fields = findByThriftId(fieldId);
				if (fields == null)
					throw new IllegalArgumentException("Field " + fieldId
							+ " doesn't exist!");
				return fields;
			}

			/**
			 * Find the _Fields constant that matches name, or null if its not
			 * found.
			 */
			public static _Fields findByName(String name) {
				return byName.get(name);
			}

			private final short _thriftId;
			private final String _fieldName;

			_Fields(short thriftId, String fieldName) {
				_thriftId = thriftId;
				_fieldName = fieldName;
			}

			public short getThriftFieldId() {
				return _thriftId;
			}

			public String getFieldName() {
				return _fieldName;
			}
		}

		public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
		static {
			Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
					_Fields.class);
			metaDataMap = Collections.unmodifiableMap(tmpMap);
			org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
					ping_args.class, metaDataMap);
		}

		public ping_args() {
		}

		/**
		 * Performs a deep copy on <i>other</i>.
		 */
		public ping_args(ping_args other) {
		}

		public ping_args deepCopy() {
			return new ping_args(this);
		}

		@Override
		public void clear() {
		}

		public void setFieldValue(_Fields field, Object value) {
			switch (field) {
			}
		}

		public Object getFieldValue(_Fields field) {
			switch (field) {
			}
			throw new IllegalStateException();
		}

		/**
		 * Returns true if field corresponding to fieldID is set (has been
		 * assigned a value) and false otherwise
		 */
		public boolean isSet(_Fields field) {
			if (field == null) {
				throw new IllegalArgumentException();
			}

			switch (field) {
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (that instanceof ping_args)
				return this.equals((ping_args) that);
			return false;
		}

		public boolean equals(ping_args that) {
			if (that == null)
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		public int compareTo(ping_args other) {
			if (!getClass().equals(other.getClass())) {
				return getClass().getName().compareTo(
						other.getClass().getName());
			}

			int lastComparison = 0;
			ping_args typedOther = (ping_args) other;

			return 0;
		}

		public _Fields fieldForId(int fieldId) {
			return _Fields.findByThriftId(fieldId);
		}

		public void read(org.apache.thrift.protocol.TProtocol iprot)
				throws org.apache.thrift.TException {
			schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
		}

		public void write(org.apache.thrift.protocol.TProtocol oprot)
				throws org.apache.thrift.TException {
			schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("ping_args(");
			boolean first = true;

			sb.append(")");
			return sb.toString();
		}

		public void validate() throws org.apache.thrift.TException {
			// check for required fields
		}

		private void writeObject(java.io.ObjectOutputStream out)
				throws java.io.IOException {
			try {
				write(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(out)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private void readObject(java.io.ObjectInputStream in)
				throws java.io.IOException, ClassNotFoundException {
			try {
				read(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(in)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private static class ping_argsStandardSchemeFactory implements
				SchemeFactory {
			public ping_argsStandardScheme getScheme() {
				return new ping_argsStandardScheme();
			}
		}

		private static class ping_argsStandardScheme extends
				StandardScheme<ping_args> {

			public void read(org.apache.thrift.protocol.TProtocol iprot,
					ping_args struct) throws org.apache.thrift.TException {
				org.apache.thrift.protocol.TField schemeField;
				iprot.readStructBegin();
				while (true) {
					schemeField = iprot.readFieldBegin();
					if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
						break;
					}
					switch (schemeField.id) {
					default:
						org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
								schemeField.type);
					}
					iprot.readFieldEnd();
				}
				iprot.readStructEnd();

				// check for required fields of primitive type, which can't be
				// checked in the validate method
				struct.validate();
			}

			public void write(org.apache.thrift.protocol.TProtocol oprot,
					ping_args struct) throws org.apache.thrift.TException {
				struct.validate();

				oprot.writeStructBegin(STRUCT_DESC);
				oprot.writeFieldStop();
				oprot.writeStructEnd();
			}

		}

		private static class ping_argsTupleSchemeFactory implements
				SchemeFactory {
			public ping_argsTupleScheme getScheme() {
				return new ping_argsTupleScheme();
			}
		}

		private static class ping_argsTupleScheme extends
				TupleScheme<ping_args> {

			@Override
			public void write(org.apache.thrift.protocol.TProtocol prot,
					ping_args struct) throws org.apache.thrift.TException {
				TTupleProtocol oprot = (TTupleProtocol) prot;
			}

			@Override
			public void read(org.apache.thrift.protocol.TProtocol prot,
					ping_args struct) throws org.apache.thrift.TException {
				TTupleProtocol iprot = (TTupleProtocol) prot;
			}
		}

	}

	public static class ping_result implements
			org.apache.thrift.TBase<ping_result, ping_result._Fields>,
			java.io.Serializable, Cloneable {
		private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
				"ping_result");

		private static final org.apache.thrift.protocol.TField SUCCESS_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"success", org.apache.thrift.protocol.TType.STRING, (short) 0);

		private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
		static {
			schemes.put(StandardScheme.class,
					new ping_resultStandardSchemeFactory());
			schemes.put(TupleScheme.class, new ping_resultTupleSchemeFactory());
		}

		public String success; // required

		/**
		 * The set of fields this struct contains, along with convenience
		 * methods for finding and manipulating them.
		 */
		public enum _Fields implements org.apache.thrift.TFieldIdEnum {
			SUCCESS((short) 0, "success");

			private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

			static {
				for (_Fields field : EnumSet.allOf(_Fields.class)) {
					byName.put(field.getFieldName(), field);
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, or null if its
			 * not found.
			 */
			public static _Fields findByThriftId(int fieldId) {
				switch (fieldId) {
				case 0: // SUCCESS
					return SUCCESS;
				default:
					return null;
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, throwing an
			 * exception if it is not found.
			 */
			public static _Fields findByThriftIdOrThrow(int fieldId) {
				_Fields fields = findByThriftId(fieldId);
				if (fields == null)
					throw new IllegalArgumentException("Field " + fieldId
							+ " doesn't exist!");
				return fields;
			}

			/**
			 * Find the _Fields constant that matches name, or null if its not
			 * found.
			 */
			public static _Fields findByName(String name) {
				return byName.get(name);
			}

			private final short _thriftId;
			private final String _fieldName;

			_Fields(short thriftId, String fieldName) {
				_thriftId = thriftId;
				_fieldName = fieldName;
			}

			public short getThriftFieldId() {
				return _thriftId;
			}

			public String getFieldName() {
				return _fieldName;
			}
		}

		// isset id assignments
		public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
		static {
			Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
					_Fields.class);
			tmpMap.put(_Fields.SUCCESS,
					new org.apache.thrift.meta_data.FieldMetaData("success",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.FieldValueMetaData(
									org.apache.thrift.protocol.TType.STRING)));
			metaDataMap = Collections.unmodifiableMap(tmpMap);
			org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
					ping_result.class, metaDataMap);
		}

		public ping_result() {
		}

		public ping_result(String success) {
			this();
			this.success = success;
		}

		/**
		 * Performs a deep copy on <i>other</i>.
		 */
		public ping_result(ping_result other) {
			if (other.isSetSuccess()) {
				this.success = other.success;
			}
		}

		public ping_result deepCopy() {
			return new ping_result(this);
		}

		@Override
		public void clear() {
			this.success = null;
		}

		public String getSuccess() {
			return this.success;
		}

		public ping_result setSuccess(String success) {
			this.success = success;
			return this;
		}

		public void unsetSuccess() {
			this.success = null;
		}

		/**
		 * Returns true if field success is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetSuccess() {
			return this.success != null;
		}

		public void setSuccessIsSet(boolean value) {
			if (!value) {
				this.success = null;
			}
		}

		public void setFieldValue(_Fields field, Object value) {
			switch (field) {
			case SUCCESS:
				if (value == null) {
					unsetSuccess();
				} else {
					setSuccess((String) value);
				}
				break;

			}
		}

		public Object getFieldValue(_Fields field) {
			switch (field) {
			case SUCCESS:
				return getSuccess();

			}
			throw new IllegalStateException();
		}

		/**
		 * Returns true if field corresponding to fieldID is set (has been
		 * assigned a value) and false otherwise
		 */
		public boolean isSet(_Fields field) {
			if (field == null) {
				throw new IllegalArgumentException();
			}

			switch (field) {
			case SUCCESS:
				return isSetSuccess();
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (that instanceof ping_result)
				return this.equals((ping_result) that);
			return false;
		}

		public boolean equals(ping_result that) {
			if (that == null)
				return false;

			boolean this_present_success = true && this.isSetSuccess();
			boolean that_present_success = true && that.isSetSuccess();
			if (this_present_success || that_present_success) {
				if (!(this_present_success && that_present_success))
					return false;
				if (!this.success.equals(that.success))
					return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		public int compareTo(ping_result other) {
			if (!getClass().equals(other.getClass())) {
				return getClass().getName().compareTo(
						other.getClass().getName());
			}

			int lastComparison = 0;
			ping_result typedOther = (ping_result) other;

			lastComparison = Boolean.valueOf(isSetSuccess()).compareTo(
					typedOther.isSetSuccess());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetSuccess()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.success, typedOther.success);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			return 0;
		}

		public _Fields fieldForId(int fieldId) {
			return _Fields.findByThriftId(fieldId);
		}

		public void read(org.apache.thrift.protocol.TProtocol iprot)
				throws org.apache.thrift.TException {
			schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
		}

		public void write(org.apache.thrift.protocol.TProtocol oprot)
				throws org.apache.thrift.TException {
			schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("ping_result(");
			boolean first = true;

			sb.append("success:");
			if (this.success == null) {
				sb.append("null");
			} else {
				sb.append(this.success);
			}
			first = false;
			sb.append(")");
			return sb.toString();
		}

		public void validate() throws org.apache.thrift.TException {
			// check for required fields
		}

		private void writeObject(java.io.ObjectOutputStream out)
				throws java.io.IOException {
			try {
				write(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(out)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private void readObject(java.io.ObjectInputStream in)
				throws java.io.IOException, ClassNotFoundException {
			try {
				read(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(in)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private static class ping_resultStandardSchemeFactory implements
				SchemeFactory {
			public ping_resultStandardScheme getScheme() {
				return new ping_resultStandardScheme();
			}
		}

		private static class ping_resultStandardScheme extends
				StandardScheme<ping_result> {

			public void read(org.apache.thrift.protocol.TProtocol iprot,
					ping_result struct) throws org.apache.thrift.TException {
				org.apache.thrift.protocol.TField schemeField;
				iprot.readStructBegin();
				while (true) {
					schemeField = iprot.readFieldBegin();
					if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
						break;
					}
					switch (schemeField.id) {
					case 0: // SUCCESS
						if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
							struct.success = iprot.readString();
							struct.setSuccessIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					default:
						org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
								schemeField.type);
					}
					iprot.readFieldEnd();
				}
				iprot.readStructEnd();

				// check for required fields of primitive type, which can't be
				// checked in the validate method
				struct.validate();
			}

			public void write(org.apache.thrift.protocol.TProtocol oprot,
					ping_result struct) throws org.apache.thrift.TException {
				struct.validate();

				oprot.writeStructBegin(STRUCT_DESC);
				if (struct.success != null) {
					oprot.writeFieldBegin(SUCCESS_FIELD_DESC);
					oprot.writeString(struct.success);
					oprot.writeFieldEnd();
				}
				oprot.writeFieldStop();
				oprot.writeStructEnd();
			}

		}

		private static class ping_resultTupleSchemeFactory implements
				SchemeFactory {
			public ping_resultTupleScheme getScheme() {
				return new ping_resultTupleScheme();
			}
		}

		private static class ping_resultTupleScheme extends
				TupleScheme<ping_result> {

			@Override
			public void write(org.apache.thrift.protocol.TProtocol prot,
					ping_result struct) throws org.apache.thrift.TException {
				TTupleProtocol oprot = (TTupleProtocol) prot;
				BitSet optionals = new BitSet();
				if (struct.isSetSuccess()) {
					optionals.set(0);
				}
				oprot.writeBitSet(optionals, 1);
				if (struct.isSetSuccess()) {
					oprot.writeString(struct.success);
				}
			}

			@Override
			public void read(org.apache.thrift.protocol.TProtocol prot,
					ping_result struct) throws org.apache.thrift.TException {
				TTupleProtocol iprot = (TTupleProtocol) prot;
				BitSet incoming = iprot.readBitSet(1);
				if (incoming.get(0)) {
					struct.success = iprot.readString();
					struct.setSuccessIsSet(true);
				}
			}
		}

	}

	public static class sPut_args implements
			org.apache.thrift.TBase<sPut_args, sPut_args._Fields>,
			java.io.Serializable, Cloneable {
		private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
				"sPut_args");

		private static final org.apache.thrift.protocol.TField KEY_VALUE_PAIRS_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"keyValuePairs", org.apache.thrift.protocol.TType.MAP,
				(short) 1);

		private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
		static {
			schemes.put(StandardScheme.class,
					new sPut_argsStandardSchemeFactory());
			schemes.put(TupleScheme.class, new sPut_argsTupleSchemeFactory());
		}

		public Map<ByteBuffer, ByteBuffer> keyValuePairs; // required

		/**
		 * The set of fields this struct contains, along with convenience
		 * methods for finding and manipulating them.
		 */
		public enum _Fields implements org.apache.thrift.TFieldIdEnum {
			KEY_VALUE_PAIRS((short) 1, "keyValuePairs");

			private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

			static {
				for (_Fields field : EnumSet.allOf(_Fields.class)) {
					byName.put(field.getFieldName(), field);
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, or null if its
			 * not found.
			 */
			public static _Fields findByThriftId(int fieldId) {
				switch (fieldId) {
				case 1: // KEY_VALUE_PAIRS
					return KEY_VALUE_PAIRS;
				default:
					return null;
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, throwing an
			 * exception if it is not found.
			 */
			public static _Fields findByThriftIdOrThrow(int fieldId) {
				_Fields fields = findByThriftId(fieldId);
				if (fields == null)
					throw new IllegalArgumentException("Field " + fieldId
							+ " doesn't exist!");
				return fields;
			}

			/**
			 * Find the _Fields constant that matches name, or null if its not
			 * found.
			 */
			public static _Fields findByName(String name) {
				return byName.get(name);
			}

			private final short _thriftId;
			private final String _fieldName;

			_Fields(short thriftId, String fieldName) {
				_thriftId = thriftId;
				_fieldName = fieldName;
			}

			public short getThriftFieldId() {
				return _thriftId;
			}

			public String getFieldName() {
				return _fieldName;
			}
		}

		// isset id assignments
		public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
		static {
			Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
					_Fields.class);
			tmpMap.put(
					_Fields.KEY_VALUE_PAIRS,
					new org.apache.thrift.meta_data.FieldMetaData(
							"keyValuePairs",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.MapMetaData(
									org.apache.thrift.protocol.TType.MAP,
									new org.apache.thrift.meta_data.FieldValueMetaData(
											org.apache.thrift.protocol.TType.STRING,
											true),
									new org.apache.thrift.meta_data.FieldValueMetaData(
											org.apache.thrift.protocol.TType.STRING,
											true))));
			metaDataMap = Collections.unmodifiableMap(tmpMap);
			org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
					sPut_args.class, metaDataMap);
		}

		public sPut_args() {
		}

		public sPut_args(Map<ByteBuffer, ByteBuffer> keyValuePairs) {
			this();
			this.keyValuePairs = keyValuePairs;
		}

		/**
		 * Performs a deep copy on <i>other</i>.
		 */
		public sPut_args(sPut_args other) {
			if (other.isSetKeyValuePairs()) {
				Map<ByteBuffer, ByteBuffer> __this__keyValuePairs = new HashMap<ByteBuffer, ByteBuffer>();
				for (Map.Entry<ByteBuffer, ByteBuffer> other_element : other.keyValuePairs
						.entrySet()) {

					ByteBuffer other_element_key = other_element.getKey();
					ByteBuffer other_element_value = other_element.getValue();

					ByteBuffer __this__keyValuePairs_copy_key = org.apache.thrift.TBaseHelper
							.copyBinary(other_element_key);
					;

					ByteBuffer __this__keyValuePairs_copy_value = org.apache.thrift.TBaseHelper
							.copyBinary(other_element_value);
					;

					__this__keyValuePairs.put(__this__keyValuePairs_copy_key,
							__this__keyValuePairs_copy_value);
				}
				this.keyValuePairs = __this__keyValuePairs;
			}
		}

		public sPut_args deepCopy() {
			return new sPut_args(this);
		}

		@Override
		public void clear() {
			this.keyValuePairs = null;
		}

		public int getKeyValuePairsSize() {
			return (this.keyValuePairs == null) ? 0 : this.keyValuePairs.size();
		}

		public void putToKeyValuePairs(ByteBuffer key, ByteBuffer val) {
			if (this.keyValuePairs == null) {
				this.keyValuePairs = new HashMap<ByteBuffer, ByteBuffer>();
			}
			this.keyValuePairs.put(key, val);
		}

		public Map<ByteBuffer, ByteBuffer> getKeyValuePairs() {
			return this.keyValuePairs;
		}

		public sPut_args setKeyValuePairs(
				Map<ByteBuffer, ByteBuffer> keyValuePairs) {
			this.keyValuePairs = keyValuePairs;
			return this;
		}

		public void unsetKeyValuePairs() {
			this.keyValuePairs = null;
		}

		/**
		 * Returns true if field keyValuePairs is set (has been assigned a
		 * value) and false otherwise
		 */
		public boolean isSetKeyValuePairs() {
			return this.keyValuePairs != null;
		}

		public void setKeyValuePairsIsSet(boolean value) {
			if (!value) {
				this.keyValuePairs = null;
			}
		}

		public void setFieldValue(_Fields field, Object value) {
			switch (field) {
			case KEY_VALUE_PAIRS:
				if (value == null) {
					unsetKeyValuePairs();
				} else {
					setKeyValuePairs((Map<ByteBuffer, ByteBuffer>) value);
				}
				break;

			}
		}

		public Object getFieldValue(_Fields field) {
			switch (field) {
			case KEY_VALUE_PAIRS:
				return getKeyValuePairs();

			}
			throw new IllegalStateException();
		}

		/**
		 * Returns true if field corresponding to fieldID is set (has been
		 * assigned a value) and false otherwise
		 */
		public boolean isSet(_Fields field) {
			if (field == null) {
				throw new IllegalArgumentException();
			}

			switch (field) {
			case KEY_VALUE_PAIRS:
				return isSetKeyValuePairs();
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (that instanceof sPut_args)
				return this.equals((sPut_args) that);
			return false;
		}

		public boolean equals(sPut_args that) {
			if (that == null)
				return false;

			boolean this_present_keyValuePairs = true && this
					.isSetKeyValuePairs();
			boolean that_present_keyValuePairs = true && that
					.isSetKeyValuePairs();
			if (this_present_keyValuePairs || that_present_keyValuePairs) {
				if (!(this_present_keyValuePairs && that_present_keyValuePairs))
					return false;
				if (!this.keyValuePairs.equals(that.keyValuePairs))
					return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		public int compareTo(sPut_args other) {
			if (!getClass().equals(other.getClass())) {
				return getClass().getName().compareTo(
						other.getClass().getName());
			}

			int lastComparison = 0;
			sPut_args typedOther = (sPut_args) other;

			lastComparison = Boolean.valueOf(isSetKeyValuePairs()).compareTo(
					typedOther.isSetKeyValuePairs());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetKeyValuePairs()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.keyValuePairs, typedOther.keyValuePairs);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			return 0;
		}

		public _Fields fieldForId(int fieldId) {
			return _Fields.findByThriftId(fieldId);
		}

		public void read(org.apache.thrift.protocol.TProtocol iprot)
				throws org.apache.thrift.TException {
			schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
		}

		public void write(org.apache.thrift.protocol.TProtocol oprot)
				throws org.apache.thrift.TException {
			schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("sPut_args(");
			boolean first = true;

			sb.append("keyValuePairs:");
			if (this.keyValuePairs == null) {
				sb.append("null");
			} else {
				sb.append(this.keyValuePairs);
			}
			first = false;
			sb.append(")");
			return sb.toString();
		}

		public void validate() throws org.apache.thrift.TException {
			// check for required fields
		}

		private void writeObject(java.io.ObjectOutputStream out)
				throws java.io.IOException {
			try {
				write(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(out)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private void readObject(java.io.ObjectInputStream in)
				throws java.io.IOException, ClassNotFoundException {
			try {
				read(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(in)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private static class sPut_argsStandardSchemeFactory implements
				SchemeFactory {
			public sPut_argsStandardScheme getScheme() {
				return new sPut_argsStandardScheme();
			}
		}

		private static class sPut_argsStandardScheme extends
				StandardScheme<sPut_args> {

			public void read(org.apache.thrift.protocol.TProtocol iprot,
					sPut_args struct) throws org.apache.thrift.TException {
				org.apache.thrift.protocol.TField schemeField;
				iprot.readStructBegin();
				while (true) {
					schemeField = iprot.readFieldBegin();
					if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
						break;
					}
					switch (schemeField.id) {
					case 1: // KEY_VALUE_PAIRS
						if (schemeField.type == org.apache.thrift.protocol.TType.MAP) {
							{
								org.apache.thrift.protocol.TMap _map0 = iprot
										.readMapBegin();
								struct.keyValuePairs = new HashMap<ByteBuffer, ByteBuffer>(
										2 * _map0.size);
								for (int _i1 = 0; _i1 < _map0.size; ++_i1) {
									ByteBuffer _key2; // required
									ByteBuffer _val3; // required
									_key2 = iprot.readBinary();
									_val3 = iprot.readBinary();
									struct.keyValuePairs.put(_key2, _val3);
								}
								iprot.readMapEnd();
							}
							struct.setKeyValuePairsIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					default:
						org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
								schemeField.type);
					}
					iprot.readFieldEnd();
				}
				iprot.readStructEnd();

				// check for required fields of primitive type, which can't be
				// checked in the validate method
				struct.validate();
			}

			public void write(org.apache.thrift.protocol.TProtocol oprot,
					sPut_args struct) throws org.apache.thrift.TException {
				struct.validate();

				oprot.writeStructBegin(STRUCT_DESC);
				if (struct.keyValuePairs != null) {
					oprot.writeFieldBegin(KEY_VALUE_PAIRS_FIELD_DESC);
					{
						oprot.writeMapBegin(new org.apache.thrift.protocol.TMap(
								org.apache.thrift.protocol.TType.STRING,
								org.apache.thrift.protocol.TType.STRING,
								struct.keyValuePairs.size()));
						for (Map.Entry<ByteBuffer, ByteBuffer> _iter4 : struct.keyValuePairs
								.entrySet()) {
							oprot.writeBinary(_iter4.getKey());
							oprot.writeBinary(_iter4.getValue());
						}
						oprot.writeMapEnd();
					}
					oprot.writeFieldEnd();
				}
				oprot.writeFieldStop();
				oprot.writeStructEnd();
			}

		}

		private static class sPut_argsTupleSchemeFactory implements
				SchemeFactory {
			public sPut_argsTupleScheme getScheme() {
				return new sPut_argsTupleScheme();
			}
		}

		private static class sPut_argsTupleScheme extends
				TupleScheme<sPut_args> {

			@Override
			public void write(org.apache.thrift.protocol.TProtocol prot,
					sPut_args struct) throws org.apache.thrift.TException {
				TTupleProtocol oprot = (TTupleProtocol) prot;
				BitSet optionals = new BitSet();
				if (struct.isSetKeyValuePairs()) {
					optionals.set(0);
				}
				oprot.writeBitSet(optionals, 1);
				if (struct.isSetKeyValuePairs()) {
					{
						oprot.writeI32(struct.keyValuePairs.size());
						for (Map.Entry<ByteBuffer, ByteBuffer> _iter5 : struct.keyValuePairs
								.entrySet()) {
							oprot.writeBinary(_iter5.getKey());
							oprot.writeBinary(_iter5.getValue());
						}
					}
				}
			}

			@Override
			public void read(org.apache.thrift.protocol.TProtocol prot,
					sPut_args struct) throws org.apache.thrift.TException {
				TTupleProtocol iprot = (TTupleProtocol) prot;
				BitSet incoming = iprot.readBitSet(1);
				if (incoming.get(0)) {
					{
						org.apache.thrift.protocol.TMap _map6 = new org.apache.thrift.protocol.TMap(
								org.apache.thrift.protocol.TType.STRING,
								org.apache.thrift.protocol.TType.STRING,
								iprot.readI32());
						struct.keyValuePairs = new HashMap<ByteBuffer, ByteBuffer>(
								2 * _map6.size);
						for (int _i7 = 0; _i7 < _map6.size; ++_i7) {
							ByteBuffer _key8; // required
							ByteBuffer _val9; // required
							_key8 = iprot.readBinary();
							_val9 = iprot.readBinary();
							struct.keyValuePairs.put(_key8, _val9);
						}
					}
					struct.setKeyValuePairsIsSet(true);
				}
			}
		}

	}

	public static class sPut_result implements
			org.apache.thrift.TBase<sPut_result, sPut_result._Fields>,
			java.io.Serializable, Cloneable {
		private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
				"sPut_result");

		private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
		static {
			schemes.put(StandardScheme.class,
					new sPut_resultStandardSchemeFactory());
			schemes.put(TupleScheme.class, new sPut_resultTupleSchemeFactory());
		}

		/**
		 * The set of fields this struct contains, along with convenience
		 * methods for finding and manipulating them.
		 */
		public enum _Fields implements org.apache.thrift.TFieldIdEnum {
			;

			private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

			static {
				for (_Fields field : EnumSet.allOf(_Fields.class)) {
					byName.put(field.getFieldName(), field);
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, or null if its
			 * not found.
			 */
			public static _Fields findByThriftId(int fieldId) {
				switch (fieldId) {
				default:
					return null;
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, throwing an
			 * exception if it is not found.
			 */
			public static _Fields findByThriftIdOrThrow(int fieldId) {
				_Fields fields = findByThriftId(fieldId);
				if (fields == null)
					throw new IllegalArgumentException("Field " + fieldId
							+ " doesn't exist!");
				return fields;
			}

			/**
			 * Find the _Fields constant that matches name, or null if its not
			 * found.
			 */
			public static _Fields findByName(String name) {
				return byName.get(name);
			}

			private final short _thriftId;
			private final String _fieldName;

			_Fields(short thriftId, String fieldName) {
				_thriftId = thriftId;
				_fieldName = fieldName;
			}

			public short getThriftFieldId() {
				return _thriftId;
			}

			public String getFieldName() {
				return _fieldName;
			}
		}

		public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
		static {
			Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
					_Fields.class);
			metaDataMap = Collections.unmodifiableMap(tmpMap);
			org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
					sPut_result.class, metaDataMap);
		}

		public sPut_result() {
		}

		/**
		 * Performs a deep copy on <i>other</i>.
		 */
		public sPut_result(sPut_result other) {
		}

		public sPut_result deepCopy() {
			return new sPut_result(this);
		}

		@Override
		public void clear() {
		}

		public void setFieldValue(_Fields field, Object value) {
			switch (field) {
			}
		}

		public Object getFieldValue(_Fields field) {
			switch (field) {
			}
			throw new IllegalStateException();
		}

		/**
		 * Returns true if field corresponding to fieldID is set (has been
		 * assigned a value) and false otherwise
		 */
		public boolean isSet(_Fields field) {
			if (field == null) {
				throw new IllegalArgumentException();
			}

			switch (field) {
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (that instanceof sPut_result)
				return this.equals((sPut_result) that);
			return false;
		}

		public boolean equals(sPut_result that) {
			if (that == null)
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		public int compareTo(sPut_result other) {
			if (!getClass().equals(other.getClass())) {
				return getClass().getName().compareTo(
						other.getClass().getName());
			}

			int lastComparison = 0;
			sPut_result typedOther = (sPut_result) other;

			return 0;
		}

		public _Fields fieldForId(int fieldId) {
			return _Fields.findByThriftId(fieldId);
		}

		public void read(org.apache.thrift.protocol.TProtocol iprot)
				throws org.apache.thrift.TException {
			schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
		}

		public void write(org.apache.thrift.protocol.TProtocol oprot)
				throws org.apache.thrift.TException {
			schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("sPut_result(");
			boolean first = true;

			sb.append(")");
			return sb.toString();
		}

		public void validate() throws org.apache.thrift.TException {
			// check for required fields
		}

		private void writeObject(java.io.ObjectOutputStream out)
				throws java.io.IOException {
			try {
				write(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(out)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private void readObject(java.io.ObjectInputStream in)
				throws java.io.IOException, ClassNotFoundException {
			try {
				read(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(in)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private static class sPut_resultStandardSchemeFactory implements
				SchemeFactory {
			public sPut_resultStandardScheme getScheme() {
				return new sPut_resultStandardScheme();
			}
		}

		private static class sPut_resultStandardScheme extends
				StandardScheme<sPut_result> {

			public void read(org.apache.thrift.protocol.TProtocol iprot,
					sPut_result struct) throws org.apache.thrift.TException {
				org.apache.thrift.protocol.TField schemeField;
				iprot.readStructBegin();
				while (true) {
					schemeField = iprot.readFieldBegin();
					if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
						break;
					}
					switch (schemeField.id) {
					default:
						org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
								schemeField.type);
					}
					iprot.readFieldEnd();
				}
				iprot.readStructEnd();

				// check for required fields of primitive type, which can't be
				// checked in the validate method
				struct.validate();
			}

			public void write(org.apache.thrift.protocol.TProtocol oprot,
					sPut_result struct) throws org.apache.thrift.TException {
				struct.validate();

				oprot.writeStructBegin(STRUCT_DESC);
				oprot.writeFieldStop();
				oprot.writeStructEnd();
			}

		}

		private static class sPut_resultTupleSchemeFactory implements
				SchemeFactory {
			public sPut_resultTupleScheme getScheme() {
				return new sPut_resultTupleScheme();
			}
		}

		private static class sPut_resultTupleScheme extends
				TupleScheme<sPut_result> {

			@Override
			public void write(org.apache.thrift.protocol.TProtocol prot,
					sPut_result struct) throws org.apache.thrift.TException {
				TTupleProtocol oprot = (TTupleProtocol) prot;
			}

			@Override
			public void read(org.apache.thrift.protocol.TProtocol prot,
					sPut_result struct) throws org.apache.thrift.TException {
				TTupleProtocol iprot = (TTupleProtocol) prot;
			}
		}

	}

	public static class sRemove_args implements
			org.apache.thrift.TBase<sRemove_args, sRemove_args._Fields>,
			java.io.Serializable, Cloneable {
		private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
				"sRemove_args");

		private static final org.apache.thrift.protocol.TField KEYS_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"keys", org.apache.thrift.protocol.TType.LIST, (short) 1);

		private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
		static {
			schemes.put(StandardScheme.class,
					new sRemove_argsStandardSchemeFactory());
			schemes.put(TupleScheme.class, new sRemove_argsTupleSchemeFactory());
		}

		public List<ByteBuffer> keys; // required

		/**
		 * The set of fields this struct contains, along with convenience
		 * methods for finding and manipulating them.
		 */
		public enum _Fields implements org.apache.thrift.TFieldIdEnum {
			KEYS((short) 1, "keys");

			private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

			static {
				for (_Fields field : EnumSet.allOf(_Fields.class)) {
					byName.put(field.getFieldName(), field);
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, or null if its
			 * not found.
			 */
			public static _Fields findByThriftId(int fieldId) {
				switch (fieldId) {
				case 1: // KEYS
					return KEYS;
				default:
					return null;
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, throwing an
			 * exception if it is not found.
			 */
			public static _Fields findByThriftIdOrThrow(int fieldId) {
				_Fields fields = findByThriftId(fieldId);
				if (fields == null)
					throw new IllegalArgumentException("Field " + fieldId
							+ " doesn't exist!");
				return fields;
			}

			/**
			 * Find the _Fields constant that matches name, or null if its not
			 * found.
			 */
			public static _Fields findByName(String name) {
				return byName.get(name);
			}

			private final short _thriftId;
			private final String _fieldName;

			_Fields(short thriftId, String fieldName) {
				_thriftId = thriftId;
				_fieldName = fieldName;
			}

			public short getThriftFieldId() {
				return _thriftId;
			}

			public String getFieldName() {
				return _fieldName;
			}
		}

		// isset id assignments
		public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
		static {
			Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
					_Fields.class);
			tmpMap.put(
					_Fields.KEYS,
					new org.apache.thrift.meta_data.FieldMetaData(
							"keys",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.ListMetaData(
									org.apache.thrift.protocol.TType.LIST,
									new org.apache.thrift.meta_data.FieldValueMetaData(
											org.apache.thrift.protocol.TType.STRING,
											true))));
			metaDataMap = Collections.unmodifiableMap(tmpMap);
			org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
					sRemove_args.class, metaDataMap);
		}

		public sRemove_args() {
		}

		public sRemove_args(List<ByteBuffer> keys) {
			this();
			this.keys = keys;
		}

		/**
		 * Performs a deep copy on <i>other</i>.
		 */
		public sRemove_args(sRemove_args other) {
			if (other.isSetKeys()) {
				List<ByteBuffer> __this__keys = new ArrayList<ByteBuffer>();
				for (ByteBuffer other_element : other.keys) {
					ByteBuffer temp_binary_element = org.apache.thrift.TBaseHelper
							.copyBinary(other_element);
					;
					__this__keys.add(temp_binary_element);
				}
				this.keys = __this__keys;
			}
		}

		public sRemove_args deepCopy() {
			return new sRemove_args(this);
		}

		@Override
		public void clear() {
			this.keys = null;
		}

		public int getKeysSize() {
			return (this.keys == null) ? 0 : this.keys.size();
		}

		public java.util.Iterator<ByteBuffer> getKeysIterator() {
			return (this.keys == null) ? null : this.keys.iterator();
		}

		public void addToKeys(ByteBuffer elem) {
			if (this.keys == null) {
				this.keys = new ArrayList<ByteBuffer>();
			}
			this.keys.add(elem);
		}

		public List<ByteBuffer> getKeys() {
			return this.keys;
		}

		public sRemove_args setKeys(List<ByteBuffer> keys) {
			this.keys = keys;
			return this;
		}

		public void unsetKeys() {
			this.keys = null;
		}

		/**
		 * Returns true if field keys is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetKeys() {
			return this.keys != null;
		}

		public void setKeysIsSet(boolean value) {
			if (!value) {
				this.keys = null;
			}
		}

		public void setFieldValue(_Fields field, Object value) {
			switch (field) {
			case KEYS:
				if (value == null) {
					unsetKeys();
				} else {
					setKeys((List<ByteBuffer>) value);
				}
				break;

			}
		}

		public Object getFieldValue(_Fields field) {
			switch (field) {
			case KEYS:
				return getKeys();

			}
			throw new IllegalStateException();
		}

		/**
		 * Returns true if field corresponding to fieldID is set (has been
		 * assigned a value) and false otherwise
		 */
		public boolean isSet(_Fields field) {
			if (field == null) {
				throw new IllegalArgumentException();
			}

			switch (field) {
			case KEYS:
				return isSetKeys();
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (that instanceof sRemove_args)
				return this.equals((sRemove_args) that);
			return false;
		}

		public boolean equals(sRemove_args that) {
			if (that == null)
				return false;

			boolean this_present_keys = true && this.isSetKeys();
			boolean that_present_keys = true && that.isSetKeys();
			if (this_present_keys || that_present_keys) {
				if (!(this_present_keys && that_present_keys))
					return false;
				if (!this.keys.equals(that.keys))
					return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		public int compareTo(sRemove_args other) {
			if (!getClass().equals(other.getClass())) {
				return getClass().getName().compareTo(
						other.getClass().getName());
			}

			int lastComparison = 0;
			sRemove_args typedOther = (sRemove_args) other;

			lastComparison = Boolean.valueOf(isSetKeys()).compareTo(
					typedOther.isSetKeys());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetKeys()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.keys, typedOther.keys);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			return 0;
		}

		public _Fields fieldForId(int fieldId) {
			return _Fields.findByThriftId(fieldId);
		}

		public void read(org.apache.thrift.protocol.TProtocol iprot)
				throws org.apache.thrift.TException {
			schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
		}

		public void write(org.apache.thrift.protocol.TProtocol oprot)
				throws org.apache.thrift.TException {
			schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("sRemove_args(");
			boolean first = true;

			sb.append("keys:");
			if (this.keys == null) {
				sb.append("null");
			} else {
				sb.append(this.keys);
			}
			first = false;
			sb.append(")");
			return sb.toString();
		}

		public void validate() throws org.apache.thrift.TException {
			// check for required fields
		}

		private void writeObject(java.io.ObjectOutputStream out)
				throws java.io.IOException {
			try {
				write(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(out)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private void readObject(java.io.ObjectInputStream in)
				throws java.io.IOException, ClassNotFoundException {
			try {
				read(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(in)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private static class sRemove_argsStandardSchemeFactory implements
				SchemeFactory {
			public sRemove_argsStandardScheme getScheme() {
				return new sRemove_argsStandardScheme();
			}
		}

		private static class sRemove_argsStandardScheme extends
				StandardScheme<sRemove_args> {

			public void read(org.apache.thrift.protocol.TProtocol iprot,
					sRemove_args struct) throws org.apache.thrift.TException {
				org.apache.thrift.protocol.TField schemeField;
				iprot.readStructBegin();
				while (true) {
					schemeField = iprot.readFieldBegin();
					if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
						break;
					}
					switch (schemeField.id) {
					case 1: // KEYS
						if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
							{
								org.apache.thrift.protocol.TList _list10 = iprot
										.readListBegin();
								struct.keys = new ArrayList<ByteBuffer>(
										_list10.size);
								for (int _i11 = 0; _i11 < _list10.size; ++_i11) {
									ByteBuffer _elem12; // required
									_elem12 = iprot.readBinary();
									struct.keys.add(_elem12);
								}
								iprot.readListEnd();
							}
							struct.setKeysIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					default:
						org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
								schemeField.type);
					}
					iprot.readFieldEnd();
				}
				iprot.readStructEnd();

				// check for required fields of primitive type, which can't be
				// checked in the validate method
				struct.validate();
			}

			public void write(org.apache.thrift.protocol.TProtocol oprot,
					sRemove_args struct) throws org.apache.thrift.TException {
				struct.validate();

				oprot.writeStructBegin(STRUCT_DESC);
				if (struct.keys != null) {
					oprot.writeFieldBegin(KEYS_FIELD_DESC);
					{
						oprot.writeListBegin(new org.apache.thrift.protocol.TList(
								org.apache.thrift.protocol.TType.STRING,
								struct.keys.size()));
						for (ByteBuffer _iter13 : struct.keys) {
							oprot.writeBinary(_iter13);
						}
						oprot.writeListEnd();
					}
					oprot.writeFieldEnd();
				}
				oprot.writeFieldStop();
				oprot.writeStructEnd();
			}

		}

		private static class sRemove_argsTupleSchemeFactory implements
				SchemeFactory {
			public sRemove_argsTupleScheme getScheme() {
				return new sRemove_argsTupleScheme();
			}
		}

		private static class sRemove_argsTupleScheme extends
				TupleScheme<sRemove_args> {

			@Override
			public void write(org.apache.thrift.protocol.TProtocol prot,
					sRemove_args struct) throws org.apache.thrift.TException {
				TTupleProtocol oprot = (TTupleProtocol) prot;
				BitSet optionals = new BitSet();
				if (struct.isSetKeys()) {
					optionals.set(0);
				}
				oprot.writeBitSet(optionals, 1);
				if (struct.isSetKeys()) {
					{
						oprot.writeI32(struct.keys.size());
						for (ByteBuffer _iter14 : struct.keys) {
							oprot.writeBinary(_iter14);
						}
					}
				}
			}

			@Override
			public void read(org.apache.thrift.protocol.TProtocol prot,
					sRemove_args struct) throws org.apache.thrift.TException {
				TTupleProtocol iprot = (TTupleProtocol) prot;
				BitSet incoming = iprot.readBitSet(1);
				if (incoming.get(0)) {
					{
						org.apache.thrift.protocol.TList _list15 = new org.apache.thrift.protocol.TList(
								org.apache.thrift.protocol.TType.STRING,
								iprot.readI32());
						struct.keys = new ArrayList<ByteBuffer>(_list15.size);
						for (int _i16 = 0; _i16 < _list15.size; ++_i16) {
							ByteBuffer _elem17; // required
							_elem17 = iprot.readBinary();
							struct.keys.add(_elem17);
						}
					}
					struct.setKeysIsSet(true);
				}
			}
		}

	}

	public static class sRemove_result implements
			org.apache.thrift.TBase<sRemove_result, sRemove_result._Fields>,
			java.io.Serializable, Cloneable {
		private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
				"sRemove_result");

		private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
		static {
			schemes.put(StandardScheme.class,
					new sRemove_resultStandardSchemeFactory());
			schemes.put(TupleScheme.class,
					new sRemove_resultTupleSchemeFactory());
		}

		/**
		 * The set of fields this struct contains, along with convenience
		 * methods for finding and manipulating them.
		 */
		public enum _Fields implements org.apache.thrift.TFieldIdEnum {
			;

			private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

			static {
				for (_Fields field : EnumSet.allOf(_Fields.class)) {
					byName.put(field.getFieldName(), field);
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, or null if its
			 * not found.
			 */
			public static _Fields findByThriftId(int fieldId) {
				switch (fieldId) {
				default:
					return null;
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, throwing an
			 * exception if it is not found.
			 */
			public static _Fields findByThriftIdOrThrow(int fieldId) {
				_Fields fields = findByThriftId(fieldId);
				if (fields == null)
					throw new IllegalArgumentException("Field " + fieldId
							+ " doesn't exist!");
				return fields;
			}

			/**
			 * Find the _Fields constant that matches name, or null if its not
			 * found.
			 */
			public static _Fields findByName(String name) {
				return byName.get(name);
			}

			private final short _thriftId;
			private final String _fieldName;

			_Fields(short thriftId, String fieldName) {
				_thriftId = thriftId;
				_fieldName = fieldName;
			}

			public short getThriftFieldId() {
				return _thriftId;
			}

			public String getFieldName() {
				return _fieldName;
			}
		}

		public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
		static {
			Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
					_Fields.class);
			metaDataMap = Collections.unmodifiableMap(tmpMap);
			org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
					sRemove_result.class, metaDataMap);
		}

		public sRemove_result() {
		}

		/**
		 * Performs a deep copy on <i>other</i>.
		 */
		public sRemove_result(sRemove_result other) {
		}

		public sRemove_result deepCopy() {
			return new sRemove_result(this);
		}

		@Override
		public void clear() {
		}

		public void setFieldValue(_Fields field, Object value) {
			switch (field) {
			}
		}

		public Object getFieldValue(_Fields field) {
			switch (field) {
			}
			throw new IllegalStateException();
		}

		/**
		 * Returns true if field corresponding to fieldID is set (has been
		 * assigned a value) and false otherwise
		 */
		public boolean isSet(_Fields field) {
			if (field == null) {
				throw new IllegalArgumentException();
			}

			switch (field) {
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (that instanceof sRemove_result)
				return this.equals((sRemove_result) that);
			return false;
		}

		public boolean equals(sRemove_result that) {
			if (that == null)
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		public int compareTo(sRemove_result other) {
			if (!getClass().equals(other.getClass())) {
				return getClass().getName().compareTo(
						other.getClass().getName());
			}

			int lastComparison = 0;
			sRemove_result typedOther = (sRemove_result) other;

			return 0;
		}

		public _Fields fieldForId(int fieldId) {
			return _Fields.findByThriftId(fieldId);
		}

		public void read(org.apache.thrift.protocol.TProtocol iprot)
				throws org.apache.thrift.TException {
			schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
		}

		public void write(org.apache.thrift.protocol.TProtocol oprot)
				throws org.apache.thrift.TException {
			schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("sRemove_result(");
			boolean first = true;

			sb.append(")");
			return sb.toString();
		}

		public void validate() throws org.apache.thrift.TException {
			// check for required fields
		}

		private void writeObject(java.io.ObjectOutputStream out)
				throws java.io.IOException {
			try {
				write(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(out)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private void readObject(java.io.ObjectInputStream in)
				throws java.io.IOException, ClassNotFoundException {
			try {
				read(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(in)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private static class sRemove_resultStandardSchemeFactory implements
				SchemeFactory {
			public sRemove_resultStandardScheme getScheme() {
				return new sRemove_resultStandardScheme();
			}
		}

		private static class sRemove_resultStandardScheme extends
				StandardScheme<sRemove_result> {

			public void read(org.apache.thrift.protocol.TProtocol iprot,
					sRemove_result struct) throws org.apache.thrift.TException {
				org.apache.thrift.protocol.TField schemeField;
				iprot.readStructBegin();
				while (true) {
					schemeField = iprot.readFieldBegin();
					if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
						break;
					}
					switch (schemeField.id) {
					default:
						org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
								schemeField.type);
					}
					iprot.readFieldEnd();
				}
				iprot.readStructEnd();

				// check for required fields of primitive type, which can't be
				// checked in the validate method
				struct.validate();
			}

			public void write(org.apache.thrift.protocol.TProtocol oprot,
					sRemove_result struct) throws org.apache.thrift.TException {
				struct.validate();

				oprot.writeStructBegin(STRUCT_DESC);
				oprot.writeFieldStop();
				oprot.writeStructEnd();
			}

		}

		private static class sRemove_resultTupleSchemeFactory implements
				SchemeFactory {
			public sRemove_resultTupleScheme getScheme() {
				return new sRemove_resultTupleScheme();
			}
		}

		private static class sRemove_resultTupleScheme extends
				TupleScheme<sRemove_result> {

			@Override
			public void write(org.apache.thrift.protocol.TProtocol prot,
					sRemove_result struct) throws org.apache.thrift.TException {
				TTupleProtocol oprot = (TTupleProtocol) prot;
			}

			@Override
			public void read(org.apache.thrift.protocol.TProtocol prot,
					sRemove_result struct) throws org.apache.thrift.TException {
				TTupleProtocol iprot = (TTupleProtocol) prot;
			}
		}

	}

	public static class getSegmentHashes_args
			implements
			org.apache.thrift.TBase<getSegmentHashes_args, getSegmentHashes_args._Fields>,
			java.io.Serializable, Cloneable {
		private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
				"getSegmentHashes_args");

		private static final org.apache.thrift.protocol.TField TREE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"treeId", org.apache.thrift.protocol.TType.I64, (short) 1);
		private static final org.apache.thrift.protocol.TField NODE_IDS_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"nodeIds", org.apache.thrift.protocol.TType.LIST, (short) 2);

		private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
		static {
			schemes.put(StandardScheme.class,
					new getSegmentHashes_argsStandardSchemeFactory());
			schemes.put(TupleScheme.class,
					new getSegmentHashes_argsTupleSchemeFactory());
		}

		public long treeId; // required
		public List<Integer> nodeIds; // required

		/**
		 * The set of fields this struct contains, along with convenience
		 * methods for finding and manipulating them.
		 */
		public enum _Fields implements org.apache.thrift.TFieldIdEnum {
			TREE_ID((short) 1, "treeId"), NODE_IDS((short) 2, "nodeIds");

			private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

			static {
				for (_Fields field : EnumSet.allOf(_Fields.class)) {
					byName.put(field.getFieldName(), field);
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, or null if its
			 * not found.
			 */
			public static _Fields findByThriftId(int fieldId) {
				switch (fieldId) {
				case 1: // TREE_ID
					return TREE_ID;
				case 2: // NODE_IDS
					return NODE_IDS;
				default:
					return null;
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, throwing an
			 * exception if it is not found.
			 */
			public static _Fields findByThriftIdOrThrow(int fieldId) {
				_Fields fields = findByThriftId(fieldId);
				if (fields == null)
					throw new IllegalArgumentException("Field " + fieldId
							+ " doesn't exist!");
				return fields;
			}

			/**
			 * Find the _Fields constant that matches name, or null if its not
			 * found.
			 */
			public static _Fields findByName(String name) {
				return byName.get(name);
			}

			private final short _thriftId;
			private final String _fieldName;

			_Fields(short thriftId, String fieldName) {
				_thriftId = thriftId;
				_fieldName = fieldName;
			}

			public short getThriftFieldId() {
				return _thriftId;
			}

			public String getFieldName() {
				return _fieldName;
			}
		}

		// isset id assignments
		private static final int __TREEID_ISSET_ID = 0;
		private BitSet __isset_bit_vector = new BitSet(1);
		public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
		static {
			Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
					_Fields.class);
			tmpMap.put(_Fields.TREE_ID,
					new org.apache.thrift.meta_data.FieldMetaData("treeId",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.FieldValueMetaData(
									org.apache.thrift.protocol.TType.I64)));
			tmpMap.put(
					_Fields.NODE_IDS,
					new org.apache.thrift.meta_data.FieldMetaData(
							"nodeIds",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.ListMetaData(
									org.apache.thrift.protocol.TType.LIST,
									new org.apache.thrift.meta_data.FieldValueMetaData(
											org.apache.thrift.protocol.TType.I32))));
			metaDataMap = Collections.unmodifiableMap(tmpMap);
			org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
					getSegmentHashes_args.class, metaDataMap);
		}

		public getSegmentHashes_args() {
		}

		public getSegmentHashes_args(long treeId, List<Integer> nodeIds) {
			this();
			this.treeId = treeId;
			setTreeIdIsSet(true);
			this.nodeIds = nodeIds;
		}

		/**
		 * Performs a deep copy on <i>other</i>.
		 */
		public getSegmentHashes_args(getSegmentHashes_args other) {
			__isset_bit_vector.clear();
			__isset_bit_vector.or(other.__isset_bit_vector);
			this.treeId = other.treeId;
			if (other.isSetNodeIds()) {
				List<Integer> __this__nodeIds = new ArrayList<Integer>();
				for (Integer other_element : other.nodeIds) {
					__this__nodeIds.add(other_element);
				}
				this.nodeIds = __this__nodeIds;
			}
		}

		public getSegmentHashes_args deepCopy() {
			return new getSegmentHashes_args(this);
		}

		@Override
		public void clear() {
			setTreeIdIsSet(false);
			this.treeId = 0;
			this.nodeIds = null;
		}

		public long getTreeId() {
			return this.treeId;
		}

		public getSegmentHashes_args setTreeId(long treeId) {
			this.treeId = treeId;
			setTreeIdIsSet(true);
			return this;
		}

		public void unsetTreeId() {
			__isset_bit_vector.clear(__TREEID_ISSET_ID);
		}

		/**
		 * Returns true if field treeId is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetTreeId() {
			return __isset_bit_vector.get(__TREEID_ISSET_ID);
		}

		public void setTreeIdIsSet(boolean value) {
			__isset_bit_vector.set(__TREEID_ISSET_ID, value);
		}

		public int getNodeIdsSize() {
			return (this.nodeIds == null) ? 0 : this.nodeIds.size();
		}

		public java.util.Iterator<Integer> getNodeIdsIterator() {
			return (this.nodeIds == null) ? null : this.nodeIds.iterator();
		}

		public void addToNodeIds(int elem) {
			if (this.nodeIds == null) {
				this.nodeIds = new ArrayList<Integer>();
			}
			this.nodeIds.add(elem);
		}

		public List<Integer> getNodeIds() {
			return this.nodeIds;
		}

		public getSegmentHashes_args setNodeIds(List<Integer> nodeIds) {
			this.nodeIds = nodeIds;
			return this;
		}

		public void unsetNodeIds() {
			this.nodeIds = null;
		}

		/**
		 * Returns true if field nodeIds is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetNodeIds() {
			return this.nodeIds != null;
		}

		public void setNodeIdsIsSet(boolean value) {
			if (!value) {
				this.nodeIds = null;
			}
		}

		public void setFieldValue(_Fields field, Object value) {
			switch (field) {
			case TREE_ID:
				if (value == null) {
					unsetTreeId();
				} else {
					setTreeId((Long) value);
				}
				break;

			case NODE_IDS:
				if (value == null) {
					unsetNodeIds();
				} else {
					setNodeIds((List<Integer>) value);
				}
				break;

			}
		}

		public Object getFieldValue(_Fields field) {
			switch (field) {
			case TREE_ID:
				return Long.valueOf(getTreeId());

			case NODE_IDS:
				return getNodeIds();

			}
			throw new IllegalStateException();
		}

		/**
		 * Returns true if field corresponding to fieldID is set (has been
		 * assigned a value) and false otherwise
		 */
		public boolean isSet(_Fields field) {
			if (field == null) {
				throw new IllegalArgumentException();
			}

			switch (field) {
			case TREE_ID:
				return isSetTreeId();
			case NODE_IDS:
				return isSetNodeIds();
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (that instanceof getSegmentHashes_args)
				return this.equals((getSegmentHashes_args) that);
			return false;
		}

		public boolean equals(getSegmentHashes_args that) {
			if (that == null)
				return false;

			boolean this_present_treeId = true;
			boolean that_present_treeId = true;
			if (this_present_treeId || that_present_treeId) {
				if (!(this_present_treeId && that_present_treeId))
					return false;
				if (this.treeId != that.treeId)
					return false;
			}

			boolean this_present_nodeIds = true && this.isSetNodeIds();
			boolean that_present_nodeIds = true && that.isSetNodeIds();
			if (this_present_nodeIds || that_present_nodeIds) {
				if (!(this_present_nodeIds && that_present_nodeIds))
					return false;
				if (!this.nodeIds.equals(that.nodeIds))
					return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		public int compareTo(getSegmentHashes_args other) {
			if (!getClass().equals(other.getClass())) {
				return getClass().getName().compareTo(
						other.getClass().getName());
			}

			int lastComparison = 0;
			getSegmentHashes_args typedOther = (getSegmentHashes_args) other;

			lastComparison = Boolean.valueOf(isSetTreeId()).compareTo(
					typedOther.isSetTreeId());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetTreeId()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.treeId, typedOther.treeId);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			lastComparison = Boolean.valueOf(isSetNodeIds()).compareTo(
					typedOther.isSetNodeIds());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetNodeIds()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.nodeIds, typedOther.nodeIds);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			return 0;
		}

		public _Fields fieldForId(int fieldId) {
			return _Fields.findByThriftId(fieldId);
		}

		public void read(org.apache.thrift.protocol.TProtocol iprot)
				throws org.apache.thrift.TException {
			schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
		}

		public void write(org.apache.thrift.protocol.TProtocol oprot)
				throws org.apache.thrift.TException {
			schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("getSegmentHashes_args(");
			boolean first = true;

			sb.append("treeId:");
			sb.append(this.treeId);
			first = false;
			if (!first)
				sb.append(", ");
			sb.append("nodeIds:");
			if (this.nodeIds == null) {
				sb.append("null");
			} else {
				sb.append(this.nodeIds);
			}
			first = false;
			sb.append(")");
			return sb.toString();
		}

		public void validate() throws org.apache.thrift.TException {
			// check for required fields
		}

		private void writeObject(java.io.ObjectOutputStream out)
				throws java.io.IOException {
			try {
				write(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(out)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private void readObject(java.io.ObjectInputStream in)
				throws java.io.IOException, ClassNotFoundException {
			try {
				// it doesn't seem like you should have to do this, but java
				// serialization is wacky, and doesn't call the default
				// constructor.
				__isset_bit_vector = new BitSet(1);
				read(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(in)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private static class getSegmentHashes_argsStandardSchemeFactory
				implements SchemeFactory {
			public getSegmentHashes_argsStandardScheme getScheme() {
				return new getSegmentHashes_argsStandardScheme();
			}
		}

		private static class getSegmentHashes_argsStandardScheme extends
				StandardScheme<getSegmentHashes_args> {

			public void read(org.apache.thrift.protocol.TProtocol iprot,
					getSegmentHashes_args struct)
					throws org.apache.thrift.TException {
				org.apache.thrift.protocol.TField schemeField;
				iprot.readStructBegin();
				while (true) {
					schemeField = iprot.readFieldBegin();
					if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
						break;
					}
					switch (schemeField.id) {
					case 1: // TREE_ID
						if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
							struct.treeId = iprot.readI64();
							struct.setTreeIdIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					case 2: // NODE_IDS
						if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
							{
								org.apache.thrift.protocol.TList _list18 = iprot
										.readListBegin();
								struct.nodeIds = new ArrayList<Integer>(
										_list18.size);
								for (int _i19 = 0; _i19 < _list18.size; ++_i19) {
									int _elem20; // required
									_elem20 = iprot.readI32();
									struct.nodeIds.add(_elem20);
								}
								iprot.readListEnd();
							}
							struct.setNodeIdsIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					default:
						org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
								schemeField.type);
					}
					iprot.readFieldEnd();
				}
				iprot.readStructEnd();

				// check for required fields of primitive type, which can't be
				// checked in the validate method
				struct.validate();
			}

			public void write(org.apache.thrift.protocol.TProtocol oprot,
					getSegmentHashes_args struct)
					throws org.apache.thrift.TException {
				struct.validate();

				oprot.writeStructBegin(STRUCT_DESC);
				oprot.writeFieldBegin(TREE_ID_FIELD_DESC);
				oprot.writeI64(struct.treeId);
				oprot.writeFieldEnd();
				if (struct.nodeIds != null) {
					oprot.writeFieldBegin(NODE_IDS_FIELD_DESC);
					{
						oprot.writeListBegin(new org.apache.thrift.protocol.TList(
								org.apache.thrift.protocol.TType.I32,
								struct.nodeIds.size()));
						for (int _iter21 : struct.nodeIds) {
							oprot.writeI32(_iter21);
						}
						oprot.writeListEnd();
					}
					oprot.writeFieldEnd();
				}
				oprot.writeFieldStop();
				oprot.writeStructEnd();
			}

		}

		private static class getSegmentHashes_argsTupleSchemeFactory implements
				SchemeFactory {
			public getSegmentHashes_argsTupleScheme getScheme() {
				return new getSegmentHashes_argsTupleScheme();
			}
		}

		private static class getSegmentHashes_argsTupleScheme extends
				TupleScheme<getSegmentHashes_args> {

			@Override
			public void write(org.apache.thrift.protocol.TProtocol prot,
					getSegmentHashes_args struct)
					throws org.apache.thrift.TException {
				TTupleProtocol oprot = (TTupleProtocol) prot;
				BitSet optionals = new BitSet();
				if (struct.isSetTreeId()) {
					optionals.set(0);
				}
				if (struct.isSetNodeIds()) {
					optionals.set(1);
				}
				oprot.writeBitSet(optionals, 2);
				if (struct.isSetTreeId()) {
					oprot.writeI64(struct.treeId);
				}
				if (struct.isSetNodeIds()) {
					{
						oprot.writeI32(struct.nodeIds.size());
						for (int _iter22 : struct.nodeIds) {
							oprot.writeI32(_iter22);
						}
					}
				}
			}

			@Override
			public void read(org.apache.thrift.protocol.TProtocol prot,
					getSegmentHashes_args struct)
					throws org.apache.thrift.TException {
				TTupleProtocol iprot = (TTupleProtocol) prot;
				BitSet incoming = iprot.readBitSet(2);
				if (incoming.get(0)) {
					struct.treeId = iprot.readI64();
					struct.setTreeIdIsSet(true);
				}
				if (incoming.get(1)) {
					{
						org.apache.thrift.protocol.TList _list23 = new org.apache.thrift.protocol.TList(
								org.apache.thrift.protocol.TType.I32,
								iprot.readI32());
						struct.nodeIds = new ArrayList<Integer>(_list23.size);
						for (int _i24 = 0; _i24 < _list23.size; ++_i24) {
							int _elem25; // required
							_elem25 = iprot.readI32();
							struct.nodeIds.add(_elem25);
						}
					}
					struct.setNodeIdsIsSet(true);
				}
			}
		}

	}

	public static class getSegmentHashes_result
			implements
			org.apache.thrift.TBase<getSegmentHashes_result, getSegmentHashes_result._Fields>,
			java.io.Serializable, Cloneable {
		private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
				"getSegmentHashes_result");

		private static final org.apache.thrift.protocol.TField SUCCESS_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"success", org.apache.thrift.protocol.TType.LIST, (short) 0);

		private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
		static {
			schemes.put(StandardScheme.class,
					new getSegmentHashes_resultStandardSchemeFactory());
			schemes.put(TupleScheme.class,
					new getSegmentHashes_resultTupleSchemeFactory());
		}

		public List<SegmentHash> success; // required

		/**
		 * The set of fields this struct contains, along with convenience
		 * methods for finding and manipulating them.
		 */
		public enum _Fields implements org.apache.thrift.TFieldIdEnum {
			SUCCESS((short) 0, "success");

			private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

			static {
				for (_Fields field : EnumSet.allOf(_Fields.class)) {
					byName.put(field.getFieldName(), field);
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, or null if its
			 * not found.
			 */
			public static _Fields findByThriftId(int fieldId) {
				switch (fieldId) {
				case 0: // SUCCESS
					return SUCCESS;
				default:
					return null;
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, throwing an
			 * exception if it is not found.
			 */
			public static _Fields findByThriftIdOrThrow(int fieldId) {
				_Fields fields = findByThriftId(fieldId);
				if (fields == null)
					throw new IllegalArgumentException("Field " + fieldId
							+ " doesn't exist!");
				return fields;
			}

			/**
			 * Find the _Fields constant that matches name, or null if its not
			 * found.
			 */
			public static _Fields findByName(String name) {
				return byName.get(name);
			}

			private final short _thriftId;
			private final String _fieldName;

			_Fields(short thriftId, String fieldName) {
				_thriftId = thriftId;
				_fieldName = fieldName;
			}

			public short getThriftFieldId() {
				return _thriftId;
			}

			public String getFieldName() {
				return _fieldName;
			}
		}

		// isset id assignments
		public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
		static {
			Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
					_Fields.class);
			tmpMap.put(
					_Fields.SUCCESS,
					new org.apache.thrift.meta_data.FieldMetaData(
							"success",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.ListMetaData(
									org.apache.thrift.protocol.TType.LIST,
									new org.apache.thrift.meta_data.StructMetaData(
											org.apache.thrift.protocol.TType.STRUCT,
											SegmentHash.class))));
			metaDataMap = Collections.unmodifiableMap(tmpMap);
			org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
					getSegmentHashes_result.class, metaDataMap);
		}

		public getSegmentHashes_result() {
		}

		public getSegmentHashes_result(List<SegmentHash> success) {
			this();
			this.success = success;
		}

		/**
		 * Performs a deep copy on <i>other</i>.
		 */
		public getSegmentHashes_result(getSegmentHashes_result other) {
			if (other.isSetSuccess()) {
				List<SegmentHash> __this__success = new ArrayList<SegmentHash>();
				for (SegmentHash other_element : other.success) {
					__this__success.add(new SegmentHash(other_element));
				}
				this.success = __this__success;
			}
		}

		public getSegmentHashes_result deepCopy() {
			return new getSegmentHashes_result(this);
		}

		@Override
		public void clear() {
			this.success = null;
		}

		public int getSuccessSize() {
			return (this.success == null) ? 0 : this.success.size();
		}

		public java.util.Iterator<SegmentHash> getSuccessIterator() {
			return (this.success == null) ? null : this.success.iterator();
		}

		public void addToSuccess(SegmentHash elem) {
			if (this.success == null) {
				this.success = new ArrayList<SegmentHash>();
			}
			this.success.add(elem);
		}

		public List<SegmentHash> getSuccess() {
			return this.success;
		}

		public getSegmentHashes_result setSuccess(List<SegmentHash> success) {
			this.success = success;
			return this;
		}

		public void unsetSuccess() {
			this.success = null;
		}

		/**
		 * Returns true if field success is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetSuccess() {
			return this.success != null;
		}

		public void setSuccessIsSet(boolean value) {
			if (!value) {
				this.success = null;
			}
		}

		public void setFieldValue(_Fields field, Object value) {
			switch (field) {
			case SUCCESS:
				if (value == null) {
					unsetSuccess();
				} else {
					setSuccess((List<SegmentHash>) value);
				}
				break;

			}
		}

		public Object getFieldValue(_Fields field) {
			switch (field) {
			case SUCCESS:
				return getSuccess();

			}
			throw new IllegalStateException();
		}

		/**
		 * Returns true if field corresponding to fieldID is set (has been
		 * assigned a value) and false otherwise
		 */
		public boolean isSet(_Fields field) {
			if (field == null) {
				throw new IllegalArgumentException();
			}

			switch (field) {
			case SUCCESS:
				return isSetSuccess();
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (that instanceof getSegmentHashes_result)
				return this.equals((getSegmentHashes_result) that);
			return false;
		}

		public boolean equals(getSegmentHashes_result that) {
			if (that == null)
				return false;

			boolean this_present_success = true && this.isSetSuccess();
			boolean that_present_success = true && that.isSetSuccess();
			if (this_present_success || that_present_success) {
				if (!(this_present_success && that_present_success))
					return false;
				if (!this.success.equals(that.success))
					return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		public int compareTo(getSegmentHashes_result other) {
			if (!getClass().equals(other.getClass())) {
				return getClass().getName().compareTo(
						other.getClass().getName());
			}

			int lastComparison = 0;
			getSegmentHashes_result typedOther = (getSegmentHashes_result) other;

			lastComparison = Boolean.valueOf(isSetSuccess()).compareTo(
					typedOther.isSetSuccess());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetSuccess()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.success, typedOther.success);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			return 0;
		}

		public _Fields fieldForId(int fieldId) {
			return _Fields.findByThriftId(fieldId);
		}

		public void read(org.apache.thrift.protocol.TProtocol iprot)
				throws org.apache.thrift.TException {
			schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
		}

		public void write(org.apache.thrift.protocol.TProtocol oprot)
				throws org.apache.thrift.TException {
			schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("getSegmentHashes_result(");
			boolean first = true;

			sb.append("success:");
			if (this.success == null) {
				sb.append("null");
			} else {
				sb.append(this.success);
			}
			first = false;
			sb.append(")");
			return sb.toString();
		}

		public void validate() throws org.apache.thrift.TException {
			// check for required fields
		}

		private void writeObject(java.io.ObjectOutputStream out)
				throws java.io.IOException {
			try {
				write(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(out)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private void readObject(java.io.ObjectInputStream in)
				throws java.io.IOException, ClassNotFoundException {
			try {
				read(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(in)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private static class getSegmentHashes_resultStandardSchemeFactory
				implements SchemeFactory {
			public getSegmentHashes_resultStandardScheme getScheme() {
				return new getSegmentHashes_resultStandardScheme();
			}
		}

		private static class getSegmentHashes_resultStandardScheme extends
				StandardScheme<getSegmentHashes_result> {

			public void read(org.apache.thrift.protocol.TProtocol iprot,
					getSegmentHashes_result struct)
					throws org.apache.thrift.TException {
				org.apache.thrift.protocol.TField schemeField;
				iprot.readStructBegin();
				while (true) {
					schemeField = iprot.readFieldBegin();
					if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
						break;
					}
					switch (schemeField.id) {
					case 0: // SUCCESS
						if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
							{
								org.apache.thrift.protocol.TList _list26 = iprot
										.readListBegin();
								struct.success = new ArrayList<SegmentHash>(
										_list26.size);
								for (int _i27 = 0; _i27 < _list26.size; ++_i27) {
									SegmentHash _elem28; // required
									_elem28 = new SegmentHash();
									_elem28.read(iprot);
									struct.success.add(_elem28);
								}
								iprot.readListEnd();
							}
							struct.setSuccessIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					default:
						org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
								schemeField.type);
					}
					iprot.readFieldEnd();
				}
				iprot.readStructEnd();

				// check for required fields of primitive type, which can't be
				// checked in the validate method
				struct.validate();
			}

			public void write(org.apache.thrift.protocol.TProtocol oprot,
					getSegmentHashes_result struct)
					throws org.apache.thrift.TException {
				struct.validate();

				oprot.writeStructBegin(STRUCT_DESC);
				if (struct.success != null) {
					oprot.writeFieldBegin(SUCCESS_FIELD_DESC);
					{
						oprot.writeListBegin(new org.apache.thrift.protocol.TList(
								org.apache.thrift.protocol.TType.STRUCT,
								struct.success.size()));
						for (SegmentHash _iter29 : struct.success) {
							_iter29.write(oprot);
						}
						oprot.writeListEnd();
					}
					oprot.writeFieldEnd();
				}
				oprot.writeFieldStop();
				oprot.writeStructEnd();
			}

		}

		private static class getSegmentHashes_resultTupleSchemeFactory
				implements SchemeFactory {
			public getSegmentHashes_resultTupleScheme getScheme() {
				return new getSegmentHashes_resultTupleScheme();
			}
		}

		private static class getSegmentHashes_resultTupleScheme extends
				TupleScheme<getSegmentHashes_result> {

			@Override
			public void write(org.apache.thrift.protocol.TProtocol prot,
					getSegmentHashes_result struct)
					throws org.apache.thrift.TException {
				TTupleProtocol oprot = (TTupleProtocol) prot;
				BitSet optionals = new BitSet();
				if (struct.isSetSuccess()) {
					optionals.set(0);
				}
				oprot.writeBitSet(optionals, 1);
				if (struct.isSetSuccess()) {
					{
						oprot.writeI32(struct.success.size());
						for (SegmentHash _iter30 : struct.success) {
							_iter30.write(oprot);
						}
					}
				}
			}

			@Override
			public void read(org.apache.thrift.protocol.TProtocol prot,
					getSegmentHashes_result struct)
					throws org.apache.thrift.TException {
				TTupleProtocol iprot = (TTupleProtocol) prot;
				BitSet incoming = iprot.readBitSet(1);
				if (incoming.get(0)) {
					{
						org.apache.thrift.protocol.TList _list31 = new org.apache.thrift.protocol.TList(
								org.apache.thrift.protocol.TType.STRUCT,
								iprot.readI32());
						struct.success = new ArrayList<SegmentHash>(
								_list31.size);
						for (int _i32 = 0; _i32 < _list31.size; ++_i32) {
							SegmentHash _elem33; // required
							_elem33 = new SegmentHash();
							_elem33.read(iprot);
							struct.success.add(_elem33);
						}
					}
					struct.setSuccessIsSet(true);
				}
			}
		}

	}

	public static class getSegmentHash_args
			implements
			org.apache.thrift.TBase<getSegmentHash_args, getSegmentHash_args._Fields>,
			java.io.Serializable, Cloneable {
		private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
				"getSegmentHash_args");

		private static final org.apache.thrift.protocol.TField TREE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"treeId", org.apache.thrift.protocol.TType.I64, (short) 1);
		private static final org.apache.thrift.protocol.TField NODE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"nodeId", org.apache.thrift.protocol.TType.I32, (short) 2);

		private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
		static {
			schemes.put(StandardScheme.class,
					new getSegmentHash_argsStandardSchemeFactory());
			schemes.put(TupleScheme.class,
					new getSegmentHash_argsTupleSchemeFactory());
		}

		public long treeId; // required
		public int nodeId; // required

		/**
		 * The set of fields this struct contains, along with convenience
		 * methods for finding and manipulating them.
		 */
		public enum _Fields implements org.apache.thrift.TFieldIdEnum {
			TREE_ID((short) 1, "treeId"), NODE_ID((short) 2, "nodeId");

			private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

			static {
				for (_Fields field : EnumSet.allOf(_Fields.class)) {
					byName.put(field.getFieldName(), field);
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, or null if its
			 * not found.
			 */
			public static _Fields findByThriftId(int fieldId) {
				switch (fieldId) {
				case 1: // TREE_ID
					return TREE_ID;
				case 2: // NODE_ID
					return NODE_ID;
				default:
					return null;
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, throwing an
			 * exception if it is not found.
			 */
			public static _Fields findByThriftIdOrThrow(int fieldId) {
				_Fields fields = findByThriftId(fieldId);
				if (fields == null)
					throw new IllegalArgumentException("Field " + fieldId
							+ " doesn't exist!");
				return fields;
			}

			/**
			 * Find the _Fields constant that matches name, or null if its not
			 * found.
			 */
			public static _Fields findByName(String name) {
				return byName.get(name);
			}

			private final short _thriftId;
			private final String _fieldName;

			_Fields(short thriftId, String fieldName) {
				_thriftId = thriftId;
				_fieldName = fieldName;
			}

			public short getThriftFieldId() {
				return _thriftId;
			}

			public String getFieldName() {
				return _fieldName;
			}
		}

		// isset id assignments
		private static final int __TREEID_ISSET_ID = 0;
		private static final int __NODEID_ISSET_ID = 1;
		private BitSet __isset_bit_vector = new BitSet(2);
		public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
		static {
			Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
					_Fields.class);
			tmpMap.put(_Fields.TREE_ID,
					new org.apache.thrift.meta_data.FieldMetaData("treeId",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.FieldValueMetaData(
									org.apache.thrift.protocol.TType.I64)));
			tmpMap.put(_Fields.NODE_ID,
					new org.apache.thrift.meta_data.FieldMetaData("nodeId",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.FieldValueMetaData(
									org.apache.thrift.protocol.TType.I32)));
			metaDataMap = Collections.unmodifiableMap(tmpMap);
			org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
					getSegmentHash_args.class, metaDataMap);
		}

		public getSegmentHash_args() {
		}

		public getSegmentHash_args(long treeId, int nodeId) {
			this();
			this.treeId = treeId;
			setTreeIdIsSet(true);
			this.nodeId = nodeId;
			setNodeIdIsSet(true);
		}

		/**
		 * Performs a deep copy on <i>other</i>.
		 */
		public getSegmentHash_args(getSegmentHash_args other) {
			__isset_bit_vector.clear();
			__isset_bit_vector.or(other.__isset_bit_vector);
			this.treeId = other.treeId;
			this.nodeId = other.nodeId;
		}

		public getSegmentHash_args deepCopy() {
			return new getSegmentHash_args(this);
		}

		@Override
		public void clear() {
			setTreeIdIsSet(false);
			this.treeId = 0;
			setNodeIdIsSet(false);
			this.nodeId = 0;
		}

		public long getTreeId() {
			return this.treeId;
		}

		public getSegmentHash_args setTreeId(long treeId) {
			this.treeId = treeId;
			setTreeIdIsSet(true);
			return this;
		}

		public void unsetTreeId() {
			__isset_bit_vector.clear(__TREEID_ISSET_ID);
		}

		/**
		 * Returns true if field treeId is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetTreeId() {
			return __isset_bit_vector.get(__TREEID_ISSET_ID);
		}

		public void setTreeIdIsSet(boolean value) {
			__isset_bit_vector.set(__TREEID_ISSET_ID, value);
		}

		public int getNodeId() {
			return this.nodeId;
		}

		public getSegmentHash_args setNodeId(int nodeId) {
			this.nodeId = nodeId;
			setNodeIdIsSet(true);
			return this;
		}

		public void unsetNodeId() {
			__isset_bit_vector.clear(__NODEID_ISSET_ID);
		}

		/**
		 * Returns true if field nodeId is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetNodeId() {
			return __isset_bit_vector.get(__NODEID_ISSET_ID);
		}

		public void setNodeIdIsSet(boolean value) {
			__isset_bit_vector.set(__NODEID_ISSET_ID, value);
		}

		public void setFieldValue(_Fields field, Object value) {
			switch (field) {
			case TREE_ID:
				if (value == null) {
					unsetTreeId();
				} else {
					setTreeId((Long) value);
				}
				break;

			case NODE_ID:
				if (value == null) {
					unsetNodeId();
				} else {
					setNodeId((Integer) value);
				}
				break;

			}
		}

		public Object getFieldValue(_Fields field) {
			switch (field) {
			case TREE_ID:
				return Long.valueOf(getTreeId());

			case NODE_ID:
				return Integer.valueOf(getNodeId());

			}
			throw new IllegalStateException();
		}

		/**
		 * Returns true if field corresponding to fieldID is set (has been
		 * assigned a value) and false otherwise
		 */
		public boolean isSet(_Fields field) {
			if (field == null) {
				throw new IllegalArgumentException();
			}

			switch (field) {
			case TREE_ID:
				return isSetTreeId();
			case NODE_ID:
				return isSetNodeId();
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (that instanceof getSegmentHash_args)
				return this.equals((getSegmentHash_args) that);
			return false;
		}

		public boolean equals(getSegmentHash_args that) {
			if (that == null)
				return false;

			boolean this_present_treeId = true;
			boolean that_present_treeId = true;
			if (this_present_treeId || that_present_treeId) {
				if (!(this_present_treeId && that_present_treeId))
					return false;
				if (this.treeId != that.treeId)
					return false;
			}

			boolean this_present_nodeId = true;
			boolean that_present_nodeId = true;
			if (this_present_nodeId || that_present_nodeId) {
				if (!(this_present_nodeId && that_present_nodeId))
					return false;
				if (this.nodeId != that.nodeId)
					return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		public int compareTo(getSegmentHash_args other) {
			if (!getClass().equals(other.getClass())) {
				return getClass().getName().compareTo(
						other.getClass().getName());
			}

			int lastComparison = 0;
			getSegmentHash_args typedOther = (getSegmentHash_args) other;

			lastComparison = Boolean.valueOf(isSetTreeId()).compareTo(
					typedOther.isSetTreeId());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetTreeId()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.treeId, typedOther.treeId);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			lastComparison = Boolean.valueOf(isSetNodeId()).compareTo(
					typedOther.isSetNodeId());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetNodeId()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.nodeId, typedOther.nodeId);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			return 0;
		}

		public _Fields fieldForId(int fieldId) {
			return _Fields.findByThriftId(fieldId);
		}

		public void read(org.apache.thrift.protocol.TProtocol iprot)
				throws org.apache.thrift.TException {
			schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
		}

		public void write(org.apache.thrift.protocol.TProtocol oprot)
				throws org.apache.thrift.TException {
			schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("getSegmentHash_args(");
			boolean first = true;

			sb.append("treeId:");
			sb.append(this.treeId);
			first = false;
			if (!first)
				sb.append(", ");
			sb.append("nodeId:");
			sb.append(this.nodeId);
			first = false;
			sb.append(")");
			return sb.toString();
		}

		public void validate() throws org.apache.thrift.TException {
			// check for required fields
		}

		private void writeObject(java.io.ObjectOutputStream out)
				throws java.io.IOException {
			try {
				write(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(out)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private void readObject(java.io.ObjectInputStream in)
				throws java.io.IOException, ClassNotFoundException {
			try {
				read(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(in)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private static class getSegmentHash_argsStandardSchemeFactory implements
				SchemeFactory {
			public getSegmentHash_argsStandardScheme getScheme() {
				return new getSegmentHash_argsStandardScheme();
			}
		}

		private static class getSegmentHash_argsStandardScheme extends
				StandardScheme<getSegmentHash_args> {

			public void read(org.apache.thrift.protocol.TProtocol iprot,
					getSegmentHash_args struct)
					throws org.apache.thrift.TException {
				org.apache.thrift.protocol.TField schemeField;
				iprot.readStructBegin();
				while (true) {
					schemeField = iprot.readFieldBegin();
					if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
						break;
					}
					switch (schemeField.id) {
					case 1: // TREE_ID
						if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
							struct.treeId = iprot.readI64();
							struct.setTreeIdIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					case 2: // NODE_ID
						if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
							struct.nodeId = iprot.readI32();
							struct.setNodeIdIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					default:
						org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
								schemeField.type);
					}
					iprot.readFieldEnd();
				}
				iprot.readStructEnd();

				// check for required fields of primitive type, which can't be
				// checked in the validate method
				struct.validate();
			}

			public void write(org.apache.thrift.protocol.TProtocol oprot,
					getSegmentHash_args struct)
					throws org.apache.thrift.TException {
				struct.validate();

				oprot.writeStructBegin(STRUCT_DESC);
				oprot.writeFieldBegin(TREE_ID_FIELD_DESC);
				oprot.writeI64(struct.treeId);
				oprot.writeFieldEnd();
				oprot.writeFieldBegin(NODE_ID_FIELD_DESC);
				oprot.writeI32(struct.nodeId);
				oprot.writeFieldEnd();
				oprot.writeFieldStop();
				oprot.writeStructEnd();
			}

		}

		private static class getSegmentHash_argsTupleSchemeFactory implements
				SchemeFactory {
			public getSegmentHash_argsTupleScheme getScheme() {
				return new getSegmentHash_argsTupleScheme();
			}
		}

		private static class getSegmentHash_argsTupleScheme extends
				TupleScheme<getSegmentHash_args> {

			@Override
			public void write(org.apache.thrift.protocol.TProtocol prot,
					getSegmentHash_args struct)
					throws org.apache.thrift.TException {
				TTupleProtocol oprot = (TTupleProtocol) prot;
				BitSet optionals = new BitSet();
				if (struct.isSetTreeId()) {
					optionals.set(0);
				}
				if (struct.isSetNodeId()) {
					optionals.set(1);
				}
				oprot.writeBitSet(optionals, 2);
				if (struct.isSetTreeId()) {
					oprot.writeI64(struct.treeId);
				}
				if (struct.isSetNodeId()) {
					oprot.writeI32(struct.nodeId);
				}
			}

			@Override
			public void read(org.apache.thrift.protocol.TProtocol prot,
					getSegmentHash_args struct)
					throws org.apache.thrift.TException {
				TTupleProtocol iprot = (TTupleProtocol) prot;
				BitSet incoming = iprot.readBitSet(2);
				if (incoming.get(0)) {
					struct.treeId = iprot.readI64();
					struct.setTreeIdIsSet(true);
				}
				if (incoming.get(1)) {
					struct.nodeId = iprot.readI32();
					struct.setNodeIdIsSet(true);
				}
			}
		}

	}

	public static class getSegmentHash_result
			implements
			org.apache.thrift.TBase<getSegmentHash_result, getSegmentHash_result._Fields>,
			java.io.Serializable, Cloneable {
		private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
				"getSegmentHash_result");

		private static final org.apache.thrift.protocol.TField SUCCESS_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"success", org.apache.thrift.protocol.TType.STRUCT, (short) 0);

		private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
		static {
			schemes.put(StandardScheme.class,
					new getSegmentHash_resultStandardSchemeFactory());
			schemes.put(TupleScheme.class,
					new getSegmentHash_resultTupleSchemeFactory());
		}

		public SegmentHash success; // required

		/**
		 * The set of fields this struct contains, along with convenience
		 * methods for finding and manipulating them.
		 */
		public enum _Fields implements org.apache.thrift.TFieldIdEnum {
			SUCCESS((short) 0, "success");

			private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

			static {
				for (_Fields field : EnumSet.allOf(_Fields.class)) {
					byName.put(field.getFieldName(), field);
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, or null if its
			 * not found.
			 */
			public static _Fields findByThriftId(int fieldId) {
				switch (fieldId) {
				case 0: // SUCCESS
					return SUCCESS;
				default:
					return null;
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, throwing an
			 * exception if it is not found.
			 */
			public static _Fields findByThriftIdOrThrow(int fieldId) {
				_Fields fields = findByThriftId(fieldId);
				if (fields == null)
					throw new IllegalArgumentException("Field " + fieldId
							+ " doesn't exist!");
				return fields;
			}

			/**
			 * Find the _Fields constant that matches name, or null if its not
			 * found.
			 */
			public static _Fields findByName(String name) {
				return byName.get(name);
			}

			private final short _thriftId;
			private final String _fieldName;

			_Fields(short thriftId, String fieldName) {
				_thriftId = thriftId;
				_fieldName = fieldName;
			}

			public short getThriftFieldId() {
				return _thriftId;
			}

			public String getFieldName() {
				return _fieldName;
			}
		}

		// isset id assignments
		public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
		static {
			Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
					_Fields.class);
			tmpMap.put(_Fields.SUCCESS,
					new org.apache.thrift.meta_data.FieldMetaData("success",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.StructMetaData(
									org.apache.thrift.protocol.TType.STRUCT,
									SegmentHash.class)));
			metaDataMap = Collections.unmodifiableMap(tmpMap);
			org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
					getSegmentHash_result.class, metaDataMap);
		}

		public getSegmentHash_result() {
		}

		public getSegmentHash_result(SegmentHash success) {
			this();
			this.success = success;
		}

		/**
		 * Performs a deep copy on <i>other</i>.
		 */
		public getSegmentHash_result(getSegmentHash_result other) {
			if (other.isSetSuccess()) {
				this.success = new SegmentHash(other.success);
			}
		}

		public getSegmentHash_result deepCopy() {
			return new getSegmentHash_result(this);
		}

		@Override
		public void clear() {
			this.success = null;
		}

		public SegmentHash getSuccess() {
			return this.success;
		}

		public getSegmentHash_result setSuccess(SegmentHash success) {
			this.success = success;
			return this;
		}

		public void unsetSuccess() {
			this.success = null;
		}

		/**
		 * Returns true if field success is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetSuccess() {
			return this.success != null;
		}

		public void setSuccessIsSet(boolean value) {
			if (!value) {
				this.success = null;
			}
		}

		public void setFieldValue(_Fields field, Object value) {
			switch (field) {
			case SUCCESS:
				if (value == null) {
					unsetSuccess();
				} else {
					setSuccess((SegmentHash) value);
				}
				break;

			}
		}

		public Object getFieldValue(_Fields field) {
			switch (field) {
			case SUCCESS:
				return getSuccess();

			}
			throw new IllegalStateException();
		}

		/**
		 * Returns true if field corresponding to fieldID is set (has been
		 * assigned a value) and false otherwise
		 */
		public boolean isSet(_Fields field) {
			if (field == null) {
				throw new IllegalArgumentException();
			}

			switch (field) {
			case SUCCESS:
				return isSetSuccess();
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (that instanceof getSegmentHash_result)
				return this.equals((getSegmentHash_result) that);
			return false;
		}

		public boolean equals(getSegmentHash_result that) {
			if (that == null)
				return false;

			boolean this_present_success = true && this.isSetSuccess();
			boolean that_present_success = true && that.isSetSuccess();
			if (this_present_success || that_present_success) {
				if (!(this_present_success && that_present_success))
					return false;
				if (!this.success.equals(that.success))
					return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		public int compareTo(getSegmentHash_result other) {
			if (!getClass().equals(other.getClass())) {
				return getClass().getName().compareTo(
						other.getClass().getName());
			}

			int lastComparison = 0;
			getSegmentHash_result typedOther = (getSegmentHash_result) other;

			lastComparison = Boolean.valueOf(isSetSuccess()).compareTo(
					typedOther.isSetSuccess());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetSuccess()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.success, typedOther.success);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			return 0;
		}

		public _Fields fieldForId(int fieldId) {
			return _Fields.findByThriftId(fieldId);
		}

		public void read(org.apache.thrift.protocol.TProtocol iprot)
				throws org.apache.thrift.TException {
			schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
		}

		public void write(org.apache.thrift.protocol.TProtocol oprot)
				throws org.apache.thrift.TException {
			schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("getSegmentHash_result(");
			boolean first = true;

			sb.append("success:");
			if (this.success == null) {
				sb.append("null");
			} else {
				sb.append(this.success);
			}
			first = false;
			sb.append(")");
			return sb.toString();
		}

		public void validate() throws org.apache.thrift.TException {
			// check for required fields
		}

		private void writeObject(java.io.ObjectOutputStream out)
				throws java.io.IOException {
			try {
				write(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(out)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private void readObject(java.io.ObjectInputStream in)
				throws java.io.IOException, ClassNotFoundException {
			try {
				read(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(in)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private static class getSegmentHash_resultStandardSchemeFactory
				implements SchemeFactory {
			public getSegmentHash_resultStandardScheme getScheme() {
				return new getSegmentHash_resultStandardScheme();
			}
		}

		private static class getSegmentHash_resultStandardScheme extends
				StandardScheme<getSegmentHash_result> {

			public void read(org.apache.thrift.protocol.TProtocol iprot,
					getSegmentHash_result struct)
					throws org.apache.thrift.TException {
				org.apache.thrift.protocol.TField schemeField;
				iprot.readStructBegin();
				while (true) {
					schemeField = iprot.readFieldBegin();
					if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
						break;
					}
					switch (schemeField.id) {
					case 0: // SUCCESS
						if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
							struct.success = new SegmentHash();
							struct.success.read(iprot);
							struct.setSuccessIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					default:
						org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
								schemeField.type);
					}
					iprot.readFieldEnd();
				}
				iprot.readStructEnd();

				// check for required fields of primitive type, which can't be
				// checked in the validate method
				struct.validate();
			}

			public void write(org.apache.thrift.protocol.TProtocol oprot,
					getSegmentHash_result struct)
					throws org.apache.thrift.TException {
				struct.validate();

				oprot.writeStructBegin(STRUCT_DESC);
				if (struct.success != null) {
					oprot.writeFieldBegin(SUCCESS_FIELD_DESC);
					struct.success.write(oprot);
					oprot.writeFieldEnd();
				}
				oprot.writeFieldStop();
				oprot.writeStructEnd();
			}

		}

		private static class getSegmentHash_resultTupleSchemeFactory implements
				SchemeFactory {
			public getSegmentHash_resultTupleScheme getScheme() {
				return new getSegmentHash_resultTupleScheme();
			}
		}

		private static class getSegmentHash_resultTupleScheme extends
				TupleScheme<getSegmentHash_result> {

			@Override
			public void write(org.apache.thrift.protocol.TProtocol prot,
					getSegmentHash_result struct)
					throws org.apache.thrift.TException {
				TTupleProtocol oprot = (TTupleProtocol) prot;
				BitSet optionals = new BitSet();
				if (struct.isSetSuccess()) {
					optionals.set(0);
				}
				oprot.writeBitSet(optionals, 1);
				if (struct.isSetSuccess()) {
					struct.success.write(oprot);
				}
			}

			@Override
			public void read(org.apache.thrift.protocol.TProtocol prot,
					getSegmentHash_result struct)
					throws org.apache.thrift.TException {
				TTupleProtocol iprot = (TTupleProtocol) prot;
				BitSet incoming = iprot.readBitSet(1);
				if (incoming.get(0)) {
					struct.success = new SegmentHash();
					struct.success.read(iprot);
					struct.setSuccessIsSet(true);
				}
			}
		}

	}

	public static class getSegment_args implements
			org.apache.thrift.TBase<getSegment_args, getSegment_args._Fields>,
			java.io.Serializable, Cloneable {
		private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
				"getSegment_args");

		private static final org.apache.thrift.protocol.TField TREE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"treeId", org.apache.thrift.protocol.TType.I64, (short) 1);
		private static final org.apache.thrift.protocol.TField SEG_ID_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"segId", org.apache.thrift.protocol.TType.I32, (short) 2);

		private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
		static {
			schemes.put(StandardScheme.class,
					new getSegment_argsStandardSchemeFactory());
			schemes.put(TupleScheme.class,
					new getSegment_argsTupleSchemeFactory());
		}

		public long treeId; // required
		public int segId; // required

		/**
		 * The set of fields this struct contains, along with convenience
		 * methods for finding and manipulating them.
		 */
		public enum _Fields implements org.apache.thrift.TFieldIdEnum {
			TREE_ID((short) 1, "treeId"), SEG_ID((short) 2, "segId");

			private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

			static {
				for (_Fields field : EnumSet.allOf(_Fields.class)) {
					byName.put(field.getFieldName(), field);
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, or null if its
			 * not found.
			 */
			public static _Fields findByThriftId(int fieldId) {
				switch (fieldId) {
				case 1: // TREE_ID
					return TREE_ID;
				case 2: // SEG_ID
					return SEG_ID;
				default:
					return null;
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, throwing an
			 * exception if it is not found.
			 */
			public static _Fields findByThriftIdOrThrow(int fieldId) {
				_Fields fields = findByThriftId(fieldId);
				if (fields == null)
					throw new IllegalArgumentException("Field " + fieldId
							+ " doesn't exist!");
				return fields;
			}

			/**
			 * Find the _Fields constant that matches name, or null if its not
			 * found.
			 */
			public static _Fields findByName(String name) {
				return byName.get(name);
			}

			private final short _thriftId;
			private final String _fieldName;

			_Fields(short thriftId, String fieldName) {
				_thriftId = thriftId;
				_fieldName = fieldName;
			}

			public short getThriftFieldId() {
				return _thriftId;
			}

			public String getFieldName() {
				return _fieldName;
			}
		}

		// isset id assignments
		private static final int __TREEID_ISSET_ID = 0;
		private static final int __SEGID_ISSET_ID = 1;
		private BitSet __isset_bit_vector = new BitSet(2);
		public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
		static {
			Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
					_Fields.class);
			tmpMap.put(_Fields.TREE_ID,
					new org.apache.thrift.meta_data.FieldMetaData("treeId",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.FieldValueMetaData(
									org.apache.thrift.protocol.TType.I64)));
			tmpMap.put(_Fields.SEG_ID,
					new org.apache.thrift.meta_data.FieldMetaData("segId",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.FieldValueMetaData(
									org.apache.thrift.protocol.TType.I32)));
			metaDataMap = Collections.unmodifiableMap(tmpMap);
			org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
					getSegment_args.class, metaDataMap);
		}

		public getSegment_args() {
		}

		public getSegment_args(long treeId, int segId) {
			this();
			this.treeId = treeId;
			setTreeIdIsSet(true);
			this.segId = segId;
			setSegIdIsSet(true);
		}

		/**
		 * Performs a deep copy on <i>other</i>.
		 */
		public getSegment_args(getSegment_args other) {
			__isset_bit_vector.clear();
			__isset_bit_vector.or(other.__isset_bit_vector);
			this.treeId = other.treeId;
			this.segId = other.segId;
		}

		public getSegment_args deepCopy() {
			return new getSegment_args(this);
		}

		@Override
		public void clear() {
			setTreeIdIsSet(false);
			this.treeId = 0;
			setSegIdIsSet(false);
			this.segId = 0;
		}

		public long getTreeId() {
			return this.treeId;
		}

		public getSegment_args setTreeId(long treeId) {
			this.treeId = treeId;
			setTreeIdIsSet(true);
			return this;
		}

		public void unsetTreeId() {
			__isset_bit_vector.clear(__TREEID_ISSET_ID);
		}

		/**
		 * Returns true if field treeId is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetTreeId() {
			return __isset_bit_vector.get(__TREEID_ISSET_ID);
		}

		public void setTreeIdIsSet(boolean value) {
			__isset_bit_vector.set(__TREEID_ISSET_ID, value);
		}

		public int getSegId() {
			return this.segId;
		}

		public getSegment_args setSegId(int segId) {
			this.segId = segId;
			setSegIdIsSet(true);
			return this;
		}

		public void unsetSegId() {
			__isset_bit_vector.clear(__SEGID_ISSET_ID);
		}

		/**
		 * Returns true if field segId is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetSegId() {
			return __isset_bit_vector.get(__SEGID_ISSET_ID);
		}

		public void setSegIdIsSet(boolean value) {
			__isset_bit_vector.set(__SEGID_ISSET_ID, value);
		}

		public void setFieldValue(_Fields field, Object value) {
			switch (field) {
			case TREE_ID:
				if (value == null) {
					unsetTreeId();
				} else {
					setTreeId((Long) value);
				}
				break;

			case SEG_ID:
				if (value == null) {
					unsetSegId();
				} else {
					setSegId((Integer) value);
				}
				break;

			}
		}

		public Object getFieldValue(_Fields field) {
			switch (field) {
			case TREE_ID:
				return Long.valueOf(getTreeId());

			case SEG_ID:
				return Integer.valueOf(getSegId());

			}
			throw new IllegalStateException();
		}

		/**
		 * Returns true if field corresponding to fieldID is set (has been
		 * assigned a value) and false otherwise
		 */
		public boolean isSet(_Fields field) {
			if (field == null) {
				throw new IllegalArgumentException();
			}

			switch (field) {
			case TREE_ID:
				return isSetTreeId();
			case SEG_ID:
				return isSetSegId();
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (that instanceof getSegment_args)
				return this.equals((getSegment_args) that);
			return false;
		}

		public boolean equals(getSegment_args that) {
			if (that == null)
				return false;

			boolean this_present_treeId = true;
			boolean that_present_treeId = true;
			if (this_present_treeId || that_present_treeId) {
				if (!(this_present_treeId && that_present_treeId))
					return false;
				if (this.treeId != that.treeId)
					return false;
			}

			boolean this_present_segId = true;
			boolean that_present_segId = true;
			if (this_present_segId || that_present_segId) {
				if (!(this_present_segId && that_present_segId))
					return false;
				if (this.segId != that.segId)
					return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		public int compareTo(getSegment_args other) {
			if (!getClass().equals(other.getClass())) {
				return getClass().getName().compareTo(
						other.getClass().getName());
			}

			int lastComparison = 0;
			getSegment_args typedOther = (getSegment_args) other;

			lastComparison = Boolean.valueOf(isSetTreeId()).compareTo(
					typedOther.isSetTreeId());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetTreeId()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.treeId, typedOther.treeId);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			lastComparison = Boolean.valueOf(isSetSegId()).compareTo(
					typedOther.isSetSegId());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetSegId()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.segId, typedOther.segId);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			return 0;
		}

		public _Fields fieldForId(int fieldId) {
			return _Fields.findByThriftId(fieldId);
		}

		public void read(org.apache.thrift.protocol.TProtocol iprot)
				throws org.apache.thrift.TException {
			schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
		}

		public void write(org.apache.thrift.protocol.TProtocol oprot)
				throws org.apache.thrift.TException {
			schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("getSegment_args(");
			boolean first = true;

			sb.append("treeId:");
			sb.append(this.treeId);
			first = false;
			if (!first)
				sb.append(", ");
			sb.append("segId:");
			sb.append(this.segId);
			first = false;
			sb.append(")");
			return sb.toString();
		}

		public void validate() throws org.apache.thrift.TException {
			// check for required fields
		}

		private void writeObject(java.io.ObjectOutputStream out)
				throws java.io.IOException {
			try {
				write(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(out)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private void readObject(java.io.ObjectInputStream in)
				throws java.io.IOException, ClassNotFoundException {
			try {
				read(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(in)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private static class getSegment_argsStandardSchemeFactory implements
				SchemeFactory {
			public getSegment_argsStandardScheme getScheme() {
				return new getSegment_argsStandardScheme();
			}
		}

		private static class getSegment_argsStandardScheme extends
				StandardScheme<getSegment_args> {

			public void read(org.apache.thrift.protocol.TProtocol iprot,
					getSegment_args struct) throws org.apache.thrift.TException {
				org.apache.thrift.protocol.TField schemeField;
				iprot.readStructBegin();
				while (true) {
					schemeField = iprot.readFieldBegin();
					if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
						break;
					}
					switch (schemeField.id) {
					case 1: // TREE_ID
						if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
							struct.treeId = iprot.readI64();
							struct.setTreeIdIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					case 2: // SEG_ID
						if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
							struct.segId = iprot.readI32();
							struct.setSegIdIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					default:
						org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
								schemeField.type);
					}
					iprot.readFieldEnd();
				}
				iprot.readStructEnd();

				// check for required fields of primitive type, which can't be
				// checked in the validate method
				struct.validate();
			}

			public void write(org.apache.thrift.protocol.TProtocol oprot,
					getSegment_args struct) throws org.apache.thrift.TException {
				struct.validate();

				oprot.writeStructBegin(STRUCT_DESC);
				oprot.writeFieldBegin(TREE_ID_FIELD_DESC);
				oprot.writeI64(struct.treeId);
				oprot.writeFieldEnd();
				oprot.writeFieldBegin(SEG_ID_FIELD_DESC);
				oprot.writeI32(struct.segId);
				oprot.writeFieldEnd();
				oprot.writeFieldStop();
				oprot.writeStructEnd();
			}

		}

		private static class getSegment_argsTupleSchemeFactory implements
				SchemeFactory {
			public getSegment_argsTupleScheme getScheme() {
				return new getSegment_argsTupleScheme();
			}
		}

		private static class getSegment_argsTupleScheme extends
				TupleScheme<getSegment_args> {

			@Override
			public void write(org.apache.thrift.protocol.TProtocol prot,
					getSegment_args struct) throws org.apache.thrift.TException {
				TTupleProtocol oprot = (TTupleProtocol) prot;
				BitSet optionals = new BitSet();
				if (struct.isSetTreeId()) {
					optionals.set(0);
				}
				if (struct.isSetSegId()) {
					optionals.set(1);
				}
				oprot.writeBitSet(optionals, 2);
				if (struct.isSetTreeId()) {
					oprot.writeI64(struct.treeId);
				}
				if (struct.isSetSegId()) {
					oprot.writeI32(struct.segId);
				}
			}

			@Override
			public void read(org.apache.thrift.protocol.TProtocol prot,
					getSegment_args struct) throws org.apache.thrift.TException {
				TTupleProtocol iprot = (TTupleProtocol) prot;
				BitSet incoming = iprot.readBitSet(2);
				if (incoming.get(0)) {
					struct.treeId = iprot.readI64();
					struct.setTreeIdIsSet(true);
				}
				if (incoming.get(1)) {
					struct.segId = iprot.readI32();
					struct.setSegIdIsSet(true);
				}
			}
		}

	}

	public static class getSegment_result
			implements
			org.apache.thrift.TBase<getSegment_result, getSegment_result._Fields>,
			java.io.Serializable, Cloneable {
		private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
				"getSegment_result");

		private static final org.apache.thrift.protocol.TField SUCCESS_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"success", org.apache.thrift.protocol.TType.LIST, (short) 0);

		private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
		static {
			schemes.put(StandardScheme.class,
					new getSegment_resultStandardSchemeFactory());
			schemes.put(TupleScheme.class,
					new getSegment_resultTupleSchemeFactory());
		}

		public List<SegmentData> success; // required

		/**
		 * The set of fields this struct contains, along with convenience
		 * methods for finding and manipulating them.
		 */
		public enum _Fields implements org.apache.thrift.TFieldIdEnum {
			SUCCESS((short) 0, "success");

			private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

			static {
				for (_Fields field : EnumSet.allOf(_Fields.class)) {
					byName.put(field.getFieldName(), field);
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, or null if its
			 * not found.
			 */
			public static _Fields findByThriftId(int fieldId) {
				switch (fieldId) {
				case 0: // SUCCESS
					return SUCCESS;
				default:
					return null;
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, throwing an
			 * exception if it is not found.
			 */
			public static _Fields findByThriftIdOrThrow(int fieldId) {
				_Fields fields = findByThriftId(fieldId);
				if (fields == null)
					throw new IllegalArgumentException("Field " + fieldId
							+ " doesn't exist!");
				return fields;
			}

			/**
			 * Find the _Fields constant that matches name, or null if its not
			 * found.
			 */
			public static _Fields findByName(String name) {
				return byName.get(name);
			}

			private final short _thriftId;
			private final String _fieldName;

			_Fields(short thriftId, String fieldName) {
				_thriftId = thriftId;
				_fieldName = fieldName;
			}

			public short getThriftFieldId() {
				return _thriftId;
			}

			public String getFieldName() {
				return _fieldName;
			}
		}

		// isset id assignments
		public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
		static {
			Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
					_Fields.class);
			tmpMap.put(
					_Fields.SUCCESS,
					new org.apache.thrift.meta_data.FieldMetaData(
							"success",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.ListMetaData(
									org.apache.thrift.protocol.TType.LIST,
									new org.apache.thrift.meta_data.StructMetaData(
											org.apache.thrift.protocol.TType.STRUCT,
											SegmentData.class))));
			metaDataMap = Collections.unmodifiableMap(tmpMap);
			org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
					getSegment_result.class, metaDataMap);
		}

		public getSegment_result() {
		}

		public getSegment_result(List<SegmentData> success) {
			this();
			this.success = success;
		}

		/**
		 * Performs a deep copy on <i>other</i>.
		 */
		public getSegment_result(getSegment_result other) {
			if (other.isSetSuccess()) {
				List<SegmentData> __this__success = new ArrayList<SegmentData>();
				for (SegmentData other_element : other.success) {
					__this__success.add(new SegmentData(other_element));
				}
				this.success = __this__success;
			}
		}

		public getSegment_result deepCopy() {
			return new getSegment_result(this);
		}

		@Override
		public void clear() {
			this.success = null;
		}

		public int getSuccessSize() {
			return (this.success == null) ? 0 : this.success.size();
		}

		public java.util.Iterator<SegmentData> getSuccessIterator() {
			return (this.success == null) ? null : this.success.iterator();
		}

		public void addToSuccess(SegmentData elem) {
			if (this.success == null) {
				this.success = new ArrayList<SegmentData>();
			}
			this.success.add(elem);
		}

		public List<SegmentData> getSuccess() {
			return this.success;
		}

		public getSegment_result setSuccess(List<SegmentData> success) {
			this.success = success;
			return this;
		}

		public void unsetSuccess() {
			this.success = null;
		}

		/**
		 * Returns true if field success is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetSuccess() {
			return this.success != null;
		}

		public void setSuccessIsSet(boolean value) {
			if (!value) {
				this.success = null;
			}
		}

		public void setFieldValue(_Fields field, Object value) {
			switch (field) {
			case SUCCESS:
				if (value == null) {
					unsetSuccess();
				} else {
					setSuccess((List<SegmentData>) value);
				}
				break;

			}
		}

		public Object getFieldValue(_Fields field) {
			switch (field) {
			case SUCCESS:
				return getSuccess();

			}
			throw new IllegalStateException();
		}

		/**
		 * Returns true if field corresponding to fieldID is set (has been
		 * assigned a value) and false otherwise
		 */
		public boolean isSet(_Fields field) {
			if (field == null) {
				throw new IllegalArgumentException();
			}

			switch (field) {
			case SUCCESS:
				return isSetSuccess();
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (that instanceof getSegment_result)
				return this.equals((getSegment_result) that);
			return false;
		}

		public boolean equals(getSegment_result that) {
			if (that == null)
				return false;

			boolean this_present_success = true && this.isSetSuccess();
			boolean that_present_success = true && that.isSetSuccess();
			if (this_present_success || that_present_success) {
				if (!(this_present_success && that_present_success))
					return false;
				if (!this.success.equals(that.success))
					return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		public int compareTo(getSegment_result other) {
			if (!getClass().equals(other.getClass())) {
				return getClass().getName().compareTo(
						other.getClass().getName());
			}

			int lastComparison = 0;
			getSegment_result typedOther = (getSegment_result) other;

			lastComparison = Boolean.valueOf(isSetSuccess()).compareTo(
					typedOther.isSetSuccess());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetSuccess()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.success, typedOther.success);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			return 0;
		}

		public _Fields fieldForId(int fieldId) {
			return _Fields.findByThriftId(fieldId);
		}

		public void read(org.apache.thrift.protocol.TProtocol iprot)
				throws org.apache.thrift.TException {
			schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
		}

		public void write(org.apache.thrift.protocol.TProtocol oprot)
				throws org.apache.thrift.TException {
			schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("getSegment_result(");
			boolean first = true;

			sb.append("success:");
			if (this.success == null) {
				sb.append("null");
			} else {
				sb.append(this.success);
			}
			first = false;
			sb.append(")");
			return sb.toString();
		}

		public void validate() throws org.apache.thrift.TException {
			// check for required fields
		}

		private void writeObject(java.io.ObjectOutputStream out)
				throws java.io.IOException {
			try {
				write(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(out)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private void readObject(java.io.ObjectInputStream in)
				throws java.io.IOException, ClassNotFoundException {
			try {
				read(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(in)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private static class getSegment_resultStandardSchemeFactory implements
				SchemeFactory {
			public getSegment_resultStandardScheme getScheme() {
				return new getSegment_resultStandardScheme();
			}
		}

		private static class getSegment_resultStandardScheme extends
				StandardScheme<getSegment_result> {

			public void read(org.apache.thrift.protocol.TProtocol iprot,
					getSegment_result struct)
					throws org.apache.thrift.TException {
				org.apache.thrift.protocol.TField schemeField;
				iprot.readStructBegin();
				while (true) {
					schemeField = iprot.readFieldBegin();
					if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
						break;
					}
					switch (schemeField.id) {
					case 0: // SUCCESS
						if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
							{
								org.apache.thrift.protocol.TList _list34 = iprot
										.readListBegin();
								struct.success = new ArrayList<SegmentData>(
										_list34.size);
								for (int _i35 = 0; _i35 < _list34.size; ++_i35) {
									SegmentData _elem36; // required
									_elem36 = new SegmentData();
									_elem36.read(iprot);
									struct.success.add(_elem36);
								}
								iprot.readListEnd();
							}
							struct.setSuccessIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					default:
						org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
								schemeField.type);
					}
					iprot.readFieldEnd();
				}
				iprot.readStructEnd();

				// check for required fields of primitive type, which can't be
				// checked in the validate method
				struct.validate();
			}

			public void write(org.apache.thrift.protocol.TProtocol oprot,
					getSegment_result struct)
					throws org.apache.thrift.TException {
				struct.validate();

				oprot.writeStructBegin(STRUCT_DESC);
				if (struct.success != null) {
					oprot.writeFieldBegin(SUCCESS_FIELD_DESC);
					{
						oprot.writeListBegin(new org.apache.thrift.protocol.TList(
								org.apache.thrift.protocol.TType.STRUCT,
								struct.success.size()));
						for (SegmentData _iter37 : struct.success) {
							_iter37.write(oprot);
						}
						oprot.writeListEnd();
					}
					oprot.writeFieldEnd();
				}
				oprot.writeFieldStop();
				oprot.writeStructEnd();
			}

		}

		private static class getSegment_resultTupleSchemeFactory implements
				SchemeFactory {
			public getSegment_resultTupleScheme getScheme() {
				return new getSegment_resultTupleScheme();
			}
		}

		private static class getSegment_resultTupleScheme extends
				TupleScheme<getSegment_result> {

			@Override
			public void write(org.apache.thrift.protocol.TProtocol prot,
					getSegment_result struct)
					throws org.apache.thrift.TException {
				TTupleProtocol oprot = (TTupleProtocol) prot;
				BitSet optionals = new BitSet();
				if (struct.isSetSuccess()) {
					optionals.set(0);
				}
				oprot.writeBitSet(optionals, 1);
				if (struct.isSetSuccess()) {
					{
						oprot.writeI32(struct.success.size());
						for (SegmentData _iter38 : struct.success) {
							_iter38.write(oprot);
						}
					}
				}
			}

			@Override
			public void read(org.apache.thrift.protocol.TProtocol prot,
					getSegment_result struct)
					throws org.apache.thrift.TException {
				TTupleProtocol iprot = (TTupleProtocol) prot;
				BitSet incoming = iprot.readBitSet(1);
				if (incoming.get(0)) {
					{
						org.apache.thrift.protocol.TList _list39 = new org.apache.thrift.protocol.TList(
								org.apache.thrift.protocol.TType.STRUCT,
								iprot.readI32());
						struct.success = new ArrayList<SegmentData>(
								_list39.size);
						for (int _i40 = 0; _i40 < _list39.size; ++_i40) {
							SegmentData _elem41; // required
							_elem41 = new SegmentData();
							_elem41.read(iprot);
							struct.success.add(_elem41);
						}
					}
					struct.setSuccessIsSet(true);
				}
			}
		}

	}

	public static class getSegmentData_args
			implements
			org.apache.thrift.TBase<getSegmentData_args, getSegmentData_args._Fields>,
			java.io.Serializable, Cloneable {
		private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
				"getSegmentData_args");

		private static final org.apache.thrift.protocol.TField TREE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"treeId", org.apache.thrift.protocol.TType.I64, (short) 1);
		private static final org.apache.thrift.protocol.TField SEG_ID_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"segId", org.apache.thrift.protocol.TType.I32, (short) 2);
		private static final org.apache.thrift.protocol.TField KEY_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"key", org.apache.thrift.protocol.TType.STRING, (short) 3);

		private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
		static {
			schemes.put(StandardScheme.class,
					new getSegmentData_argsStandardSchemeFactory());
			schemes.put(TupleScheme.class,
					new getSegmentData_argsTupleSchemeFactory());
		}

		public long treeId; // required
		public int segId; // required
		public ByteBuffer key; // required

		/**
		 * The set of fields this struct contains, along with convenience
		 * methods for finding and manipulating them.
		 */
		public enum _Fields implements org.apache.thrift.TFieldIdEnum {
			TREE_ID((short) 1, "treeId"), SEG_ID((short) 2, "segId"), KEY(
					(short) 3, "key");

			private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

			static {
				for (_Fields field : EnumSet.allOf(_Fields.class)) {
					byName.put(field.getFieldName(), field);
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, or null if its
			 * not found.
			 */
			public static _Fields findByThriftId(int fieldId) {
				switch (fieldId) {
				case 1: // TREE_ID
					return TREE_ID;
				case 2: // SEG_ID
					return SEG_ID;
				case 3: // KEY
					return KEY;
				default:
					return null;
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, throwing an
			 * exception if it is not found.
			 */
			public static _Fields findByThriftIdOrThrow(int fieldId) {
				_Fields fields = findByThriftId(fieldId);
				if (fields == null)
					throw new IllegalArgumentException("Field " + fieldId
							+ " doesn't exist!");
				return fields;
			}

			/**
			 * Find the _Fields constant that matches name, or null if its not
			 * found.
			 */
			public static _Fields findByName(String name) {
				return byName.get(name);
			}

			private final short _thriftId;
			private final String _fieldName;

			_Fields(short thriftId, String fieldName) {
				_thriftId = thriftId;
				_fieldName = fieldName;
			}

			public short getThriftFieldId() {
				return _thriftId;
			}

			public String getFieldName() {
				return _fieldName;
			}
		}

		// isset id assignments
		private static final int __TREEID_ISSET_ID = 0;
		private static final int __SEGID_ISSET_ID = 1;
		private BitSet __isset_bit_vector = new BitSet(2);
		public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
		static {
			Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
					_Fields.class);
			tmpMap.put(_Fields.TREE_ID,
					new org.apache.thrift.meta_data.FieldMetaData("treeId",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.FieldValueMetaData(
									org.apache.thrift.protocol.TType.I64)));
			tmpMap.put(_Fields.SEG_ID,
					new org.apache.thrift.meta_data.FieldMetaData("segId",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.FieldValueMetaData(
									org.apache.thrift.protocol.TType.I32)));
			tmpMap.put(_Fields.KEY,
					new org.apache.thrift.meta_data.FieldMetaData("key",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.FieldValueMetaData(
									org.apache.thrift.protocol.TType.STRING,
									true)));
			metaDataMap = Collections.unmodifiableMap(tmpMap);
			org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
					getSegmentData_args.class, metaDataMap);
		}

		public getSegmentData_args() {
		}

		public getSegmentData_args(long treeId, int segId, ByteBuffer key) {
			this();
			this.treeId = treeId;
			setTreeIdIsSet(true);
			this.segId = segId;
			setSegIdIsSet(true);
			this.key = key;
		}

		/**
		 * Performs a deep copy on <i>other</i>.
		 */
		public getSegmentData_args(getSegmentData_args other) {
			__isset_bit_vector.clear();
			__isset_bit_vector.or(other.__isset_bit_vector);
			this.treeId = other.treeId;
			this.segId = other.segId;
			if (other.isSetKey()) {
				this.key = org.apache.thrift.TBaseHelper.copyBinary(other.key);
				;
			}
		}

		public getSegmentData_args deepCopy() {
			return new getSegmentData_args(this);
		}

		@Override
		public void clear() {
			setTreeIdIsSet(false);
			this.treeId = 0;
			setSegIdIsSet(false);
			this.segId = 0;
			this.key = null;
		}

		public long getTreeId() {
			return this.treeId;
		}

		public getSegmentData_args setTreeId(long treeId) {
			this.treeId = treeId;
			setTreeIdIsSet(true);
			return this;
		}

		public void unsetTreeId() {
			__isset_bit_vector.clear(__TREEID_ISSET_ID);
		}

		/**
		 * Returns true if field treeId is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetTreeId() {
			return __isset_bit_vector.get(__TREEID_ISSET_ID);
		}

		public void setTreeIdIsSet(boolean value) {
			__isset_bit_vector.set(__TREEID_ISSET_ID, value);
		}

		public int getSegId() {
			return this.segId;
		}

		public getSegmentData_args setSegId(int segId) {
			this.segId = segId;
			setSegIdIsSet(true);
			return this;
		}

		public void unsetSegId() {
			__isset_bit_vector.clear(__SEGID_ISSET_ID);
		}

		/**
		 * Returns true if field segId is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetSegId() {
			return __isset_bit_vector.get(__SEGID_ISSET_ID);
		}

		public void setSegIdIsSet(boolean value) {
			__isset_bit_vector.set(__SEGID_ISSET_ID, value);
		}

		public byte[] getKey() {
			setKey(org.apache.thrift.TBaseHelper.rightSize(key));
			return key == null ? null : key.array();
		}

		public ByteBuffer bufferForKey() {
			return key;
		}

		public getSegmentData_args setKey(byte[] key) {
			setKey(key == null ? (ByteBuffer) null : ByteBuffer.wrap(key));
			return this;
		}

		public getSegmentData_args setKey(ByteBuffer key) {
			this.key = key;
			return this;
		}

		public void unsetKey() {
			this.key = null;
		}

		/**
		 * Returns true if field key is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetKey() {
			return this.key != null;
		}

		public void setKeyIsSet(boolean value) {
			if (!value) {
				this.key = null;
			}
		}

		public void setFieldValue(_Fields field, Object value) {
			switch (field) {
			case TREE_ID:
				if (value == null) {
					unsetTreeId();
				} else {
					setTreeId((Long) value);
				}
				break;

			case SEG_ID:
				if (value == null) {
					unsetSegId();
				} else {
					setSegId((Integer) value);
				}
				break;

			case KEY:
				if (value == null) {
					unsetKey();
				} else {
					setKey((ByteBuffer) value);
				}
				break;

			}
		}

		public Object getFieldValue(_Fields field) {
			switch (field) {
			case TREE_ID:
				return Long.valueOf(getTreeId());

			case SEG_ID:
				return Integer.valueOf(getSegId());

			case KEY:
				return getKey();

			}
			throw new IllegalStateException();
		}

		/**
		 * Returns true if field corresponding to fieldID is set (has been
		 * assigned a value) and false otherwise
		 */
		public boolean isSet(_Fields field) {
			if (field == null) {
				throw new IllegalArgumentException();
			}

			switch (field) {
			case TREE_ID:
				return isSetTreeId();
			case SEG_ID:
				return isSetSegId();
			case KEY:
				return isSetKey();
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (that instanceof getSegmentData_args)
				return this.equals((getSegmentData_args) that);
			return false;
		}

		public boolean equals(getSegmentData_args that) {
			if (that == null)
				return false;

			boolean this_present_treeId = true;
			boolean that_present_treeId = true;
			if (this_present_treeId || that_present_treeId) {
				if (!(this_present_treeId && that_present_treeId))
					return false;
				if (this.treeId != that.treeId)
					return false;
			}

			boolean this_present_segId = true;
			boolean that_present_segId = true;
			if (this_present_segId || that_present_segId) {
				if (!(this_present_segId && that_present_segId))
					return false;
				if (this.segId != that.segId)
					return false;
			}

			boolean this_present_key = true && this.isSetKey();
			boolean that_present_key = true && that.isSetKey();
			if (this_present_key || that_present_key) {
				if (!(this_present_key && that_present_key))
					return false;
				if (!this.key.equals(that.key))
					return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		public int compareTo(getSegmentData_args other) {
			if (!getClass().equals(other.getClass())) {
				return getClass().getName().compareTo(
						other.getClass().getName());
			}

			int lastComparison = 0;
			getSegmentData_args typedOther = (getSegmentData_args) other;

			lastComparison = Boolean.valueOf(isSetTreeId()).compareTo(
					typedOther.isSetTreeId());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetTreeId()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.treeId, typedOther.treeId);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			lastComparison = Boolean.valueOf(isSetSegId()).compareTo(
					typedOther.isSetSegId());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetSegId()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.segId, typedOther.segId);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			lastComparison = Boolean.valueOf(isSetKey()).compareTo(
					typedOther.isSetKey());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetKey()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.key, typedOther.key);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			return 0;
		}

		public _Fields fieldForId(int fieldId) {
			return _Fields.findByThriftId(fieldId);
		}

		public void read(org.apache.thrift.protocol.TProtocol iprot)
				throws org.apache.thrift.TException {
			schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
		}

		public void write(org.apache.thrift.protocol.TProtocol oprot)
				throws org.apache.thrift.TException {
			schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("getSegmentData_args(");
			boolean first = true;

			sb.append("treeId:");
			sb.append(this.treeId);
			first = false;
			if (!first)
				sb.append(", ");
			sb.append("segId:");
			sb.append(this.segId);
			first = false;
			if (!first)
				sb.append(", ");
			sb.append("key:");
			if (this.key == null) {
				sb.append("null");
			} else {
				org.apache.thrift.TBaseHelper.toString(this.key, sb);
			}
			first = false;
			sb.append(")");
			return sb.toString();
		}

		public void validate() throws org.apache.thrift.TException {
			// check for required fields
		}

		private void writeObject(java.io.ObjectOutputStream out)
				throws java.io.IOException {
			try {
				write(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(out)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private void readObject(java.io.ObjectInputStream in)
				throws java.io.IOException, ClassNotFoundException {
			try {
				// it doesn't seem like you should have to do this, but java
				// serialization is wacky, and doesn't call the default
				// constructor.
				__isset_bit_vector = new BitSet(1);
				read(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(in)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private static class getSegmentData_argsStandardSchemeFactory implements
				SchemeFactory {
			public getSegmentData_argsStandardScheme getScheme() {
				return new getSegmentData_argsStandardScheme();
			}
		}

		private static class getSegmentData_argsStandardScheme extends
				StandardScheme<getSegmentData_args> {

			public void read(org.apache.thrift.protocol.TProtocol iprot,
					getSegmentData_args struct)
					throws org.apache.thrift.TException {
				org.apache.thrift.protocol.TField schemeField;
				iprot.readStructBegin();
				while (true) {
					schemeField = iprot.readFieldBegin();
					if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
						break;
					}
					switch (schemeField.id) {
					case 1: // TREE_ID
						if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
							struct.treeId = iprot.readI64();
							struct.setTreeIdIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					case 2: // SEG_ID
						if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
							struct.segId = iprot.readI32();
							struct.setSegIdIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					case 3: // KEY
						if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
							struct.key = iprot.readBinary();
							struct.setKeyIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					default:
						org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
								schemeField.type);
					}
					iprot.readFieldEnd();
				}
				iprot.readStructEnd();

				// check for required fields of primitive type, which can't be
				// checked in the validate method
				struct.validate();
			}

			public void write(org.apache.thrift.protocol.TProtocol oprot,
					getSegmentData_args struct)
					throws org.apache.thrift.TException {
				struct.validate();

				oprot.writeStructBegin(STRUCT_DESC);
				oprot.writeFieldBegin(TREE_ID_FIELD_DESC);
				oprot.writeI64(struct.treeId);
				oprot.writeFieldEnd();
				oprot.writeFieldBegin(SEG_ID_FIELD_DESC);
				oprot.writeI32(struct.segId);
				oprot.writeFieldEnd();
				if (struct.key != null) {
					oprot.writeFieldBegin(KEY_FIELD_DESC);
					oprot.writeBinary(struct.key);
					oprot.writeFieldEnd();
				}
				oprot.writeFieldStop();
				oprot.writeStructEnd();
			}

		}

		private static class getSegmentData_argsTupleSchemeFactory implements
				SchemeFactory {
			public getSegmentData_argsTupleScheme getScheme() {
				return new getSegmentData_argsTupleScheme();
			}
		}

		private static class getSegmentData_argsTupleScheme extends
				TupleScheme<getSegmentData_args> {

			@Override
			public void write(org.apache.thrift.protocol.TProtocol prot,
					getSegmentData_args struct)
					throws org.apache.thrift.TException {
				TTupleProtocol oprot = (TTupleProtocol) prot;
				BitSet optionals = new BitSet();
				if (struct.isSetTreeId()) {
					optionals.set(0);
				}
				if (struct.isSetSegId()) {
					optionals.set(1);
				}
				if (struct.isSetKey()) {
					optionals.set(2);
				}
				oprot.writeBitSet(optionals, 3);
				if (struct.isSetTreeId()) {
					oprot.writeI64(struct.treeId);
				}
				if (struct.isSetSegId()) {
					oprot.writeI32(struct.segId);
				}
				if (struct.isSetKey()) {
					oprot.writeBinary(struct.key);
				}
			}

			@Override
			public void read(org.apache.thrift.protocol.TProtocol prot,
					getSegmentData_args struct)
					throws org.apache.thrift.TException {
				TTupleProtocol iprot = (TTupleProtocol) prot;
				BitSet incoming = iprot.readBitSet(3);
				if (incoming.get(0)) {
					struct.treeId = iprot.readI64();
					struct.setTreeIdIsSet(true);
				}
				if (incoming.get(1)) {
					struct.segId = iprot.readI32();
					struct.setSegIdIsSet(true);
				}
				if (incoming.get(2)) {
					struct.key = iprot.readBinary();
					struct.setKeyIsSet(true);
				}
			}
		}

	}

	public static class getSegmentData_result
			implements
			org.apache.thrift.TBase<getSegmentData_result, getSegmentData_result._Fields>,
			java.io.Serializable, Cloneable {
		private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
				"getSegmentData_result");

		private static final org.apache.thrift.protocol.TField SUCCESS_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"success", org.apache.thrift.protocol.TType.STRUCT, (short) 0);

		private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
		static {
			schemes.put(StandardScheme.class,
					new getSegmentData_resultStandardSchemeFactory());
			schemes.put(TupleScheme.class,
					new getSegmentData_resultTupleSchemeFactory());
		}

		public SegmentData success; // required

		/**
		 * The set of fields this struct contains, along with convenience
		 * methods for finding and manipulating them.
		 */
		public enum _Fields implements org.apache.thrift.TFieldIdEnum {
			SUCCESS((short) 0, "success");

			private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

			static {
				for (_Fields field : EnumSet.allOf(_Fields.class)) {
					byName.put(field.getFieldName(), field);
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, or null if its
			 * not found.
			 */
			public static _Fields findByThriftId(int fieldId) {
				switch (fieldId) {
				case 0: // SUCCESS
					return SUCCESS;
				default:
					return null;
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, throwing an
			 * exception if it is not found.
			 */
			public static _Fields findByThriftIdOrThrow(int fieldId) {
				_Fields fields = findByThriftId(fieldId);
				if (fields == null)
					throw new IllegalArgumentException("Field " + fieldId
							+ " doesn't exist!");
				return fields;
			}

			/**
			 * Find the _Fields constant that matches name, or null if its not
			 * found.
			 */
			public static _Fields findByName(String name) {
				return byName.get(name);
			}

			private final short _thriftId;
			private final String _fieldName;

			_Fields(short thriftId, String fieldName) {
				_thriftId = thriftId;
				_fieldName = fieldName;
			}

			public short getThriftFieldId() {
				return _thriftId;
			}

			public String getFieldName() {
				return _fieldName;
			}
		}

		// isset id assignments
		public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
		static {
			Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
					_Fields.class);
			tmpMap.put(_Fields.SUCCESS,
					new org.apache.thrift.meta_data.FieldMetaData("success",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.StructMetaData(
									org.apache.thrift.protocol.TType.STRUCT,
									SegmentData.class)));
			metaDataMap = Collections.unmodifiableMap(tmpMap);
			org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
					getSegmentData_result.class, metaDataMap);
		}

		public getSegmentData_result() {
		}

		public getSegmentData_result(SegmentData success) {
			this();
			this.success = success;
		}

		/**
		 * Performs a deep copy on <i>other</i>.
		 */
		public getSegmentData_result(getSegmentData_result other) {
			if (other.isSetSuccess()) {
				this.success = new SegmentData(other.success);
			}
		}

		public getSegmentData_result deepCopy() {
			return new getSegmentData_result(this);
		}

		@Override
		public void clear() {
			this.success = null;
		}

		public SegmentData getSuccess() {
			return this.success;
		}

		public getSegmentData_result setSuccess(SegmentData success) {
			this.success = success;
			return this;
		}

		public void unsetSuccess() {
			this.success = null;
		}

		/**
		 * Returns true if field success is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetSuccess() {
			return this.success != null;
		}

		public void setSuccessIsSet(boolean value) {
			if (!value) {
				this.success = null;
			}
		}

		public void setFieldValue(_Fields field, Object value) {
			switch (field) {
			case SUCCESS:
				if (value == null) {
					unsetSuccess();
				} else {
					setSuccess((SegmentData) value);
				}
				break;

			}
		}

		public Object getFieldValue(_Fields field) {
			switch (field) {
			case SUCCESS:
				return getSuccess();

			}
			throw new IllegalStateException();
		}

		/**
		 * Returns true if field corresponding to fieldID is set (has been
		 * assigned a value) and false otherwise
		 */
		public boolean isSet(_Fields field) {
			if (field == null) {
				throw new IllegalArgumentException();
			}

			switch (field) {
			case SUCCESS:
				return isSetSuccess();
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (that instanceof getSegmentData_result)
				return this.equals((getSegmentData_result) that);
			return false;
		}

		public boolean equals(getSegmentData_result that) {
			if (that == null)
				return false;

			boolean this_present_success = true && this.isSetSuccess();
			boolean that_present_success = true && that.isSetSuccess();
			if (this_present_success || that_present_success) {
				if (!(this_present_success && that_present_success))
					return false;
				if (!this.success.equals(that.success))
					return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		public int compareTo(getSegmentData_result other) {
			if (!getClass().equals(other.getClass())) {
				return getClass().getName().compareTo(
						other.getClass().getName());
			}

			int lastComparison = 0;
			getSegmentData_result typedOther = (getSegmentData_result) other;

			lastComparison = Boolean.valueOf(isSetSuccess()).compareTo(
					typedOther.isSetSuccess());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetSuccess()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.success, typedOther.success);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			return 0;
		}

		public _Fields fieldForId(int fieldId) {
			return _Fields.findByThriftId(fieldId);
		}

		public void read(org.apache.thrift.protocol.TProtocol iprot)
				throws org.apache.thrift.TException {
			schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
		}

		public void write(org.apache.thrift.protocol.TProtocol oprot)
				throws org.apache.thrift.TException {
			schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("getSegmentData_result(");
			boolean first = true;

			sb.append("success:");
			if (this.success == null) {
				sb.append("null");
			} else {
				sb.append(this.success);
			}
			first = false;
			sb.append(")");
			return sb.toString();
		}

		public void validate() throws org.apache.thrift.TException {
			// check for required fields
		}

		private void writeObject(java.io.ObjectOutputStream out)
				throws java.io.IOException {
			try {
				write(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(out)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private void readObject(java.io.ObjectInputStream in)
				throws java.io.IOException, ClassNotFoundException {
			try {
				read(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(in)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private static class getSegmentData_resultStandardSchemeFactory
				implements SchemeFactory {
			public getSegmentData_resultStandardScheme getScheme() {
				return new getSegmentData_resultStandardScheme();
			}
		}

		private static class getSegmentData_resultStandardScheme extends
				StandardScheme<getSegmentData_result> {

			public void read(org.apache.thrift.protocol.TProtocol iprot,
					getSegmentData_result struct)
					throws org.apache.thrift.TException {
				org.apache.thrift.protocol.TField schemeField;
				iprot.readStructBegin();
				while (true) {
					schemeField = iprot.readFieldBegin();
					if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
						break;
					}
					switch (schemeField.id) {
					case 0: // SUCCESS
						if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
							struct.success = new SegmentData();
							struct.success.read(iprot);
							struct.setSuccessIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					default:
						org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
								schemeField.type);
					}
					iprot.readFieldEnd();
				}
				iprot.readStructEnd();

				// check for required fields of primitive type, which can't be
				// checked in the validate method
				struct.validate();
			}

			public void write(org.apache.thrift.protocol.TProtocol oprot,
					getSegmentData_result struct)
					throws org.apache.thrift.TException {
				struct.validate();

				oprot.writeStructBegin(STRUCT_DESC);
				if (struct.success != null) {
					oprot.writeFieldBegin(SUCCESS_FIELD_DESC);
					struct.success.write(oprot);
					oprot.writeFieldEnd();
				}
				oprot.writeFieldStop();
				oprot.writeStructEnd();
			}

		}

		private static class getSegmentData_resultTupleSchemeFactory implements
				SchemeFactory {
			public getSegmentData_resultTupleScheme getScheme() {
				return new getSegmentData_resultTupleScheme();
			}
		}

		private static class getSegmentData_resultTupleScheme extends
				TupleScheme<getSegmentData_result> {

			@Override
			public void write(org.apache.thrift.protocol.TProtocol prot,
					getSegmentData_result struct)
					throws org.apache.thrift.TException {
				TTupleProtocol oprot = (TTupleProtocol) prot;
				BitSet optionals = new BitSet();
				if (struct.isSetSuccess()) {
					optionals.set(0);
				}
				oprot.writeBitSet(optionals, 1);
				if (struct.isSetSuccess()) {
					struct.success.write(oprot);
				}
			}

			@Override
			public void read(org.apache.thrift.protocol.TProtocol prot,
					getSegmentData_result struct)
					throws org.apache.thrift.TException {
				TTupleProtocol iprot = (TTupleProtocol) prot;
				BitSet incoming = iprot.readBitSet(1);
				if (incoming.get(0)) {
					struct.success = new SegmentData();
					struct.success.read(iprot);
					struct.setSuccessIsSet(true);
				}
			}
		}

	}

	public static class deleteTreeNodes_args
			implements
			org.apache.thrift.TBase<deleteTreeNodes_args, deleteTreeNodes_args._Fields>,
			java.io.Serializable, Cloneable {
		private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
				"deleteTreeNodes_args");

		private static final org.apache.thrift.protocol.TField TREE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"treeId", org.apache.thrift.protocol.TType.I64, (short) 1);
		private static final org.apache.thrift.protocol.TField NODE_IDS_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"nodeIds", org.apache.thrift.protocol.TType.LIST, (short) 2);

		private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
		static {
			schemes.put(StandardScheme.class,
					new deleteTreeNodes_argsStandardSchemeFactory());
			schemes.put(TupleScheme.class,
					new deleteTreeNodes_argsTupleSchemeFactory());
		}

		public long treeId; // required
		public List<Integer> nodeIds; // required

		/**
		 * The set of fields this struct contains, along with convenience
		 * methods for finding and manipulating them.
		 */
		public enum _Fields implements org.apache.thrift.TFieldIdEnum {
			TREE_ID((short) 1, "treeId"), NODE_IDS((short) 2, "nodeIds");

			private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

			static {
				for (_Fields field : EnumSet.allOf(_Fields.class)) {
					byName.put(field.getFieldName(), field);
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, or null if its
			 * not found.
			 */
			public static _Fields findByThriftId(int fieldId) {
				switch (fieldId) {
				case 1: // TREE_ID
					return TREE_ID;
				case 2: // NODE_IDS
					return NODE_IDS;
				default:
					return null;
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, throwing an
			 * exception if it is not found.
			 */
			public static _Fields findByThriftIdOrThrow(int fieldId) {
				_Fields fields = findByThriftId(fieldId);
				if (fields == null)
					throw new IllegalArgumentException("Field " + fieldId
							+ " doesn't exist!");
				return fields;
			}

			/**
			 * Find the _Fields constant that matches name, or null if its not
			 * found.
			 */
			public static _Fields findByName(String name) {
				return byName.get(name);
			}

			private final short _thriftId;
			private final String _fieldName;

			_Fields(short thriftId, String fieldName) {
				_thriftId = thriftId;
				_fieldName = fieldName;
			}

			public short getThriftFieldId() {
				return _thriftId;
			}

			public String getFieldName() {
				return _fieldName;
			}
		}

		// isset id assignments
		private static final int __TREEID_ISSET_ID = 0;
		private BitSet __isset_bit_vector = new BitSet(1);
		public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
		static {
			Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
					_Fields.class);
			tmpMap.put(_Fields.TREE_ID,
					new org.apache.thrift.meta_data.FieldMetaData("treeId",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.FieldValueMetaData(
									org.apache.thrift.protocol.TType.I64)));
			tmpMap.put(
					_Fields.NODE_IDS,
					new org.apache.thrift.meta_data.FieldMetaData(
							"nodeIds",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.ListMetaData(
									org.apache.thrift.protocol.TType.LIST,
									new org.apache.thrift.meta_data.FieldValueMetaData(
											org.apache.thrift.protocol.TType.I32))));
			metaDataMap = Collections.unmodifiableMap(tmpMap);
			org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
					deleteTreeNodes_args.class, metaDataMap);
		}

		public deleteTreeNodes_args() {
		}

		public deleteTreeNodes_args(long treeId, List<Integer> nodeIds) {
			this();
			this.treeId = treeId;
			setTreeIdIsSet(true);
			this.nodeIds = nodeIds;
		}

		/**
		 * Performs a deep copy on <i>other</i>.
		 */
		public deleteTreeNodes_args(deleteTreeNodes_args other) {
			__isset_bit_vector.clear();
			__isset_bit_vector.or(other.__isset_bit_vector);
			this.treeId = other.treeId;
			if (other.isSetNodeIds()) {
				List<Integer> __this__nodeIds = new ArrayList<Integer>();
				for (Integer other_element : other.nodeIds) {
					__this__nodeIds.add(other_element);
				}
				this.nodeIds = __this__nodeIds;
			}
		}

		public deleteTreeNodes_args deepCopy() {
			return new deleteTreeNodes_args(this);
		}

		@Override
		public void clear() {
			setTreeIdIsSet(false);
			this.treeId = 0;
			this.nodeIds = null;
		}

		public long getTreeId() {
			return this.treeId;
		}

		public deleteTreeNodes_args setTreeId(long treeId) {
			this.treeId = treeId;
			setTreeIdIsSet(true);
			return this;
		}

		public void unsetTreeId() {
			__isset_bit_vector.clear(__TREEID_ISSET_ID);
		}

		/**
		 * Returns true if field treeId is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetTreeId() {
			return __isset_bit_vector.get(__TREEID_ISSET_ID);
		}

		public void setTreeIdIsSet(boolean value) {
			__isset_bit_vector.set(__TREEID_ISSET_ID, value);
		}

		public int getNodeIdsSize() {
			return (this.nodeIds == null) ? 0 : this.nodeIds.size();
		}

		public java.util.Iterator<Integer> getNodeIdsIterator() {
			return (this.nodeIds == null) ? null : this.nodeIds.iterator();
		}

		public void addToNodeIds(int elem) {
			if (this.nodeIds == null) {
				this.nodeIds = new ArrayList<Integer>();
			}
			this.nodeIds.add(elem);
		}

		public List<Integer> getNodeIds() {
			return this.nodeIds;
		}

		public deleteTreeNodes_args setNodeIds(List<Integer> nodeIds) {
			this.nodeIds = nodeIds;
			return this;
		}

		public void unsetNodeIds() {
			this.nodeIds = null;
		}

		/**
		 * Returns true if field nodeIds is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetNodeIds() {
			return this.nodeIds != null;
		}

		public void setNodeIdsIsSet(boolean value) {
			if (!value) {
				this.nodeIds = null;
			}
		}

		public void setFieldValue(_Fields field, Object value) {
			switch (field) {
			case TREE_ID:
				if (value == null) {
					unsetTreeId();
				} else {
					setTreeId((Long) value);
				}
				break;

			case NODE_IDS:
				if (value == null) {
					unsetNodeIds();
				} else {
					setNodeIds((List<Integer>) value);
				}
				break;

			}
		}

		public Object getFieldValue(_Fields field) {
			switch (field) {
			case TREE_ID:
				return Long.valueOf(getTreeId());

			case NODE_IDS:
				return getNodeIds();

			}
			throw new IllegalStateException();
		}

		/**
		 * Returns true if field corresponding to fieldID is set (has been
		 * assigned a value) and false otherwise
		 */
		public boolean isSet(_Fields field) {
			if (field == null) {
				throw new IllegalArgumentException();
			}

			switch (field) {
			case TREE_ID:
				return isSetTreeId();
			case NODE_IDS:
				return isSetNodeIds();
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (that instanceof deleteTreeNodes_args)
				return this.equals((deleteTreeNodes_args) that);
			return false;
		}

		public boolean equals(deleteTreeNodes_args that) {
			if (that == null)
				return false;

			boolean this_present_treeId = true;
			boolean that_present_treeId = true;
			if (this_present_treeId || that_present_treeId) {
				if (!(this_present_treeId && that_present_treeId))
					return false;
				if (this.treeId != that.treeId)
					return false;
			}

			boolean this_present_nodeIds = true && this.isSetNodeIds();
			boolean that_present_nodeIds = true && that.isSetNodeIds();
			if (this_present_nodeIds || that_present_nodeIds) {
				if (!(this_present_nodeIds && that_present_nodeIds))
					return false;
				if (!this.nodeIds.equals(that.nodeIds))
					return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		public int compareTo(deleteTreeNodes_args other) {
			if (!getClass().equals(other.getClass())) {
				return getClass().getName().compareTo(
						other.getClass().getName());
			}

			int lastComparison = 0;
			deleteTreeNodes_args typedOther = (deleteTreeNodes_args) other;

			lastComparison = Boolean.valueOf(isSetTreeId()).compareTo(
					typedOther.isSetTreeId());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetTreeId()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.treeId, typedOther.treeId);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			lastComparison = Boolean.valueOf(isSetNodeIds()).compareTo(
					typedOther.isSetNodeIds());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetNodeIds()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.nodeIds, typedOther.nodeIds);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			return 0;
		}

		public _Fields fieldForId(int fieldId) {
			return _Fields.findByThriftId(fieldId);
		}

		public void read(org.apache.thrift.protocol.TProtocol iprot)
				throws org.apache.thrift.TException {
			schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
		}

		public void write(org.apache.thrift.protocol.TProtocol oprot)
				throws org.apache.thrift.TException {
			schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("deleteTreeNodes_args(");
			boolean first = true;

			sb.append("treeId:");
			sb.append(this.treeId);
			first = false;
			if (!first)
				sb.append(", ");
			sb.append("nodeIds:");
			if (this.nodeIds == null) {
				sb.append("null");
			} else {
				sb.append(this.nodeIds);
			}
			first = false;
			sb.append(")");
			return sb.toString();
		}

		public void validate() throws org.apache.thrift.TException {
			// check for required fields
		}

		private void writeObject(java.io.ObjectOutputStream out)
				throws java.io.IOException {
			try {
				write(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(out)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private void readObject(java.io.ObjectInputStream in)
				throws java.io.IOException, ClassNotFoundException {
			try {
				read(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(in)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private static class deleteTreeNodes_argsStandardSchemeFactory
				implements SchemeFactory {
			public deleteTreeNodes_argsStandardScheme getScheme() {
				return new deleteTreeNodes_argsStandardScheme();
			}
		}

		private static class deleteTreeNodes_argsStandardScheme extends
				StandardScheme<deleteTreeNodes_args> {

			public void read(org.apache.thrift.protocol.TProtocol iprot,
					deleteTreeNodes_args struct)
					throws org.apache.thrift.TException {
				org.apache.thrift.protocol.TField schemeField;
				iprot.readStructBegin();
				while (true) {
					schemeField = iprot.readFieldBegin();
					if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
						break;
					}
					switch (schemeField.id) {
					case 1: // TREE_ID
						if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
							struct.treeId = iprot.readI64();
							struct.setTreeIdIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					case 2: // NODE_IDS
						if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
							{
								org.apache.thrift.protocol.TList _list42 = iprot
										.readListBegin();
								struct.nodeIds = new ArrayList<Integer>(
										_list42.size);
								for (int _i43 = 0; _i43 < _list42.size; ++_i43) {
									int _elem44; // required
									_elem44 = iprot.readI32();
									struct.nodeIds.add(_elem44);
								}
								iprot.readListEnd();
							}
							struct.setNodeIdsIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					default:
						org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
								schemeField.type);
					}
					iprot.readFieldEnd();
				}
				iprot.readStructEnd();

				// check for required fields of primitive type, which can't be
				// checked in the validate method
				struct.validate();
			}

			public void write(org.apache.thrift.protocol.TProtocol oprot,
					deleteTreeNodes_args struct)
					throws org.apache.thrift.TException {
				struct.validate();

				oprot.writeStructBegin(STRUCT_DESC);
				oprot.writeFieldBegin(TREE_ID_FIELD_DESC);
				oprot.writeI64(struct.treeId);
				oprot.writeFieldEnd();
				if (struct.nodeIds != null) {
					oprot.writeFieldBegin(NODE_IDS_FIELD_DESC);
					{
						oprot.writeListBegin(new org.apache.thrift.protocol.TList(
								org.apache.thrift.protocol.TType.I32,
								struct.nodeIds.size()));
						for (int _iter45 : struct.nodeIds) {
							oprot.writeI32(_iter45);
						}
						oprot.writeListEnd();
					}
					oprot.writeFieldEnd();
				}
				oprot.writeFieldStop();
				oprot.writeStructEnd();
			}

		}

		private static class deleteTreeNodes_argsTupleSchemeFactory implements
				SchemeFactory {
			public deleteTreeNodes_argsTupleScheme getScheme() {
				return new deleteTreeNodes_argsTupleScheme();
			}
		}

		private static class deleteTreeNodes_argsTupleScheme extends
				TupleScheme<deleteTreeNodes_args> {

			@Override
			public void write(org.apache.thrift.protocol.TProtocol prot,
					deleteTreeNodes_args struct)
					throws org.apache.thrift.TException {
				TTupleProtocol oprot = (TTupleProtocol) prot;
				BitSet optionals = new BitSet();
				if (struct.isSetTreeId()) {
					optionals.set(0);
				}
				if (struct.isSetNodeIds()) {
					optionals.set(1);
				}
				oprot.writeBitSet(optionals, 2);
				if (struct.isSetTreeId()) {
					oprot.writeI64(struct.treeId);
				}
				if (struct.isSetNodeIds()) {
					{
						oprot.writeI32(struct.nodeIds.size());
						for (int _iter46 : struct.nodeIds) {
							oprot.writeI32(_iter46);
						}
					}
				}
			}

			@Override
			public void read(org.apache.thrift.protocol.TProtocol prot,
					deleteTreeNodes_args struct)
					throws org.apache.thrift.TException {
				TTupleProtocol iprot = (TTupleProtocol) prot;
				BitSet incoming = iprot.readBitSet(2);
				if (incoming.get(0)) {
					struct.treeId = iprot.readI64();
					struct.setTreeIdIsSet(true);
				}
				if (incoming.get(1)) {
					{
						org.apache.thrift.protocol.TList _list47 = new org.apache.thrift.protocol.TList(
								org.apache.thrift.protocol.TType.I32,
								iprot.readI32());
						struct.nodeIds = new ArrayList<Integer>(_list47.size);
						for (int _i48 = 0; _i48 < _list47.size; ++_i48) {
							int _elem49; // required
							_elem49 = iprot.readI32();
							struct.nodeIds.add(_elem49);
						}
					}
					struct.setNodeIdsIsSet(true);
				}
			}
		}

	}

	public static class deleteTreeNodes_result
			implements
			org.apache.thrift.TBase<deleteTreeNodes_result, deleteTreeNodes_result._Fields>,
			java.io.Serializable, Cloneable {
		private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
				"deleteTreeNodes_result");

		private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
		static {
			schemes.put(StandardScheme.class,
					new deleteTreeNodes_resultStandardSchemeFactory());
			schemes.put(TupleScheme.class,
					new deleteTreeNodes_resultTupleSchemeFactory());
		}

		/**
		 * The set of fields this struct contains, along with convenience
		 * methods for finding and manipulating them.
		 */
		public enum _Fields implements org.apache.thrift.TFieldIdEnum {
			;

			private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

			static {
				for (_Fields field : EnumSet.allOf(_Fields.class)) {
					byName.put(field.getFieldName(), field);
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, or null if its
			 * not found.
			 */
			public static _Fields findByThriftId(int fieldId) {
				switch (fieldId) {
				default:
					return null;
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, throwing an
			 * exception if it is not found.
			 */
			public static _Fields findByThriftIdOrThrow(int fieldId) {
				_Fields fields = findByThriftId(fieldId);
				if (fields == null)
					throw new IllegalArgumentException("Field " + fieldId
							+ " doesn't exist!");
				return fields;
			}

			/**
			 * Find the _Fields constant that matches name, or null if its not
			 * found.
			 */
			public static _Fields findByName(String name) {
				return byName.get(name);
			}

			private final short _thriftId;
			private final String _fieldName;

			_Fields(short thriftId, String fieldName) {
				_thriftId = thriftId;
				_fieldName = fieldName;
			}

			public short getThriftFieldId() {
				return _thriftId;
			}

			public String getFieldName() {
				return _fieldName;
			}
		}

		public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
		static {
			Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
					_Fields.class);
			metaDataMap = Collections.unmodifiableMap(tmpMap);
			org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
					deleteTreeNodes_result.class, metaDataMap);
		}

		public deleteTreeNodes_result() {
		}

		/**
		 * Performs a deep copy on <i>other</i>.
		 */
		public deleteTreeNodes_result(deleteTreeNodes_result other) {
		}

		public deleteTreeNodes_result deepCopy() {
			return new deleteTreeNodes_result(this);
		}

		@Override
		public void clear() {
		}

		public void setFieldValue(_Fields field, Object value) {
			switch (field) {
			}
		}

		public Object getFieldValue(_Fields field) {
			switch (field) {
			}
			throw new IllegalStateException();
		}

		/**
		 * Returns true if field corresponding to fieldID is set (has been
		 * assigned a value) and false otherwise
		 */
		public boolean isSet(_Fields field) {
			if (field == null) {
				throw new IllegalArgumentException();
			}

			switch (field) {
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (that instanceof deleteTreeNodes_result)
				return this.equals((deleteTreeNodes_result) that);
			return false;
		}

		public boolean equals(deleteTreeNodes_result that) {
			if (that == null)
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		public int compareTo(deleteTreeNodes_result other) {
			if (!getClass().equals(other.getClass())) {
				return getClass().getName().compareTo(
						other.getClass().getName());
			}

			int lastComparison = 0;
			deleteTreeNodes_result typedOther = (deleteTreeNodes_result) other;

			return 0;
		}

		public _Fields fieldForId(int fieldId) {
			return _Fields.findByThriftId(fieldId);
		}

		public void read(org.apache.thrift.protocol.TProtocol iprot)
				throws org.apache.thrift.TException {
			schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
		}

		public void write(org.apache.thrift.protocol.TProtocol oprot)
				throws org.apache.thrift.TException {
			schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("deleteTreeNodes_result(");
			boolean first = true;

			sb.append(")");
			return sb.toString();
		}

		public void validate() throws org.apache.thrift.TException {
			// check for required fields
		}

		private void writeObject(java.io.ObjectOutputStream out)
				throws java.io.IOException {
			try {
				write(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(out)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private void readObject(java.io.ObjectInputStream in)
				throws java.io.IOException, ClassNotFoundException {
			try {
				read(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(in)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private static class deleteTreeNodes_resultStandardSchemeFactory
				implements SchemeFactory {
			public deleteTreeNodes_resultStandardScheme getScheme() {
				return new deleteTreeNodes_resultStandardScheme();
			}
		}

		private static class deleteTreeNodes_resultStandardScheme extends
				StandardScheme<deleteTreeNodes_result> {

			public void read(org.apache.thrift.protocol.TProtocol iprot,
					deleteTreeNodes_result struct)
					throws org.apache.thrift.TException {
				org.apache.thrift.protocol.TField schemeField;
				iprot.readStructBegin();
				while (true) {
					schemeField = iprot.readFieldBegin();
					if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
						break;
					}
					switch (schemeField.id) {
					default:
						org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
								schemeField.type);
					}
					iprot.readFieldEnd();
				}
				iprot.readStructEnd();

				// check for required fields of primitive type, which can't be
				// checked in the validate method
				struct.validate();
			}

			public void write(org.apache.thrift.protocol.TProtocol oprot,
					deleteTreeNodes_result struct)
					throws org.apache.thrift.TException {
				struct.validate();

				oprot.writeStructBegin(STRUCT_DESC);
				oprot.writeFieldStop();
				oprot.writeStructEnd();
			}

		}

		private static class deleteTreeNodes_resultTupleSchemeFactory implements
				SchemeFactory {
			public deleteTreeNodes_resultTupleScheme getScheme() {
				return new deleteTreeNodes_resultTupleScheme();
			}
		}

		private static class deleteTreeNodes_resultTupleScheme extends
				TupleScheme<deleteTreeNodes_result> {

			@Override
			public void write(org.apache.thrift.protocol.TProtocol prot,
					deleteTreeNodes_result struct)
					throws org.apache.thrift.TException {
				TTupleProtocol oprot = (TTupleProtocol) prot;
			}

			@Override
			public void read(org.apache.thrift.protocol.TProtocol prot,
					deleteTreeNodes_result struct)
					throws org.apache.thrift.TException {
				TTupleProtocol iprot = (TTupleProtocol) prot;
			}
		}

	}

	public static class rebuildHashTree_args
			implements
			org.apache.thrift.TBase<rebuildHashTree_args, rebuildHashTree_args._Fields>,
			java.io.Serializable, Cloneable {
		private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
				"rebuildHashTree_args");

		private static final org.apache.thrift.protocol.TField SN_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"sn", org.apache.thrift.protocol.TType.STRUCT, (short) 1);
		private static final org.apache.thrift.protocol.TField TREE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"treeId", org.apache.thrift.protocol.TType.I64, (short) 2);
		private static final org.apache.thrift.protocol.TField TOKEN_NO_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"tokenNo", org.apache.thrift.protocol.TType.I64, (short) 3);
		private static final org.apache.thrift.protocol.TField EXP_FULL_REBUILD_TIME_INT_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"expFullRebuildTimeInt", org.apache.thrift.protocol.TType.I64,
				(short) 4);

		private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
		static {
			schemes.put(StandardScheme.class,
					new rebuildHashTree_argsStandardSchemeFactory());
			schemes.put(TupleScheme.class,
					new rebuildHashTree_argsTupleSchemeFactory());
		}

		public ServerName sn; // required
		public long treeId; // required
		public long tokenNo; // required
		public long expFullRebuildTimeInt; // required

		/**
		 * The set of fields this struct contains, along with convenience
		 * methods for finding and manipulating them.
		 */
		public enum _Fields implements org.apache.thrift.TFieldIdEnum {
			SN((short) 1, "sn"), TREE_ID((short) 2, "treeId"), TOKEN_NO(
					(short) 3, "tokenNo"), EXP_FULL_REBUILD_TIME_INT((short) 4,
					"expFullRebuildTimeInt");

			private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

			static {
				for (_Fields field : EnumSet.allOf(_Fields.class)) {
					byName.put(field.getFieldName(), field);
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, or null if its
			 * not found.
			 */
			public static _Fields findByThriftId(int fieldId) {
				switch (fieldId) {
				case 1: // SN
					return SN;
				case 2: // TREE_ID
					return TREE_ID;
				case 3: // TOKEN_NO
					return TOKEN_NO;
				case 4: // EXP_FULL_REBUILD_TIME_INT
					return EXP_FULL_REBUILD_TIME_INT;
				default:
					return null;
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, throwing an
			 * exception if it is not found.
			 */
			public static _Fields findByThriftIdOrThrow(int fieldId) {
				_Fields fields = findByThriftId(fieldId);
				if (fields == null)
					throw new IllegalArgumentException("Field " + fieldId
							+ " doesn't exist!");
				return fields;
			}

			/**
			 * Find the _Fields constant that matches name, or null if its not
			 * found.
			 */
			public static _Fields findByName(String name) {
				return byName.get(name);
			}

			private final short _thriftId;
			private final String _fieldName;

			_Fields(short thriftId, String fieldName) {
				_thriftId = thriftId;
				_fieldName = fieldName;
			}

			public short getThriftFieldId() {
				return _thriftId;
			}

			public String getFieldName() {
				return _fieldName;
			}
		}

		// isset id assignments
		private static final int __TREEID_ISSET_ID = 0;
		private static final int __TOKENNO_ISSET_ID = 1;
		private static final int __EXPFULLREBUILDTIMEINT_ISSET_ID = 2;
		private BitSet __isset_bit_vector = new BitSet(3);
		public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
		static {
			Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
					_Fields.class);
			tmpMap.put(_Fields.SN,
					new org.apache.thrift.meta_data.FieldMetaData("sn",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.StructMetaData(
									org.apache.thrift.protocol.TType.STRUCT,
									ServerName.class)));
			tmpMap.put(_Fields.TREE_ID,
					new org.apache.thrift.meta_data.FieldMetaData("treeId",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.FieldValueMetaData(
									org.apache.thrift.protocol.TType.I64)));
			tmpMap.put(_Fields.TOKEN_NO,
					new org.apache.thrift.meta_data.FieldMetaData("tokenNo",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.FieldValueMetaData(
									org.apache.thrift.protocol.TType.I64)));
			tmpMap.put(_Fields.EXP_FULL_REBUILD_TIME_INT,
					new org.apache.thrift.meta_data.FieldMetaData(
							"expFullRebuildTimeInt",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.FieldValueMetaData(
									org.apache.thrift.protocol.TType.I64)));
			metaDataMap = Collections.unmodifiableMap(tmpMap);
			org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
					rebuildHashTree_args.class, metaDataMap);
		}

		public rebuildHashTree_args() {
		}

		public rebuildHashTree_args(ServerName sn, long treeId, long tokenNo,
				long expFullRebuildTimeInt) {
			this();
			this.sn = sn;
			this.treeId = treeId;
			setTreeIdIsSet(true);
			this.tokenNo = tokenNo;
			setTokenNoIsSet(true);
			this.expFullRebuildTimeInt = expFullRebuildTimeInt;
			setExpFullRebuildTimeIntIsSet(true);
		}

		/**
		 * Performs a deep copy on <i>other</i>.
		 */
		public rebuildHashTree_args(rebuildHashTree_args other) {
			__isset_bit_vector.clear();
			__isset_bit_vector.or(other.__isset_bit_vector);
			if (other.isSetSn()) {
				this.sn = new ServerName(other.sn);
			}
			this.treeId = other.treeId;
			this.tokenNo = other.tokenNo;
			this.expFullRebuildTimeInt = other.expFullRebuildTimeInt;
		}

		public rebuildHashTree_args deepCopy() {
			return new rebuildHashTree_args(this);
		}

		@Override
		public void clear() {
			this.sn = null;
			setTreeIdIsSet(false);
			this.treeId = 0;
			setTokenNoIsSet(false);
			this.tokenNo = 0;
			setExpFullRebuildTimeIntIsSet(false);
			this.expFullRebuildTimeInt = 0;
		}

		public ServerName getSn() {
			return this.sn;
		}

		public rebuildHashTree_args setSn(ServerName sn) {
			this.sn = sn;
			return this;
		}

		public void unsetSn() {
			this.sn = null;
		}

		/**
		 * Returns true if field sn is set (has been assigned a value) and false
		 * otherwise
		 */
		public boolean isSetSn() {
			return this.sn != null;
		}

		public void setSnIsSet(boolean value) {
			if (!value) {
				this.sn = null;
			}
		}

		public long getTreeId() {
			return this.treeId;
		}

		public rebuildHashTree_args setTreeId(long treeId) {
			this.treeId = treeId;
			setTreeIdIsSet(true);
			return this;
		}

		public void unsetTreeId() {
			__isset_bit_vector.clear(__TREEID_ISSET_ID);
		}

		/**
		 * Returns true if field treeId is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetTreeId() {
			return __isset_bit_vector.get(__TREEID_ISSET_ID);
		}

		public void setTreeIdIsSet(boolean value) {
			__isset_bit_vector.set(__TREEID_ISSET_ID, value);
		}

		public long getTokenNo() {
			return this.tokenNo;
		}

		public rebuildHashTree_args setTokenNo(long tokenNo) {
			this.tokenNo = tokenNo;
			setTokenNoIsSet(true);
			return this;
		}

		public void unsetTokenNo() {
			__isset_bit_vector.clear(__TOKENNO_ISSET_ID);
		}

		/**
		 * Returns true if field tokenNo is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetTokenNo() {
			return __isset_bit_vector.get(__TOKENNO_ISSET_ID);
		}

		public void setTokenNoIsSet(boolean value) {
			__isset_bit_vector.set(__TOKENNO_ISSET_ID, value);
		}

		public long getExpFullRebuildTimeInt() {
			return this.expFullRebuildTimeInt;
		}

		public rebuildHashTree_args setExpFullRebuildTimeInt(
				long expFullRebuildTimeInt) {
			this.expFullRebuildTimeInt = expFullRebuildTimeInt;
			setExpFullRebuildTimeIntIsSet(true);
			return this;
		}

		public void unsetExpFullRebuildTimeInt() {
			__isset_bit_vector.clear(__EXPFULLREBUILDTIMEINT_ISSET_ID);
		}

		/**
		 * Returns true if field expFullRebuildTimeInt is set (has been assigned
		 * a value) and false otherwise
		 */
		public boolean isSetExpFullRebuildTimeInt() {
			return __isset_bit_vector.get(__EXPFULLREBUILDTIMEINT_ISSET_ID);
		}

		public void setExpFullRebuildTimeIntIsSet(boolean value) {
			__isset_bit_vector.set(__EXPFULLREBUILDTIMEINT_ISSET_ID, value);
		}

		public void setFieldValue(_Fields field, Object value) {
			switch (field) {
			case SN:
				if (value == null) {
					unsetSn();
				} else {
					setSn((ServerName) value);
				}
				break;

			case TREE_ID:
				if (value == null) {
					unsetTreeId();
				} else {
					setTreeId((Long) value);
				}
				break;

			case TOKEN_NO:
				if (value == null) {
					unsetTokenNo();
				} else {
					setTokenNo((Long) value);
				}
				break;

			case EXP_FULL_REBUILD_TIME_INT:
				if (value == null) {
					unsetExpFullRebuildTimeInt();
				} else {
					setExpFullRebuildTimeInt((Long) value);
				}
				break;

			}
		}

		public Object getFieldValue(_Fields field) {
			switch (field) {
			case SN:
				return getSn();

			case TREE_ID:
				return Long.valueOf(getTreeId());

			case TOKEN_NO:
				return Long.valueOf(getTokenNo());

			case EXP_FULL_REBUILD_TIME_INT:
				return Long.valueOf(getExpFullRebuildTimeInt());

			}
			throw new IllegalStateException();
		}

		/**
		 * Returns true if field corresponding to fieldID is set (has been
		 * assigned a value) and false otherwise
		 */
		public boolean isSet(_Fields field) {
			if (field == null) {
				throw new IllegalArgumentException();
			}

			switch (field) {
			case SN:
				return isSetSn();
			case TREE_ID:
				return isSetTreeId();
			case TOKEN_NO:
				return isSetTokenNo();
			case EXP_FULL_REBUILD_TIME_INT:
				return isSetExpFullRebuildTimeInt();
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (that instanceof rebuildHashTree_args)
				return this.equals((rebuildHashTree_args) that);
			return false;
		}

		public boolean equals(rebuildHashTree_args that) {
			if (that == null)
				return false;

			boolean this_present_sn = true && this.isSetSn();
			boolean that_present_sn = true && that.isSetSn();
			if (this_present_sn || that_present_sn) {
				if (!(this_present_sn && that_present_sn))
					return false;
				if (!this.sn.equals(that.sn))
					return false;
			}

			boolean this_present_treeId = true;
			boolean that_present_treeId = true;
			if (this_present_treeId || that_present_treeId) {
				if (!(this_present_treeId && that_present_treeId))
					return false;
				if (this.treeId != that.treeId)
					return false;
			}

			boolean this_present_tokenNo = true;
			boolean that_present_tokenNo = true;
			if (this_present_tokenNo || that_present_tokenNo) {
				if (!(this_present_tokenNo && that_present_tokenNo))
					return false;
				if (this.tokenNo != that.tokenNo)
					return false;
			}

			boolean this_present_expFullRebuildTimeInt = true;
			boolean that_present_expFullRebuildTimeInt = true;
			if (this_present_expFullRebuildTimeInt
					|| that_present_expFullRebuildTimeInt) {
				if (!(this_present_expFullRebuildTimeInt && that_present_expFullRebuildTimeInt))
					return false;
				if (this.expFullRebuildTimeInt != that.expFullRebuildTimeInt)
					return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		public int compareTo(rebuildHashTree_args other) {
			if (!getClass().equals(other.getClass())) {
				return getClass().getName().compareTo(
						other.getClass().getName());
			}

			int lastComparison = 0;
			rebuildHashTree_args typedOther = (rebuildHashTree_args) other;

			lastComparison = Boolean.valueOf(isSetSn()).compareTo(
					typedOther.isSetSn());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetSn()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.sn, typedOther.sn);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			lastComparison = Boolean.valueOf(isSetTreeId()).compareTo(
					typedOther.isSetTreeId());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetTreeId()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.treeId, typedOther.treeId);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			lastComparison = Boolean.valueOf(isSetTokenNo()).compareTo(
					typedOther.isSetTokenNo());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetTokenNo()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.tokenNo, typedOther.tokenNo);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			lastComparison = Boolean.valueOf(isSetExpFullRebuildTimeInt())
					.compareTo(typedOther.isSetExpFullRebuildTimeInt());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetExpFullRebuildTimeInt()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.expFullRebuildTimeInt,
						typedOther.expFullRebuildTimeInt);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			return 0;
		}

		public _Fields fieldForId(int fieldId) {
			return _Fields.findByThriftId(fieldId);
		}

		public void read(org.apache.thrift.protocol.TProtocol iprot)
				throws org.apache.thrift.TException {
			schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
		}

		public void write(org.apache.thrift.protocol.TProtocol oprot)
				throws org.apache.thrift.TException {
			schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder("rebuildHashTree_args(");
			boolean first = true;

			sb.append("sn:");
			if (this.sn == null) {
				sb.append("null");
			} else {
				sb.append(this.sn);
			}
			first = false;
			if (!first)
				sb.append(", ");
			sb.append("treeId:");
			sb.append(this.treeId);
			first = false;
			if (!first)
				sb.append(", ");
			sb.append("tokenNo:");
			sb.append(this.tokenNo);
			first = false;
			if (!first)
				sb.append(", ");
			sb.append("expFullRebuildTimeInt:");
			sb.append(this.expFullRebuildTimeInt);
			first = false;
			sb.append(")");
			return sb.toString();
		}

		public void validate() throws org.apache.thrift.TException {
			// check for required fields
		}

		private void writeObject(java.io.ObjectOutputStream out)
				throws java.io.IOException {
			try {
				write(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(out)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private void readObject(java.io.ObjectInputStream in)
				throws java.io.IOException, ClassNotFoundException {
			try {
				read(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(in)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private static class rebuildHashTree_argsStandardSchemeFactory
				implements SchemeFactory {
			public rebuildHashTree_argsStandardScheme getScheme() {
				return new rebuildHashTree_argsStandardScheme();
			}
		}

		private static class rebuildHashTree_argsStandardScheme extends
				StandardScheme<rebuildHashTree_args> {

			public void read(org.apache.thrift.protocol.TProtocol iprot,
					rebuildHashTree_args struct)
					throws org.apache.thrift.TException {
				org.apache.thrift.protocol.TField schemeField;
				iprot.readStructBegin();
				while (true) {
					schemeField = iprot.readFieldBegin();
					if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
						break;
					}
					switch (schemeField.id) {
					case 1: // SN
						if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
							struct.sn = new ServerName();
							struct.sn.read(iprot);
							struct.setSnIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					case 2: // TREE_ID
						if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
							struct.treeId = iprot.readI64();
							struct.setTreeIdIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					case 3: // TOKEN_NO
						if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
							struct.tokenNo = iprot.readI64();
							struct.setTokenNoIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					case 4: // EXP_FULL_REBUILD_TIME_INT
						if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
							struct.expFullRebuildTimeInt = iprot.readI64();
							struct.setExpFullRebuildTimeIntIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					default:
						org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
								schemeField.type);
					}
					iprot.readFieldEnd();
				}
				iprot.readStructEnd();

				// check for required fields of primitive type, which can't be
				// checked in the validate method
				struct.validate();
			}

			public void write(org.apache.thrift.protocol.TProtocol oprot,
					rebuildHashTree_args struct)
					throws org.apache.thrift.TException {
				struct.validate();

				oprot.writeStructBegin(STRUCT_DESC);
				if (struct.sn != null) {
					oprot.writeFieldBegin(SN_FIELD_DESC);
					struct.sn.write(oprot);
					oprot.writeFieldEnd();
				}
				oprot.writeFieldBegin(TREE_ID_FIELD_DESC);
				oprot.writeI64(struct.treeId);
				oprot.writeFieldEnd();
				oprot.writeFieldBegin(TOKEN_NO_FIELD_DESC);
				oprot.writeI64(struct.tokenNo);
				oprot.writeFieldEnd();
				oprot.writeFieldBegin(EXP_FULL_REBUILD_TIME_INT_FIELD_DESC);
				oprot.writeI64(struct.expFullRebuildTimeInt);
				oprot.writeFieldEnd();
				oprot.writeFieldStop();
				oprot.writeStructEnd();
			}

		}

		private static class rebuildHashTree_argsTupleSchemeFactory implements
				SchemeFactory {
			public rebuildHashTree_argsTupleScheme getScheme() {
				return new rebuildHashTree_argsTupleScheme();
			}
		}

		private static class rebuildHashTree_argsTupleScheme extends
				TupleScheme<rebuildHashTree_args> {

			@Override
			public void write(org.apache.thrift.protocol.TProtocol prot,
					rebuildHashTree_args struct)
					throws org.apache.thrift.TException {
				TTupleProtocol oprot = (TTupleProtocol) prot;
				BitSet optionals = new BitSet();
				if (struct.isSetSn()) {
					optionals.set(0);
				}
				if (struct.isSetTreeId()) {
					optionals.set(1);
				}
				if (struct.isSetTokenNo()) {
					optionals.set(2);
				}
				if (struct.isSetExpFullRebuildTimeInt()) {
					optionals.set(3);
				}
				oprot.writeBitSet(optionals, 4);
				if (struct.isSetSn()) {
					struct.sn.write(oprot);
				}
				if (struct.isSetTreeId()) {
					oprot.writeI64(struct.treeId);
				}
				if (struct.isSetTokenNo()) {
					oprot.writeI64(struct.tokenNo);
				}
				if (struct.isSetExpFullRebuildTimeInt()) {
					oprot.writeI64(struct.expFullRebuildTimeInt);
				}
			}

			@Override
			public void read(org.apache.thrift.protocol.TProtocol prot,
					rebuildHashTree_args struct)
					throws org.apache.thrift.TException {
				TTupleProtocol iprot = (TTupleProtocol) prot;
				BitSet incoming = iprot.readBitSet(4);
				if (incoming.get(0)) {
					struct.sn = new ServerName();
					struct.sn.read(iprot);
					struct.setSnIsSet(true);
				}
				if (incoming.get(1)) {
					struct.treeId = iprot.readI64();
					struct.setTreeIdIsSet(true);
				}
				if (incoming.get(2)) {
					struct.tokenNo = iprot.readI64();
					struct.setTokenNoIsSet(true);
				}
				if (incoming.get(3)) {
					struct.expFullRebuildTimeInt = iprot.readI64();
					struct.setExpFullRebuildTimeIntIsSet(true);
				}
			}
		}

	}

	public static class postRebuildHashTreeResponse_args
			implements
			org.apache.thrift.TBase<postRebuildHashTreeResponse_args, postRebuildHashTreeResponse_args._Fields>,
			java.io.Serializable, Cloneable {
		private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct(
				"postRebuildHashTreeResponse_args");

		private static final org.apache.thrift.protocol.TField SN_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"sn", org.apache.thrift.protocol.TType.STRUCT, (short) 1);
		private static final org.apache.thrift.protocol.TField TREE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"treeId", org.apache.thrift.protocol.TType.I64, (short) 2);
		private static final org.apache.thrift.protocol.TField TOKEN_NO_FIELD_DESC = new org.apache.thrift.protocol.TField(
				"tokenNo", org.apache.thrift.protocol.TType.I64, (short) 3);

		private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
		static {
			schemes.put(StandardScheme.class,
					new postRebuildHashTreeResponse_argsStandardSchemeFactory());
			schemes.put(TupleScheme.class,
					new postRebuildHashTreeResponse_argsTupleSchemeFactory());
		}

		public ServerName sn; // required
		public long treeId; // required
		public long tokenNo; // required

		/**
		 * The set of fields this struct contains, along with convenience
		 * methods for finding and manipulating them.
		 */
		public enum _Fields implements org.apache.thrift.TFieldIdEnum {
			SN((short) 1, "sn"), TREE_ID((short) 2, "treeId"), TOKEN_NO(
					(short) 3, "tokenNo");

			private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

			static {
				for (_Fields field : EnumSet.allOf(_Fields.class)) {
					byName.put(field.getFieldName(), field);
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, or null if its
			 * not found.
			 */
			public static _Fields findByThriftId(int fieldId) {
				switch (fieldId) {
				case 1: // SN
					return SN;
				case 2: // TREE_ID
					return TREE_ID;
				case 3: // TOKEN_NO
					return TOKEN_NO;
				default:
					return null;
				}
			}

			/**
			 * Find the _Fields constant that matches fieldId, throwing an
			 * exception if it is not found.
			 */
			public static _Fields findByThriftIdOrThrow(int fieldId) {
				_Fields fields = findByThriftId(fieldId);
				if (fields == null)
					throw new IllegalArgumentException("Field " + fieldId
							+ " doesn't exist!");
				return fields;
			}

			/**
			 * Find the _Fields constant that matches name, or null if its not
			 * found.
			 */
			public static _Fields findByName(String name) {
				return byName.get(name);
			}

			private final short _thriftId;
			private final String _fieldName;

			_Fields(short thriftId, String fieldName) {
				_thriftId = thriftId;
				_fieldName = fieldName;
			}

			public short getThriftFieldId() {
				return _thriftId;
			}

			public String getFieldName() {
				return _fieldName;
			}
		}

		// isset id assignments
		private static final int __TREEID_ISSET_ID = 0;
		private static final int __TOKENNO_ISSET_ID = 1;
		private BitSet __isset_bit_vector = new BitSet(2);
		public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
		static {
			Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(
					_Fields.class);
			tmpMap.put(_Fields.SN,
					new org.apache.thrift.meta_data.FieldMetaData("sn",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.StructMetaData(
									org.apache.thrift.protocol.TType.STRUCT,
									ServerName.class)));
			tmpMap.put(_Fields.TREE_ID,
					new org.apache.thrift.meta_data.FieldMetaData("treeId",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.FieldValueMetaData(
									org.apache.thrift.protocol.TType.I64)));
			tmpMap.put(_Fields.TOKEN_NO,
					new org.apache.thrift.meta_data.FieldMetaData("tokenNo",
							org.apache.thrift.TFieldRequirementType.DEFAULT,
							new org.apache.thrift.meta_data.FieldValueMetaData(
									org.apache.thrift.protocol.TType.I64)));
			metaDataMap = Collections.unmodifiableMap(tmpMap);
			org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(
					postRebuildHashTreeResponse_args.class, metaDataMap);
		}

		public postRebuildHashTreeResponse_args() {
		}

		public postRebuildHashTreeResponse_args(ServerName sn, long treeId,
				long tokenNo) {
			this();
			this.sn = sn;
			this.treeId = treeId;
			setTreeIdIsSet(true);
			this.tokenNo = tokenNo;
			setTokenNoIsSet(true);
		}

		/**
		 * Performs a deep copy on <i>other</i>.
		 */
		public postRebuildHashTreeResponse_args(
				postRebuildHashTreeResponse_args other) {
			__isset_bit_vector.clear();
			__isset_bit_vector.or(other.__isset_bit_vector);
			if (other.isSetSn()) {
				this.sn = new ServerName(other.sn);
			}
			this.treeId = other.treeId;
			this.tokenNo = other.tokenNo;
		}

		public postRebuildHashTreeResponse_args deepCopy() {
			return new postRebuildHashTreeResponse_args(this);
		}

		@Override
		public void clear() {
			this.sn = null;
			setTreeIdIsSet(false);
			this.treeId = 0;
			setTokenNoIsSet(false);
			this.tokenNo = 0;
		}

		public ServerName getSn() {
			return this.sn;
		}

		public postRebuildHashTreeResponse_args setSn(ServerName sn) {
			this.sn = sn;
			return this;
		}

		public void unsetSn() {
			this.sn = null;
		}

		/**
		 * Returns true if field sn is set (has been assigned a value) and false
		 * otherwise
		 */
		public boolean isSetSn() {
			return this.sn != null;
		}

		public void setSnIsSet(boolean value) {
			if (!value) {
				this.sn = null;
			}
		}

		public long getTreeId() {
			return this.treeId;
		}

		public postRebuildHashTreeResponse_args setTreeId(long treeId) {
			this.treeId = treeId;
			setTreeIdIsSet(true);
			return this;
		}

		public void unsetTreeId() {
			__isset_bit_vector.clear(__TREEID_ISSET_ID);
		}

		/**
		 * Returns true if field treeId is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetTreeId() {
			return __isset_bit_vector.get(__TREEID_ISSET_ID);
		}

		public void setTreeIdIsSet(boolean value) {
			__isset_bit_vector.set(__TREEID_ISSET_ID, value);
		}

		public long getTokenNo() {
			return this.tokenNo;
		}

		public postRebuildHashTreeResponse_args setTokenNo(long tokenNo) {
			this.tokenNo = tokenNo;
			setTokenNoIsSet(true);
			return this;
		}

		public void unsetTokenNo() {
			__isset_bit_vector.clear(__TOKENNO_ISSET_ID);
		}

		/**
		 * Returns true if field tokenNo is set (has been assigned a value) and
		 * false otherwise
		 */
		public boolean isSetTokenNo() {
			return __isset_bit_vector.get(__TOKENNO_ISSET_ID);
		}

		public void setTokenNoIsSet(boolean value) {
			__isset_bit_vector.set(__TOKENNO_ISSET_ID, value);
		}

		public void setFieldValue(_Fields field, Object value) {
			switch (field) {
			case SN:
				if (value == null) {
					unsetSn();
				} else {
					setSn((ServerName) value);
				}
				break;

			case TREE_ID:
				if (value == null) {
					unsetTreeId();
				} else {
					setTreeId((Long) value);
				}
				break;

			case TOKEN_NO:
				if (value == null) {
					unsetTokenNo();
				} else {
					setTokenNo((Long) value);
				}
				break;

			}
		}

		public Object getFieldValue(_Fields field) {
			switch (field) {
			case SN:
				return getSn();

			case TREE_ID:
				return Long.valueOf(getTreeId());

			case TOKEN_NO:
				return Long.valueOf(getTokenNo());

			}
			throw new IllegalStateException();
		}

		/**
		 * Returns true if field corresponding to fieldID is set (has been
		 * assigned a value) and false otherwise
		 */
		public boolean isSet(_Fields field) {
			if (field == null) {
				throw new IllegalArgumentException();
			}

			switch (field) {
			case SN:
				return isSetSn();
			case TREE_ID:
				return isSetTreeId();
			case TOKEN_NO:
				return isSetTokenNo();
			}
			throw new IllegalStateException();
		}

		@Override
		public boolean equals(Object that) {
			if (that == null)
				return false;
			if (that instanceof postRebuildHashTreeResponse_args)
				return this.equals((postRebuildHashTreeResponse_args) that);
			return false;
		}

		public boolean equals(postRebuildHashTreeResponse_args that) {
			if (that == null)
				return false;

			boolean this_present_sn = true && this.isSetSn();
			boolean that_present_sn = true && that.isSetSn();
			if (this_present_sn || that_present_sn) {
				if (!(this_present_sn && that_present_sn))
					return false;
				if (!this.sn.equals(that.sn))
					return false;
			}

			boolean this_present_treeId = true;
			boolean that_present_treeId = true;
			if (this_present_treeId || that_present_treeId) {
				if (!(this_present_treeId && that_present_treeId))
					return false;
				if (this.treeId != that.treeId)
					return false;
			}

			boolean this_present_tokenNo = true;
			boolean that_present_tokenNo = true;
			if (this_present_tokenNo || that_present_tokenNo) {
				if (!(this_present_tokenNo && that_present_tokenNo))
					return false;
				if (this.tokenNo != that.tokenNo)
					return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		public int compareTo(postRebuildHashTreeResponse_args other) {
			if (!getClass().equals(other.getClass())) {
				return getClass().getName().compareTo(
						other.getClass().getName());
			}

			int lastComparison = 0;
			postRebuildHashTreeResponse_args typedOther = (postRebuildHashTreeResponse_args) other;

			lastComparison = Boolean.valueOf(isSetSn()).compareTo(
					typedOther.isSetSn());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetSn()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.sn, typedOther.sn);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			lastComparison = Boolean.valueOf(isSetTreeId()).compareTo(
					typedOther.isSetTreeId());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetTreeId()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.treeId, typedOther.treeId);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			lastComparison = Boolean.valueOf(isSetTokenNo()).compareTo(
					typedOther.isSetTokenNo());
			if (lastComparison != 0) {
				return lastComparison;
			}
			if (isSetTokenNo()) {
				lastComparison = org.apache.thrift.TBaseHelper.compareTo(
						this.tokenNo, typedOther.tokenNo);
				if (lastComparison != 0) {
					return lastComparison;
				}
			}
			return 0;
		}

		public _Fields fieldForId(int fieldId) {
			return _Fields.findByThriftId(fieldId);
		}

		public void read(org.apache.thrift.protocol.TProtocol iprot)
				throws org.apache.thrift.TException {
			schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
		}

		public void write(org.apache.thrift.protocol.TProtocol oprot)
				throws org.apache.thrift.TException {
			schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(
					"postRebuildHashTreeResponse_args(");
			boolean first = true;

			sb.append("sn:");
			if (this.sn == null) {
				sb.append("null");
			} else {
				sb.append(this.sn);
			}
			first = false;
			if (!first)
				sb.append(", ");
			sb.append("treeId:");
			sb.append(this.treeId);
			first = false;
			if (!first)
				sb.append(", ");
			sb.append("tokenNo:");
			sb.append(this.tokenNo);
			first = false;
			sb.append(")");
			return sb.toString();
		}

		public void validate() throws org.apache.thrift.TException {
			// check for required fields
		}

		private void writeObject(java.io.ObjectOutputStream out)
				throws java.io.IOException {
			try {
				write(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(out)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private void readObject(java.io.ObjectInputStream in)
				throws java.io.IOException, ClassNotFoundException {
			try {
				read(new org.apache.thrift.protocol.TCompactProtocol(
						new org.apache.thrift.transport.TIOStreamTransport(in)));
			} catch (org.apache.thrift.TException te) {
				throw new java.io.IOException(te);
			}
		}

		private static class postRebuildHashTreeResponse_argsStandardSchemeFactory
				implements SchemeFactory {
			public postRebuildHashTreeResponse_argsStandardScheme getScheme() {
				return new postRebuildHashTreeResponse_argsStandardScheme();
			}
		}

		private static class postRebuildHashTreeResponse_argsStandardScheme
				extends StandardScheme<postRebuildHashTreeResponse_args> {

			public void read(org.apache.thrift.protocol.TProtocol iprot,
					postRebuildHashTreeResponse_args struct)
					throws org.apache.thrift.TException {
				org.apache.thrift.protocol.TField schemeField;
				iprot.readStructBegin();
				while (true) {
					schemeField = iprot.readFieldBegin();
					if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
						break;
					}
					switch (schemeField.id) {
					case 1: // SN
						if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
							struct.sn = new ServerName();
							struct.sn.read(iprot);
							struct.setSnIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					case 2: // TREE_ID
						if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
							struct.treeId = iprot.readI64();
							struct.setTreeIdIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					case 3: // TOKEN_NO
						if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
							struct.tokenNo = iprot.readI64();
							struct.setTokenNoIsSet(true);
						} else {
							org.apache.thrift.protocol.TProtocolUtil.skip(
									iprot, schemeField.type);
						}
						break;
					default:
						org.apache.thrift.protocol.TProtocolUtil.skip(iprot,
								schemeField.type);
					}
					iprot.readFieldEnd();
				}
				iprot.readStructEnd();

				// check for required fields of primitive type, which can't be
				// checked in the validate method
				struct.validate();
			}

			public void write(org.apache.thrift.protocol.TProtocol oprot,
					postRebuildHashTreeResponse_args struct)
					throws org.apache.thrift.TException {
				struct.validate();

				oprot.writeStructBegin(STRUCT_DESC);
				if (struct.sn != null) {
					oprot.writeFieldBegin(SN_FIELD_DESC);
					struct.sn.write(oprot);
					oprot.writeFieldEnd();
				}
				oprot.writeFieldBegin(TREE_ID_FIELD_DESC);
				oprot.writeI64(struct.treeId);
				oprot.writeFieldEnd();
				oprot.writeFieldBegin(TOKEN_NO_FIELD_DESC);
				oprot.writeI64(struct.tokenNo);
				oprot.writeFieldEnd();
				oprot.writeFieldStop();
				oprot.writeStructEnd();
			}

		}

		private static class postRebuildHashTreeResponse_argsTupleSchemeFactory
				implements SchemeFactory {
			public postRebuildHashTreeResponse_argsTupleScheme getScheme() {
				return new postRebuildHashTreeResponse_argsTupleScheme();
			}
		}

		private static class postRebuildHashTreeResponse_argsTupleScheme extends
				TupleScheme<postRebuildHashTreeResponse_args> {

			@Override
			public void write(org.apache.thrift.protocol.TProtocol prot,
					postRebuildHashTreeResponse_args struct)
					throws org.apache.thrift.TException {
				TTupleProtocol oprot = (TTupleProtocol) prot;
				BitSet optionals = new BitSet();
				if (struct.isSetSn()) {
					optionals.set(0);
				}
				if (struct.isSetTreeId()) {
					optionals.set(1);
				}
				if (struct.isSetTokenNo()) {
					optionals.set(2);
				}
				oprot.writeBitSet(optionals, 3);
				if (struct.isSetSn()) {
					struct.sn.write(oprot);
				}
				if (struct.isSetTreeId()) {
					oprot.writeI64(struct.treeId);
				}
				if (struct.isSetTokenNo()) {
					oprot.writeI64(struct.tokenNo);
				}
			}

			@Override
			public void read(org.apache.thrift.protocol.TProtocol prot,
					postRebuildHashTreeResponse_args struct)
					throws org.apache.thrift.TException {
				TTupleProtocol iprot = (TTupleProtocol) prot;
				BitSet incoming = iprot.readBitSet(3);
				if (incoming.get(0)) {
					struct.sn = new ServerName();
					struct.sn.read(iprot);
					struct.setSnIsSet(true);
				}
				if (incoming.get(1)) {
					struct.treeId = iprot.readI64();
					struct.setTreeIdIsSet(true);
				}
				if (incoming.get(2)) {
					struct.tokenNo = iprot.readI64();
					struct.setTokenNoIsSet(true);
				}
			}
		}

	}

}
