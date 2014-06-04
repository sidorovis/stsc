package stsc.general.statistic;

import java.util.Set;
import java.util.TreeSet;

import stsc.general.statistic.cost.comparator.StatisticsComparator;

public class StatisticsCompareSelector<T> {

	private int selectLastElements;
	private final TreeSet<Statistics> select;

	public StatisticsCompareSelector(int selectLastElements, StatisticsComparator comparator) {
		this.selectLastElements = selectLastElements;
		this.select = new TreeSet<Statistics>(comparator);
	}

	public synchronized void addStatistics(final Statistics statistics) {
		select.add(statistics);
		if (select.size() > selectLastElements) {
			select.pollLast();
		}
	}

	public synchronized Set<Statistics> getSortedStatistics() {
		return select;
	}

}
