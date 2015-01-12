package org.hashtrees.store;

import java.util.List;

import org.hashtrees.synch.HashTreesManager;
import org.hashtrees.thrift.generated.RemoteTreeInfo;

/**
 * Used by {@link HashTreesManager} to persist information about which remote
 * hash trees to be synced.
 * 
 */
public interface HashTreesManagerStore {

	void addToSyncList(RemoteTreeInfo rTree);

	void removeFromSyncList(RemoteTreeInfo rTree);

	List<RemoteTreeInfo> getSyncList(long treeId);
}
