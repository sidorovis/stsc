package stsc.general.simulator.multistarter.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import com.google.common.math.DoubleMath;

import stsc.common.Settings;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.StrategySearcher;
import stsc.general.simulator.multistarter.StrategySearcherException;
import stsc.general.statistic.Statistics;
import stsc.general.statistic.StatisticsSelector;
import stsc.general.statistic.cost.function.CostFunction;
import stsc.general.statistic.cost.function.WeightedSumCostFunction;

public class StrategyGeneticSearcher implements StrategySearcher {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/strategy_genetic_searcher_log4j2.xml");
	}

	class PopulationElement {
		SimulatorSettings settings;

		public PopulationElement(SimulatorSettings settings, Statistics statistics) {
			super();
			this.settings = settings;
			this.statistics = statistics;
		}

		Statistics statistics;
	}

	private static Logger logger = LogManager.getLogger("StrategyGeneticSearcher");

	private int populationSize = 100;
	private double crossoverPart = 0.8;

	private int maxSelectionSize = 1000;
	private int currentSelectionIndex = 0;

	private final StatisticsSelector selector;
	private final SimulatorSettingsGeneticList algorithmSettings;
	private List<PopulationElement> population;

	private final CostFunction costFunction;

	private final ExecutorService executor;
	private CountDownLatch countDownLatch;

	public StrategyGeneticSearcher(final StatisticsSelector selector, SimulatorSettingsGeneticList algorithmSettings, int threadAmount)
			throws InterruptedException {
		this(selector, algorithmSettings, threadAmount, new WeightedSumCostFunction());
	}

	public StrategyGeneticSearcher(final StatisticsSelector selector, SimulatorSettingsGeneticList algorithmSettings, int threadAmount,
			CostFunction costFunction) throws InterruptedException {
		this.selector = selector;
		this.algorithmSettings = algorithmSettings;
		this.population = Collections.synchronizedList(new ArrayList<PopulationElement>());
		this.executor = Executors.newFixedThreadPool(threadAmount);

		this.costFunction = costFunction;
		this.countDownLatch = new CountDownLatch(populationSize);

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
				final SimulatorCalulatingTask task = new SimulatorCalulatingTask(searcher, searcher.getRandomSettings());
				executor.submit(task);
			}
		}

	}

	class SimulatorCalulatingTask implements Callable<Boolean> {

		private StrategyGeneticSearcher searcher;
		private SimulatorSettings settings;

		SimulatorCalulatingTask(StrategyGeneticSearcher searcher, SimulatorSettings settings) {
			this.searcher = searcher;
			this.settings = settings;
		}

		@Override
		public Boolean call() throws Exception {
			final Statistics statistics = simulate();
			countDownLatch.countDown();
			if (statistics != null) {
				searcher.population.add(new PopulationElement(settings, statistics));
				searcher.selector.addStatistics(statistics);
				return true;
			}
			return false;
		}

		private Statistics simulate() {
			try {
				Simulator simulator = new Simulator(settings);
				return simulator.getStatistics();
			} catch (Exception e) {
				logger.error("Error while calculating statistics: " + e.getMessage());
			}
			return null;
		}

	}

	@Override
	public StatisticsSelector getSelector() throws StrategySearcherException {
		try {
			waitResults();
		} catch (Exception e) {
			throw new StrategySearcherException(e.getMessage());
		}
		executor.shutdown();
		return selector;
	}

	private SimulatorSettings getRandomSettings() {
		return algorithmSettings.generateRandom();
	}

	private void waitResults() throws InterruptedException {
		while (currentSelectionIndex < maxSelectionSize) {
			countDownLatch.await();
			countDownLatch = new CountDownLatch(populationSize);
			geneticAlgorithmIteration();
		}
	}

	private void geneticAlgorithmIteration() {
		final List<PopulationElement> currentPopulation = population;
		population = Collections.synchronizedList(new ArrayList<PopulationElement>());

		final double lastCostSum = calculateCostSum();
		crossover(currentPopulation);
		mutation(currentPopulation);
		final double newCostSum = calculateCostSum();
		if (shouldTerminate(newCostSum, lastCostSum)) {
			currentSelectionIndex = maxSelectionSize;
			logger.debug("summary cost of statistics not changed throw iteration on valuable value");
		}
		currentSelectionIndex += 1;
	}

	private void crossover(final List<PopulationElement> currentPopulation) {
		final int size = currentPopulation.size();
		final int crossoverSize = (int) (populationSize * crossoverPart);
		if (size == 0) {
			return;
		}
		final Random r = new Random();

		for (int i = 0; i < crossoverSize; ++i) {
			final int leftIndex = r.nextInt(size);
			final int rightIndex = r.nextInt(size);

			final SimulatorSettings left = currentPopulation.get(leftIndex).settings;
			final SimulatorSettings right = currentPopulation.get(rightIndex).settings;
			final SimulatorSettings mergedStatistics = left.merge(right);

			executor.submit(new SimulatorCalulatingTask(this, mergedStatistics));
		}
	}

	private void mutation(final List<PopulationElement> currentPopulation) {
		final int size = currentPopulation.size();
		if (size == 0) {
			return;
		}
		final Random r = new Random();

		final int mutationSize = (int) (populationSize * (1 - crossoverPart));

		for (int i = 0; i < mutationSize; ++i) {
			final int index = r.nextInt(size);
			final SimulatorSettings settings = currentPopulation.get(index).settings;
			final SimulatorSettings mutatedSettings = settings.mutate();

			executor.submit(new SimulatorCalulatingTask(this, mutatedSettings));
		}
	}

	private boolean shouldTerminate(double newCostSum, double lastCostSum) {
		return DoubleMath.fuzzyEquals(newCostSum, lastCostSum, Settings.doubleEpsilon);
	}

	private double calculateCostSum() {
		double lastCostSum = 0.0;
		for (PopulationElement e : population) {
			lastCostSum += costFunction.calculate(e.statistics);
		}
		return lastCostSum;
	}
}
