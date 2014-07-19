package stsc.general.statistic;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import stsc.general.statistic.cost.comparator.StatisticsComparator;

public class StatisticsCompareSelector implements StatisticsSelector {

	private int selectLastElements;
	private final TreeSet<Statistics> select;

	public StatisticsCompareSelector(int selectLastElements, StatisticsComparator comparator) {
		this.selectLastElements = selectLastElements;
		this.select = new TreeSet<Statistics>(comparator);
	}

	@Override
	public synchronized boolean addStatistics(final Statistics statistics) {
		select.add(statistics);
		if (select.size() > selectLastElements) {
			final Statistics deleted = select.pollLast();
			if (deleted == statistics) {
				return false;
			}
		}
		return true;
	}

	@Override
	public synchronized List<Statistics> getStatistics() {
		final List<Statistics> result = new LinkedList<>();
		for (Statistics i : select) {
			result.add(i);
		}
		return Collections.unmodifiableList(result);
	}

}
