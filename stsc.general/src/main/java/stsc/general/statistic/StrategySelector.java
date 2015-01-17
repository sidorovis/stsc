package stsc.general.statistic;

import java.util.List;
import java.util.Optional;

import stsc.general.strategy.TradingStrategy;

public interface StrategySelector {

	public Optional<TradingStrategy> addStrategy(final TradingStrategy strategy);

	public void removeStrategy(TradingStrategy strategy);

	public List<TradingStrategy> getStrategies();

	public int size();
}
