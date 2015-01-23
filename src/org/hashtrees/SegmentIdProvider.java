package org.hashtrees;


/**
 * A segment is a leaf block in a hashtree. Which segment should be used for a
 * given key is determined using this class.
 * 
 */
public interface SegmentIdProvider {

	int getSegmentId(byte[] key);
}