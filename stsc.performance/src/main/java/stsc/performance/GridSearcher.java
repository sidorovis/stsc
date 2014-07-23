package stsc.performance;

import java.util.List;

class GridSearcher implements PerformanceSearcher {
	private final PerformanceCalculator parent;
	private final List<String> openTypes;
	private final int threadSize;
	private final String endOfPeriod;

	GridSearcher(PerformanceCalculator parent, final List<String> openTypes, int threadSize, String endOfPeriod) {
		this.parent = parent;
		this.openTypes = openTypes;
		this.threadSize = threadSize;
		this.endOfPeriod = endOfPeriod;
	}

	public PerformanceResult search() throws Exception {
		return PerformanceCalculator.timeForGridSearch(parent.stockStorage, openTypes, threadSize, endOfPeriod);
	}
}