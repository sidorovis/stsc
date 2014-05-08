package stsc.simulator.multistarter.grid;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.algorithms.BadAlgorithmException;
import stsc.signals.BadSignalException;
import stsc.simulator.Simulator;
import stsc.simulator.SimulatorSettings;
import stsc.statistic.Statistics;
import stsc.statistic.StatisticsCalculationException;
import stsc.statistic.StatisticsSelector;

/**
 * Single thread simulator settings grid searcher
 * 
 * @author rilley_elf
 * 
 */
public class StrategyGridSearcher {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY,
				"./config/strategy_grid_searcher_log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger("StrategyGridSearcher");

	private final Set<Integer> processedSettings = new HashSet<>();
	private final StatisticsSelector<Double> selector;

	public StrategyGridSearcher(final Iterable<SimulatorSettings> iterator, final StatisticsSelector<Double> selector)
			throws BadAlgorithmException, StatisticsCalculationException, BadSignalException {
		this.selector = selector;
		logger.debug("StrategyGridSearcher starting");

		int i = 1;
		for (SimulatorSettings settings : iterator) {
			final int currentHashCode = settings.hashCode();
			if (processedSettings.contains(currentHashCode)) {
				continue;
			} else {
				processedSettings.add(currentHashCode);
			}
			final Simulator simulator = new Simulator(settings);
			final Statistics s = simulator.getStatistics();
			selector.addStatistics(s);
			if (i % 5000 == 0) {
				logger.debug("Processed: " + String.valueOf(i));
			}
			++i;
		}
		logger.debug("StrategyGridSearcher stopping");
	}

	public StatisticsSelector<Double> getSelector() {
		return selector;
	}
}
