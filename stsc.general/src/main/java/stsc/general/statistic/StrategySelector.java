package stsc.general.statistic;

import java.util.List;

import stsc.general.strategy.TradingStrategy;

public interface StrategySelector {

	public TradingStrategy addStrategy(final TradingStrategy strategy);

	public void removeStrategy(TradingStrategy strategy);

	public List<TradingStrategy> getStrategies();

	public int size();
}
