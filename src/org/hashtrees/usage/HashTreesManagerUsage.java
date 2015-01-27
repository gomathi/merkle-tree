package org.hashtrees.usage;

import java.io.IOException;

import org.hashtrees.HashTrees;
import org.hashtrees.HashTreesIdProvider;
import org.hashtrees.SimpleTreeIdProvider;
import org.hashtrees.manager.EmptySyncListProvider;
import org.hashtrees.manager.HashTreesManager;
import org.hashtrees.manager.HashTreesSynchListProvider;

/**
 * This class provides an example how to build an instance of
 * {@link HashTreesManager}, and applying various operations on it.
 * 
 * First look at {@link HashTreesUsage} class for using {@link HashTrees}, as
 * this class depends upon it, it would be better if you read that class first.
 */

public class HashTreesManagerUsage {

	/**
	 * First of all {@link HashTreesManager} requires an instance of
	 * {@link HashTreesIdProvider} to know which hashtrees to rebuild.
	 * 
	 * In this example, we are just returning a {@link SimpleTreeIdProvider}
	 * which returns always treeId as 1.
	 * 
	 * @return
	 */
	public static HashTreesIdProvider createHashTreesIdProvider() {
		return new SimpleTreeIdProvider();
	}

	/**
	 * {@link HashTreesManager} requires a synch list provider instance, to know
	 * which servers to synch given a treeId. Since the synch list might change
	 * dynamically, managing servers list was left to the API users.
	 * 
	 * In this example, returns an empty collection of sync servers for any
	 * given treeId.
	 * 
	 * @return
	 */
	public static HashTreesSynchListProvider createSynchListProvider() {
		return new EmptySyncListProvider();
	}

	/**
	 * This example creates a HashTreesManager, and uses
	 * {@link #createHashTreesIdProvider()} {@link #createSynchListProvider()}
	 * for treeIdProvider and syncListProvider. Also uses
	 * {@link HashTreesUsage#buildStore()} for creating {@link HashTrees}.
	 * 
	 * There are various functions provided by {@link HashTreesManager.Builder}
	 * like {@link HashTreesManager.Builder#schedule(long)} to schedule how
	 * frequently to rebuild/synch.
	 * 
	 * @param serverName
	 *            , where the server will be running.
	 * @param portNo
	 *            , where the hashtrees server will be listening.
	 * @return
	 * @throws IOException
	 */
	public static HashTreesManager createHashTreesManager(String serverName,
			int portNo) throws IOException {
		return new HashTreesManager.Builder(serverName, portNo,
				HashTreesUsage.buildHashTrees(HashTreesUsage.buildStore()),
				createHashTreesIdProvider(), createSynchListProvider()).build();
	}

	/**
	 * {@link HashTreesManager} will start a background server to talk to other
	 * HashTrees, and also schedules rebuild and synch operations. All these
	 * operations will be started only after {@link HashTreesManager#start()}
	 * triggered for thread safety.
	 * 
	 * @param manager
	 */
	public static void startHashTreesManager(HashTreesManager manager) {
		manager.start();
	}
}
