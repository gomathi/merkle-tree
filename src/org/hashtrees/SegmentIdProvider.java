package org.hashtrees;

import java.nio.ByteBuffer;

/**
 * A segment is a leaf block in a hashtree. Which segment should be used for a
 * given key is determined using this class.
 * 
 */
public interface SegmentIdProvider {

	int getSegmentId(ByteBuffer key);
}