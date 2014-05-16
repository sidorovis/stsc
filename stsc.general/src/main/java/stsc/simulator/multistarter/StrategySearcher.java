package stsc.simulator.multistarter;

import stsc.statistic.StatisticsSelector;

public interface StrategySearcher<T> {
	public StatisticsSelector<T> getSelector() throws StrategySearcherException;
}
