package stsc.general.statistic;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;

import stsc.general.statistic.cost.comparator.CostStatisticsComparator;
import stsc.general.strategy.TradingStrategy;

public class StatisticsCompareSelector extends BorderedStrategySelector {

	private final class StrategyComparator implements Comparator<TradingStrategy> {
		private CostStatisticsComparator comparator;

		StrategyComparator(CostStatisticsComparator comparator) {
			this.comparator = comparator;
		}

		@Override
		public int compare(TradingStrategy o1, TradingStrategy o2) {
			return comparator.compare(o1.getStatistics(), o2.getStatistics());
		}

	}

	private final StrategyComparator strategyComparator;
	private final TreeSet<TradingStrategy> select;

	public StatisticsCompareSelector(int selectLastElements, CostStatisticsComparator comparator) {
		super(selectLastElements);
		this.strategyComparator = new StrategyComparator(comparator);
		this.select = new TreeSet<TradingStrategy>(strategyComparator);
	}

	@Override
	public synchronized Optional<TradingStrategy> addStrategy(final TradingStrategy strategy) {
		select.add(strategy);
		if (select.size() > size()) {
			return Optional.of(select.pollLast());
		}
		return Optional.empty();
	}

	@Override
	public synchronized void removeStrategy(final TradingStrategy strategy) {
		select.remove(strategy);
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
