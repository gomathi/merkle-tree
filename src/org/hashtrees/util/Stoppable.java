package org.hashtrees.util;

/**
 * Indicates a class that accepts a stop signal, and stop performing a task, or
 * cleans the resources on getting a stop signal.
 * 
 */

public interface Stoppable {

	void stop();
}
