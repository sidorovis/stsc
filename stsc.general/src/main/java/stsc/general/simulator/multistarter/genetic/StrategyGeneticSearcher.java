package stsc.general.simulator.multistarter.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import com.google.common.math.DoubleMath;

import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.StrategySearcher;
import stsc.general.simulator.multistarter.StrategySearcherException;
import stsc.general.statistic.StrategySelector;
import stsc.general.statistic.cost.function.CostFunction;
import stsc.general.statistic.cost.function.CostWeightedSumFunction;
import stsc.general.strategy.TradingStrategy;

public class StrategyGeneticSearcher implements StrategySearcher {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/strategy_genetic_searcher_log4j2.xml");
	}

	static Logger logger = LogManager.getLogger("StrategyGeneticSearcher");

	private final static int MINIMUM_STEPS_AMOUNT = 10;

	// empirical by StrategyGeneticSearcherTest
	// (see commit # a43c64c01d765f266b8bfae5bb3c3a1a58e4bf24)
	public final static double BEST_DEFAULT_PART = 0.94;

	// empirical by StrategyGeneticSearcherTest
	// (see commit # a43c64c01d765f266b8bfae5bb3c3a1a58e4bf24)
	public final static double CROSSOVER_DEFAULT_PART = 0.86;
	public final static int POPULATION_DEFAULT_SIZE = 100;

	private int currentSelectionIndex = 0;
	private int lastSelectionIndex;

	private double maxCostSum = -Double.MAX_VALUE;

	private final SimulatorSettingsGeneticList settingsGeneticList;

	private final CostFunction costFunction;

	private final List<SimulatorCalulatingTask> simulatorCalculatingTasks;
	private final Random indexRandomizator = new Random();

	// should be visible to package for tasks
	final StrategySelector selector;
	List<TradingStrategy> population;
	final ExecutorService executor;
	CountDownLatch countDownLatch;
	final GeneticSearchSettings settings;
	// Boolean mean that Strategy was added as part of best strategies
	final Map<TradingStrategy, Boolean> sortedPopulation;

	private boolean stoppedByRequest = false;

	public StrategyGeneticSearcher(SimulatorSettingsGeneticList algorithmSettings, final StrategySelector selector, int threadAmount)
			throws InterruptedException {
		this(algorithmSettings, selector, threadAmount, selector.size(), POPULATION_DEFAULT_SIZE);
	}

	public StrategyGeneticSearcher(SimulatorSettingsGeneticList algorithmSettings, final StrategySelector selector, int threadAmount,
			int maxSelectionIndex, int populationSize) throws InterruptedException {
		this(algorithmSettings, selector, threadAmount, new CostWeightedSumFunction(), maxSelectionIndex, populationSize,
				BEST_DEFAULT_PART, CROSSOVER_DEFAULT_PART);
	}

	public StrategyGeneticSearcher(SimulatorSettingsGeneticList algorithmSettings, final StrategySelector selector, int threadAmount,
			CostFunction costFunction, int maxSelectionIndex, int populationSize, double bestPart, double crossoverPart)
			throws InterruptedException {
		this.selector = selector;
		this.settingsGeneticList = algorithmSettings;
		this.population = Collections.synchronizedList(new ArrayList<TradingStrategy>());
		this.sortedPopulation = Collections.synchronizedMap(new HashMap<TradingStrategy, Boolean>());
		this.executor = Executors.newFixedThreadPool(threadAmount);

		this.costFunction = costFunction;
		this.countDownLatch = new CountDownLatch(populationSize);
		this.simulatorCalculatingTasks = new ArrayList<>();

		this.settings = new GeneticSearchSettings(maxSelectionIndex, populationSize, bestPart, crossoverPart, selector.size());
		this.lastSelectionIndex = maxSelectionIndex;

		startSearcher();
	}

	private void startSearcher() {
		executor.submit(new GenerateInitialPopulationsTask(this, this));
	}

	@Override
	public StrategySelector getSelector() throws StrategySearcherException {
		try {
			waitResults();
		} catch (Exception e) {
			throw new StrategySearcherException(e.getMessage());
		}
		executor.shutdown();
		return selector;
	}

	SimulatorSettings getRandomSettings() throws BadAlgorithmException {
		return settingsGeneticList.generateRandom();
	}

	private void waitResults() throws InterruptedException {
		double lastCostSum = maxCostSum;
		while (!stoppedByRequest && currentSelectionIndex < settings.maxSelectionIndex) {
			countDownLatch.await();
			countDownLatch = new CountDownLatch(settings.getTasksSize());
			lastCostSum = geneticAlgorithmIteration(lastCostSum);
		}
	}

	private double geneticAlgorithmIteration(final double lastCostSum) {
		final double newCostSum = calculateCostSum();
		final List<TradingStrategy> currentPopulation = population;

		createNewPopulation(currentPopulation);
		crossover(currentPopulation);
		mutation(currentPopulation);
		checkResult(newCostSum, lastCostSum);

		return newCostSum;
	}

	private void checkResult(double newCostSum, double lastCostSum) {
		if (currentSelectionIndex > MINIMUM_STEPS_AMOUNT && shouldTerminate(newCostSum, lastCostSum)) {
			lastSelectionIndex = currentSelectionIndex;
			currentSelectionIndex = settings.maxSelectionIndex;
			logger.debug("summary cost of statistics not changed throw iteration on valuable value");
		} else {
			for (SimulatorCalulatingTask task : simulatorCalculatingTasks) {
				executor.submit(task);
			}
			simulatorCalculatingTasks.clear();
		}
		if (lastCostSum > maxCostSum) {
			maxCostSum = lastCostSum;
		}
		currentSelectionIndex += 1;
	}

	private void createNewPopulation(List<TradingStrategy> currentPopulation) {
		population = Collections.synchronizedList(new ArrayList<TradingStrategy>());

		if (settings.sizeOfBest > 0) {
			for (TradingStrategy strategy : selector.getStrategies()) {
				final Boolean pe = sortedPopulation.get(strategy);
				if (pe != null && pe) {
					population.add(strategy);
					if (population.size() == settings.sizeOfBest) {
						break;
					}
				}
			}
		}

		sortedPopulation.clear();
		for (TradingStrategy strategy : population) {
			sortedPopulation.put(strategy, true);
		}
	}

	private void crossover(final List<TradingStrategy> currentPopulation) {
		final int size = currentPopulation.size();
		if (size == 0) {
			return;
		}
		final Random r = new Random();

		for (int i = 0; i < settings.crossoverSize; ++i) {
			final int leftIndex = r.nextInt(size);
			final int rightIndex = r.nextInt(size);

			final SimulatorSettings left = currentPopulation.get(leftIndex).getSettings();
			final SimulatorSettings right = currentPopulation.get(rightIndex).getSettings();

			final SimulatorSettings mergedStatistics = settingsGeneticList.merge(left, right);

			simulatorCalculatingTasks.add(new SimulatorCalulatingTask(this, this, mergedStatistics));
		}
	}

	private void mutation(final List<TradingStrategy> currentPopulation) {
		final int size = currentPopulation.size();
		if (size == 0) {
			return;
		}
		for (int i = 0; i < settings.mutationSize; ++i) {
			final int index = indexRandomizator.nextInt(size);
			final SimulatorSettings settings = currentPopulation.get(index).getSettings();
			final SimulatorSettings mutatedSettings = settingsGeneticList.mutate(settings);
			simulatorCalculatingTasks.add(new SimulatorCalulatingTask(this, this, mutatedSettings));
		}
	}

	private boolean shouldTerminate(double newCostSum, double lastCostSum) {
		final boolean isMaxCostSum = DoubleMath.fuzzyEquals(newCostSum, maxCostSum, Settings.doubleEpsilon);
		final boolean costSumNotChanged = DoubleMath.fuzzyEquals(newCostSum, lastCostSum, Settings.doubleEpsilon);
		return isMaxCostSum && costSumNotChanged;
	}

	private double calculateCostSum() {
		double lastCostSum = 0.0;
		for (TradingStrategy e : population) {
			lastCostSum += costFunction.calculate(e.getStatistics());
		}
		return lastCostSum;
	}

	public double getMaxCostSum() {
		return maxCostSum;
	}

	public int getLastSelectionIndex() {
		return lastSelectionIndex;
	}

	@Override
	public void stopSearch() {
		this.stoppedByRequest = true;
	}

}
