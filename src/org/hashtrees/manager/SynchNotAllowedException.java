package org.hashtrees.manager;

import org.hashtrees.thrift.generated.ServerName;

public class SynchNotAllowedException extends Exception {

	private static final long serialVersionUID = 1L;

	public SynchNotAllowedException(ServerName src, ServerName dest) {
		super("Synch is not allowed from " + src + " to " + dest);
	}
}
