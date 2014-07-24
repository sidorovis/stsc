package stsc.general.statistic;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import stsc.general.statistic.cost.comparator.StatisticsComparator;
import stsc.general.strategy.Strategy;

public class StatisticsCompareSelector extends StrategySelector {

	private final class StrategyComparator implements Comparator<Strategy> {
		private StatisticsComparator comparator;

		StrategyComparator(StatisticsComparator comparator) {
			this.comparator = comparator;
		}

		@Override
		public int compare(Strategy o1, Strategy o2) {
			return comparator.compare(o1.getStatistics(), o2.getStatistics());
		}

	}

	private final TreeSet<Strategy> select;

	public StatisticsCompareSelector(int selectLastElements, StatisticsComparator comparator) {
		super(selectLastElements);
		this.select = new TreeSet<Strategy>(new StrategyComparator(comparator));
	}

	@Override
	public synchronized boolean addStrategy(final Strategy strategy) {
		select.add(strategy);
		if (select.size() > size()) {
			final Strategy deleted = select.pollLast();
			if (deleted == strategy) {
				return false;
			}
		}
		return true;
	}

	@Override
	public synchronized List<Strategy> getStrategies() {
		final List<Strategy> result = new LinkedList<>();
		for (Strategy i : select) {
			result.add(i);
		}
		return Collections.unmodifiableList(result);
	}

}
