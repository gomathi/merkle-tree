package org.hashtrees;

public class SyncDiffResult {
	public final int totKeyDifferences, totMissingSegments,
			totExtrinsicSegments;

	public SyncDiffResult(int totKeyDifferences, int totMissingSegments,
			int totExtrinsicSegments) {
		this.totKeyDifferences = totKeyDifferences;
		this.totMissingSegments = totMissingSegments;
		this.totExtrinsicSegments = totExtrinsicSegments;
	}

	public boolean isAnyUpdatesMade() {
		return (totKeyDifferences > 0) || (totMissingSegments > 0)
				|| (totExtrinsicSegments > 0);
	}
}
