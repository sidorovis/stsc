package stsc.performance;

import java.util.List;

import stsc.common.TimeTracker;

class GeneticSearcher implements PerformanceSearcher {
	private final PerformanceCalculator parent;
	private final List<String> openTypes;
	private final int threadSize;
	private final String endOfPeriod;

	GeneticSearcher(PerformanceCalculator parent, final List<String> openTypes, int threadSize, String endOfPeriod) {
		this.parent = parent;
		this.openTypes = openTypes;
		this.threadSize = threadSize;
		this.endOfPeriod = endOfPeriod;
	}

	public TimeTracker search() throws Exception {
		return PerformanceCalculator.timeForGeneticSearch(parent.stockStorage, openTypes, threadSize, endOfPeriod);
	}
}