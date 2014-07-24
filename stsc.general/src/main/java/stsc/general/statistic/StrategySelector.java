package stsc.general.statistic;

import java.util.List;

import stsc.general.strategy.Strategy;

public abstract class StrategySelector {

	private final int selectLastElements;

	protected StrategySelector(final int selectLastElements) {
		this.selectLastElements = selectLastElements;
	}

	public abstract boolean addStrategy(final Strategy strategy);

	public abstract List<Strategy> getStrategies();

	public int size() {
		return selectLastElements;
	}
}
