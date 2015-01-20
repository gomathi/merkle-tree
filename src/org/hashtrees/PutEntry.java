package org.hashtrees;

import java.nio.ByteBuffer;

public class PutEntry {

	private volatile ByteBuffer key, value;

	public PutEntry(ByteBuffer key, ByteBuffer value) {
		this.key = key;
		this.value = value;
	}

	public ByteBuffer getKey() {
		return key;
	}

	public ByteBuffer getValue() {
		return value;
	}

	public void setKey(ByteBuffer key) {
		this.key = key;
	}

	public void setValue(ByteBuffer value) {
		this.value = value;
	}
}
