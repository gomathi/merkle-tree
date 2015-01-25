package org.hashtrees;

import com.google.common.base.MoreObjects;

public class SyncDiffResult {
	public final int totKeyDifferences, totExtrinsicSegments;

	public SyncDiffResult(int totKeyDifferences, int totExtrinsicSegments) {
		this.totKeyDifferences = totKeyDifferences;
		this.totExtrinsicSegments = totExtrinsicSegments;
	}

	public boolean isAnyUpdatesMade() {
		return (totKeyDifferences > 0) || (totExtrinsicSegments > 0);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("totKeyDifferences", totKeyDifferences)
				.add("totExtrinsicSegments", totExtrinsicSegments).toString();
	}
}
