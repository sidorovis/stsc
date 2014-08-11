package stsc.general.statistic;

import java.util.List;

import stsc.general.strategy.TradingStrategy;

public abstract class StrategySelector {

	private final int selectLastElements;

	protected StrategySelector(final int selectLastElements) {
		this.selectLastElements = selectLastElements;
	}

	public abstract boolean addStrategy(final TradingStrategy strategy);

	public abstract List<TradingStrategy> getStrategies();

	public int size() {
		return selectLastElements;
	}
}
