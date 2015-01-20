package org.hashtrees;

/**
 * This interface provides an option to hook into {@link HashTrees} internal
 * methods.
 * 
 */

public interface HashTreesListener {

	/**
	 * Called before
	 * {@link HashTrees#hPut(java.nio.ByteBuffer, java.nio.ByteBuffer)}. The
	 * implementations can change the putEntry to update what key, and value for
	 * hPut call to use.
	 * 
	 * @param putEntry
	 */
	void preHPut(PutEntry putEntry);

	/**
	 * Called after finishing up
	 * {@link HashTrees#hPut(java.nio.ByteBuffer, java.nio.ByteBuffer)}. This
	 * can be used to bookkeeping about upto which key have been updated into
	 * HashTrees.
	 * 
	 * @param putEntry
	 */
	void postHPut(PutEntry putEntry);

	/**
	 * Called before {@link HashTrees#hRemove(java.nio.ByteBuffer)}. The
	 * implementations can change the removeEntry to update what key for hRemove
	 * call to use.
	 * 
	 * @param removeEntry
	 */
	void preHRemove(RemoveEntry removeEntry);

	/**
	 * Called after finishing up {@link HashTrees#hRemove(java.nio.ByteBuffer)}
	 * .This can be used to bookkeeping about upto which key have been updated
	 * into HashTrees.
	 * 
	 * @param removeEntry
	 */
	void postHRemove(RemoveEntry removeEntry);
}
