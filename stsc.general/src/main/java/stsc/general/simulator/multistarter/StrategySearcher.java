package stsc.general.simulator.multistarter;

import stsc.general.statistic.StatisticsSelector;

public interface StrategySearcher {
	public StatisticsSelector getSelector() throws StrategySearcherException;
}
