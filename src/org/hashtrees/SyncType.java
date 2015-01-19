package org.hashtrees;

public enum SyncType {
	UPDATE // Updates the remote hash tree on finding differences
	, FIND_DIFF_ONLY // Only finds the differences between remote hash tree and
						// local tree.
}
