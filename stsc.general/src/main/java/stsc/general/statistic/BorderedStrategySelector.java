package stsc.general.statistic;

import java.util.List;

import stsc.general.strategy.TradingStrategy;

public abstract class BorderedStrategySelector implements StrategySelector {

	private final int selectLastElements;

	protected BorderedStrategySelector(final int selectLastElements) {
		this.selectLastElements = selectLastElements;
	}

	public abstract TradingStrategy addStrategy(final TradingStrategy strategy);

	public abstract void removeStrategy(TradingStrategy strategy);
	
	public abstract List<TradingStrategy> getStrategies();

	public int size() {
		return selectLastElements;
	}

}
