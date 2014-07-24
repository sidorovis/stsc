package stsc.general.simulator.multistarter;

import stsc.general.statistic.StrategySelector;

public interface StrategySearcher {
	public StrategySelector getSelector() throws StrategySearcherException;
}
