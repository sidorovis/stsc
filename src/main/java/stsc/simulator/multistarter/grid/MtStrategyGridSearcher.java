package stsc.simulator.multistarter.grid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.algorithms.BadAlgorithmException;
import stsc.signals.BadSignalException;
import stsc.simulator.Simulator;
import stsc.simulator.SimulatorSettings;
import stsc.statistic.StatisticsCalculationException;
import stsc.statistic.StatisticsSelector;

/**
 * Multithread Strategy Grid Searcher
 * 
 * @author rilley_elf
 * 
 */
public class MtStrategyGridSearcher {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY,
				"./config/mt_strategy_grid_searcher_log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger("MtStrategyGridSearcher");

	private final StatisticsSelector<Double> selector;

	private class StatisticsCalculationThread extends Thread {

		final Iterator<SimulatorSettings> iterator;
		final StatisticsSelector<Double> selector;

		public StatisticsCalculationThread(final Iterator<SimulatorSettings> iterator,
				final StatisticsSelector<Double> selector) {
			this.iterator = iterator;
			this.selector = selector;
		}

		@Override
		public void run() {
			SimulatorSettings settings = getNextSimulatorSettings();
			while (settings != null) {
				Simulator simulator;
				try {
					simulator = new Simulator(settings);
					selector.addStatistics(simulator.getStatistics());
					settings = getNextSimulatorSettings();
				} catch (BadAlgorithmException | StatisticsCalculationException | BadSignalException e) {
					logger.error("Error while calculating statistics: " + e.getMessage());
				}
			}
		}

		private SimulatorSettings getNextSimulatorSettings() {
			synchronized (iterator) {
				if (iterator.hasNext())
					return iterator.next();
			}
			return null;
		}
	}

	final List<StatisticsCalculationThread> threads = new ArrayList<>();

	public MtStrategyGridSearcher(final Iterable<SimulatorSettings> iterable,
			final StatisticsSelector<Double> selector, int threadAmount) {
		final Iterator<SimulatorSettings> iterator = iterable.iterator();
		this.selector = selector;
		logger.debug("Starting MtStrategyGridSearcher");

		for (int i = 0; i < threadAmount; ++i) {
			threads.add(new StatisticsCalculationThread(iterator, selector));
		}
		for (Thread t : threads) {
			t.start();
		}
		logger.debug("Finishing MtStrategyGridSearcher");
	}

	public StatisticsSelector<Double> getSelector() throws InterruptedException {
		for (Thread t : threads) {
			t.join();
		}
		return selector;
	}
}
