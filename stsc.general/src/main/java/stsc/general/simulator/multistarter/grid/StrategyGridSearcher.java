package stsc.general.simulator.multistarter.grid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.BadSignalException;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.StrategySearcher;
import stsc.general.simulator.multistarter.StrategySearcherException;
import stsc.general.statistic.StatisticsCalculationException;
import stsc.general.statistic.StatisticsSelector;

/**
 * Multithread Strategy Grid Searcher
 * 
 * @author rilley_elf
 * 
 */
public class StrategyGridSearcher implements StrategySearcher {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/mt_strategy_grid_searcher_log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger("StrategyGridSearcher");

	private final Set<String> processedSettings = new HashSet<>();
	private final StatisticsSelector selector;

	private class StatisticsCalculationThread extends Thread {

		final Iterator<SimulatorSettings> iterator;
		final StatisticsSelector selector;

		public StatisticsCalculationThread(final Iterator<SimulatorSettings> iterator, final StatisticsSelector selector) {
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
				while (iterator.hasNext()) {
					final SimulatorSettings nextValue = iterator.next();
					if (nextValue == null)
						return null;
					final String hashCode = nextValue.stringHashCode();
					if (processedSettings.contains(hashCode)) {
						logger.debug("Already resolved: " + hashCode);
						continue;
					} else {
						processedSettings.add(hashCode);
					}
					return nextValue;
				}
			}
			return null;
		}
	}

	final List<StatisticsCalculationThread> threads = new ArrayList<>();

	public StrategyGridSearcher(final Iterable<SimulatorSettings> iterable, final StatisticsSelector selector, int threadAmount) {
		this.selector = selector;
		final Iterator<SimulatorSettings> iterator = iterable.iterator();
		logger.debug("Starting");

		for (int i = 0; i < threadAmount; ++i) {
			threads.add(new StatisticsCalculationThread(iterator, selector));
		}
		for (Thread t : threads) {
			t.start();
		}
		logger.debug("Finishing");
	}

	@Override
	public StatisticsSelector getSelector() throws StrategySearcherException {
		try {
			for (Thread t : threads) {
				t.join();
			}
		} catch (InterruptedException e) {
			throw new StrategySearcherException(e.getMessage());
		}
		return selector;
	}
}
