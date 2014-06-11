package stsc.general.statistic;

import java.util.List;

public interface StatisticsSelector {

	public void addStatistics(final Statistics statistics);

	public List<Statistics> getStatistics();
}
