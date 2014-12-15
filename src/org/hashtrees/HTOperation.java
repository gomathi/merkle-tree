package org.hashtrees;

/**
 * Used to tag type of operation when the input is fed into the non blocking
 * version of {@link HashTreesImpl} hPut and hRemove methods.
 * 
 */
enum HTOperation {
	PUT, REMOVE
}
