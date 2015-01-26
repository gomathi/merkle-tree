package org.hashtrees.manager;

import java.util.Collections;
import java.util.List;

import org.hashtrees.thrift.generated.ServerName;

public class EmptySyncListProvider implements HashTreesSynchListProvider {

	@Override
	public List<ServerName> getServerNameListFor(long treeId) {
		return Collections.emptyList();
	}

}
