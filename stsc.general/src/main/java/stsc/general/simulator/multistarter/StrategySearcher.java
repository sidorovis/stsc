package stsc.general.simulator.multistarter;

import stsc.general.statistic.StatisticsSelector;

public interface StrategySearcher<T> {
	public StatisticsSelector<T> getSelector() throws StrategySearcherException;
}
