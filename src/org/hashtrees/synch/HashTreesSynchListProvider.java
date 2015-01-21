package org.hashtrees.synch;

import java.util.List;

import org.hashtrees.thrift.generated.ServerName;

/**
 * Used by {@link HashTreesManager} to know which remote hash trees have to be
 * synced.
 * 
 */
public interface HashTreesSynchListProvider {

	public List<ServerName> getServerNameListFor(long treeId);
}
