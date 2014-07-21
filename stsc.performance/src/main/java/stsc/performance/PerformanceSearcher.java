package stsc.performance;

import stsc.common.TimeTracker;

interface PerformanceSearcher {
	public TimeTracker search() throws Exception;
}