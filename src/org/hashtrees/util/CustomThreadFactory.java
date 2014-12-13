package org.hashtrees.util;

import java.util.concurrent.ThreadFactory;

public class CustomThreadFactory implements ThreadFactory {

	private final String threadGroupName;

	public CustomThreadFactory(String threadGroupName) {
		this.threadGroupName = threadGroupName;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r, threadGroupName);
		return thread;
	}
}