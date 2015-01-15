package org.hashtrees;

import java.nio.ByteBuffer;

import org.hashtrees.store.Store;

/**
 * {@link HashTrees} stores only key,digest in its storage, using that it will
 * figure out differences between two storages. Whenever there is an update for
 * a value, {@link HashTrees} need to fetch the entire value.
 * 
 * Rather than hitting {@link Store} every time, hashtrees can store values in
 * its storage, and whenever an update is coming to a value, it can ask the
 * application to provide new value based on value that already exist on
 * hashtrees storage, and the incoming update.
 * 
 */
public interface HTValueConverter {

	/**
	 * {@link HashTrees} will call this method whenever its
	 * {@link HashTrees#hPut(ByteBuffer, ByteBuffer)} is called. Based on the
	 * return value, it will calculate the digest.
	 * 
	 * @param prevValue
	 * @param update
	 * @return
	 */
	ByteBuffer apply(ByteBuffer prevValue, ByteBuffer update);
}
