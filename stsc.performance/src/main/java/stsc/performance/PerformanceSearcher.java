package stsc.performance;

import stsc.common.TimeTracker;

interface PerformanceSearcher {

	static final public class PerformanceResult {
		TimeTracker timeTracker;
		double sumAvGainForBest;

		public PerformanceResult(TimeTracker tt, double sum) {
			this.timeTracker = tt;
			this.sumAvGainForBest = sum;
		}
	}

	public PerformanceResult search() throws Exception;
}