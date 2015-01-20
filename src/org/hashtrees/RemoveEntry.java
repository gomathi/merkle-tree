package org.hashtrees;

import java.nio.ByteBuffer;

public class RemoveEntry {

	private volatile ByteBuffer key;

	public RemoveEntry(ByteBuffer key) {
		this.key = key;
	}

	public ByteBuffer getKey() {
		return key;
	}

	public void setKey(ByteBuffer key) {
		this.key = key;
	}

}
