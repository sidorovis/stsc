package stsc.simulator.multistarter.grid;

import java.util.ArrayList;
import java.util.List;

import stsc.simulator.Simulator;
import stsc.simulator.SimulatorSettings;
import stsc.statistic.Statistics;
import stsc.statistic.StatisticsSelector;

/**
 * Multithread Strategy Grid Searcher
 * 
 * @author rilley_elf
 * 
 */
public class MtStrategyGridSearcher {

	private final StatisticsSelector<Double> selector;
	private int allThreads;
	private int finishedThreads;

	List<Thread> threads = new ArrayList<>();

	public MtStrategyGridSearcher(final Iterable<SimulatorSettings> iterator,
			final StatisticsSelector<Double> selector, int threadAmount) throws Exception {
		this.selector = selector;
		this.allThreads = threadAmount;
		this.finishedThreads = 0;

		// for (SimulatorSettings settings : iterator) {
		// final Simulator simulator = new Simulator(settings);
		// final Statistics s = simulator.getStatistics();
		// selector.addStatistics(s);
		// }

	}

	public StatisticsSelector<Double> getSelector() {
		return selector;
	}
}
