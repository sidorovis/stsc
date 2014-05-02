package stsc.simulator.multistarter.grid;

import stsc.simulator.Simulator;
import stsc.simulator.SimulatorSettings;
import stsc.statistic.Statistics;
import stsc.statistic.StatisticsSelector;

/**
 * Single thread simulator settings grid searcher
 * 
 * @author rilley_elf
 * 
 */
public class StrategyGridSearcher {

	private final StatisticsSelector<Double> selector;

	public StrategyGridSearcher(final Iterable<SimulatorSettings> iterator,
			final StatisticsSelector<Double> selector) throws Exception {
		this.selector = selector;

		for (SimulatorSettings settings : iterator) {
			final Simulator simulator = new Simulator(settings);
			final Statistics s = simulator.getStatistics();
			selector.addStatistics(s);
		}
	}

	public StatisticsSelector<Double> getSelector() {
		return selector;
	}
}
