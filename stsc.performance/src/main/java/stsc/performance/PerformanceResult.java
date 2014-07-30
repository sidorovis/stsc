package stsc.performance;

import stsc.common.TimeTracker;

final class PerformanceResult {
	TimeTracker timeTracker;
	double sumAvGainForBest;

	public PerformanceResult(TimeTracker tt, double sum) {
		this.timeTracker = tt;
		this.sumAvGainForBest = sum;
	}
}