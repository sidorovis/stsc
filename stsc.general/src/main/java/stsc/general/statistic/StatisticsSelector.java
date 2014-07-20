package stsc.general.statistic;

import java.util.List;

public abstract class StatisticsSelector {

	private final int selectLastElements;

	protected StatisticsSelector(final int selectLastElements) {
		this.selectLastElements = selectLastElements;
	}

	public abstract boolean addStatistics(final Statistics statistics);

	public abstract List<Statistics> getStatistics();

	public int size() {
		return selectLastElements;
	}
}
