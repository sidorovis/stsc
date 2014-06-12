package stsc.general.simulator.multistarter.genetic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.BadSignalException;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.StrategySearcher;
import stsc.general.simulator.multistarter.StrategySearcherException;
import stsc.general.statistic.Statistics;
import stsc.general.statistic.StatisticsCalculationException;
import stsc.general.statistic.StatisticsSelector;

public class StrategyGeneticSearcher implements StrategySearcher {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/mt_strategy_grid_searcher_log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger("StrategyGeneticSearcher");

	private int populationSize = 100;

	private int maxSelectionSize = 1000;
	private int currentSelectionIndex = 0;

	private final StatisticsSelector selector;
	private final SimulatorSettingsGeneticList algorithmSettings;
	private Map<SimulatorSettings, Statistics> population;

	private ExecutorService executor = Executors.newFixedThreadPool(5);

	public StrategyGeneticSearcher(final StatisticsSelector selector, SimulatorSettingsGeneticList algorithmSettings, int threadAmount)
			throws InterruptedException {
		this.selector = selector;
		this.algorithmSettings = algorithmSettings;
		this.population = new ConcurrentHashMap<>();
		this.executor = Executors.newFixedThreadPool(threadAmount);

		startSearcher();
	}

	private void startSearcher() {
		executor.execute(new GenerateInitialPopulationsTask(this));
	}

	class GenerateInitialPopulationsTask implements Runnable {

		private StrategyGeneticSearcher searcher;

		GenerateInitialPopulationsTask(StrategyGeneticSearcher searcher) {
			this.searcher = searcher;
		}

		@Override
		public void run() {
			for (int i = 0; i < populationSize; ++i) {
				final SimulatorCalulatingTask task = new SimulatorCalulatingTask(searcher);
				executor.execute(task);
			}
		}

	}

	class SimulatorCalulatingTask implements Runnable {

		private StrategyGeneticSearcher searcher;

		SimulatorCalulatingTask(StrategyGeneticSearcher searcher) {
			this.searcher = searcher;
		}

		@Override
		public void run() {
			final SimulatorSettings settings = searcher.algorithmSettings.generateRandom();
			final Statistics statistics = simulate(settings);
			searcher.population.put(settings, statistics);
			searcher.selector.addStatistics(statistics);
		}

		private Statistics simulate(SimulatorSettings settings) {
			try {
				Simulator simulator = new Simulator(settings);
				return simulator.getStatistics();
			} catch (BadAlgorithmException | StatisticsCalculationException | BadSignalException e) {
				logger.error("Error while calculating statistics: " + e.getMessage());
			}
			return null;
		}

	}

	@Override
	public StatisticsSelector getSelector() throws StrategySearcherException {
		try {
			waitResults();
		} catch (InterruptedException e) {
			throw new StrategySearcherException(e.getMessage());
		}
		return selector;
	}

	private void waitResults() throws InterruptedException {
		while (currentSelectionIndex < maxSelectionSize) {
			executor.wait();

			final Map<SimulatorSettings, Statistics> currentPopulation = population;
			population = new ConcurrentHashMap<>();

			
			
			currentSelectionIndex += 1;
		}
	}
}
