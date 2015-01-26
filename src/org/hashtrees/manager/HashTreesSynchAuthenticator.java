package org.hashtrees.manager;

import org.hashtrees.HashTrees;
import org.hashtrees.thrift.generated.ServerName;

/**
 * {@link HashTrees} usually have primary and backup database. When the synch
 * happens, the primary tries to replicate its state to backup database. If
 * accidentally primary is added to backup's synch list, then the source of
 * truth will be lost. To avoid that case, this interface defines method, where
 * the implementers can say whether synch is allowed to happen or not.
 * 
 */

public interface HashTreesSynchAuthenticator {

	/**
	 * 
	 * @param source
	 * @param dest
	 * @return
	 */
	boolean canSynch(ServerName source, ServerName dest);
}
