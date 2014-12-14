package org.hashtrees.util;

import java.util.concurrent.ThreadFactory;

public class CustomThreadFactory implements ThreadFactory {

	private final String threadGroupName;
	private final boolean setDaemon;

	public CustomThreadFactory(String threadGroupName) {
		this(threadGroupName, false);
	}

	public CustomThreadFactory(String threadGroupName, boolean setDaemon) {
		this.threadGroupName = threadGroupName;
		this.setDaemon = setDaemon;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r, threadGroupName);
		thread.setDaemon(setDaemon);
		return thread;
	}
}