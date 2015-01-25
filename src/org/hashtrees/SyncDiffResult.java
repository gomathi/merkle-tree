package org.hashtrees;

public class SyncDiffResult {
	public final int totKeyDifferences, totExtrinsicSegments;

	public SyncDiffResult(int totKeyDifferences, int totExtrinsicSegments) {
		this.totKeyDifferences = totKeyDifferences;
		this.totExtrinsicSegments = totExtrinsicSegments;
	}

	public boolean isAnyUpdatesMade() {
		return (totKeyDifferences > 0) || (totExtrinsicSegments > 0);
	}
}
