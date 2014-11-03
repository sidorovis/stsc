package stsc.general.simulator.multistarter;

import stsc.general.statistic.StrategySelector;

public interface StrategySearcher {

	static abstract public class IndicatorProgressListener {
		public abstract void processed(double percent);
	}

	public StrategySelector getSelector() throws StrategySearcherException;

	public void stopSearch();

	public void addIndicatorProgress(final IndicatorProgressListener listener);
}
