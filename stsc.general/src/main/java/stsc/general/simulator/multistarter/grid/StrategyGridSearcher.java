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
import stsc.general.statistic.StrategySelector;
import stsc.general.strategy.TradingStrategy;

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
	private final StrategySelector selector;

	private class StatisticsCalculationThread extends Thread {

		private final Iterator<SimulatorSettings> iterator;
		private final StrategySelector selector;
		private boolean stoppedByRequest;

		public StatisticsCalculationThread(final Iterator<SimulatorSettings> iterator, final StrategySelector selector) {
			this.iterator = iterator;
			this.selector = selector;
			this.stoppedByRequest = false;
		}

		@Override
		public void run() {
			SimulatorSettings settings = getNextSimulatorSettings();

			while (settings != null) {
				Simulator simulator;
				try {
					simulator = new Simulator(settings);
					final TradingStrategy strategy = new TradingStrategy(settings, simulator.getStatistics());
					selector.addStrategy(strategy);
					settings = getNextSimulatorSettings();
				} catch (BadAlgorithmException | BadSignalException e) {
					logger.error("Error while calculating statistics: " + e.getMessage());
				}
			}
		}

		private SimulatorSettings getNextSimulatorSettings() {
			synchronized (iterator) {
				while (!stoppedByRequest && iterator.hasNext()) {
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

		public void stopSearch() {
			this.stoppedByRequest = true;
		}
	}

	final List<StatisticsCalculationThread> threads = new ArrayList<>();

	public StrategyGridSearcher(final Iterable<SimulatorSettings> iterable, final StrategySelector selector, int threadAmount) {
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
	public void stopSearch() {
		for (StatisticsCalculationThread t : threads) {
			t.stopSearch();
		}
	}

	@Override
	public StrategySelector getSelector() throws StrategySearcherException {
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
