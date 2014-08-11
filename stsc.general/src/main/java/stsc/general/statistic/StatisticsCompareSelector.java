package stsc.general.statistic;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import stsc.general.statistic.cost.comparator.StatisticsComparator;
import stsc.general.strategy.TradingStrategy;

public class StatisticsCompareSelector extends StrategySelector {

	private final class StrategyComparator implements Comparator<TradingStrategy> {
		private StatisticsComparator comparator;

		StrategyComparator(StatisticsComparator comparator) {
			this.comparator = comparator;
		}

		@Override
		public int compare(TradingStrategy o1, TradingStrategy o2) {
			return comparator.compare(o1.getStatistics(), o2.getStatistics());
		}

	}

	private final TreeSet<TradingStrategy> select;

	public StatisticsCompareSelector(int selectLastElements, StatisticsComparator comparator) {
		super(selectLastElements);
		this.select = new TreeSet<TradingStrategy>(new StrategyComparator(comparator));
	}

	@Override
	public synchronized boolean addStrategy(final TradingStrategy strategy) {
		select.add(strategy);
		if (select.size() > size()) {
			final TradingStrategy deleted = select.pollLast();
			if (deleted == strategy) {
				return false;
			}
		}
		return true;
	}

	@Override
	public synchronized List<TradingStrategy> getStrategies() {
		final List<TradingStrategy> result = new LinkedList<>();
		for (TradingStrategy i : select) {
			result.add(i);
		}
		return Collections.unmodifiableList(result);
	}

}
