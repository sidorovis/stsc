package stsc.general.simulator.multistarter.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import stsc.common.algorithms.BadAlgorithmException;
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

	private class PopulationElement {
		SimulatorSettings settings;
		Statistics statistics;
		boolean addedAsBestStatistics;

		PopulationElement(SimulatorSettings settings, Statistics statistics, boolean addedAsBestStatistics) {
			super();
			this.settings = settings;
			this.statistics = statistics;
			this.addedAsBestStatistics = addedAsBestStatistics;
		}
	}

	private static Logger logger = LogManager.getLogger("StrategyGeneticSearcher");

	private final static int MINIMUM_STEPS_AMOUNT = 10;
	private final static double BEST_DEFAULT_PART = 0.7;
	private final static double CROSSOVER_DEFAULT_PART = 0.8;

	private int currentSelectionIndex = 0;
	private int lastSelectionIndex;

	private double maxCostSum = -Double.MAX_VALUE;

	private final StatisticsSelector selector;
	private final SimulatorSettingsGeneticList settingsGeneticList;
	private List<PopulationElement> population;
	private Map<Statistics, PopulationElement> sortedPopulation;

	private final CostFunction costFunction;

	private final ExecutorService executor;
	private CountDownLatch countDownLatch;
	private final List<SimulatorCalulatingTask> simulatorCalculatingTasks;

	final private class GeneticSearchSettings {
		final int maxSelectionIndex;
		final int sizeOfBest;
		final int populationSize;
		final int crossoverSize;
		final int mutationSize;

		final int tasksSize;

		GeneticSearchSettings(int maxSelectionIndex, int populationSize, double bestPart, double crossoverPart, int selectorSize) {
			this.maxSelectionIndex = maxSelectionIndex;
			this.populationSize = populationSize;
			final int preSizeOfBest = (int) (bestPart * populationSize);
			if (preSizeOfBest > selectorSize) {
				this.sizeOfBest = selectorSize;
			} else {
				this.sizeOfBest = preSizeOfBest;
			}
			this.crossoverSize = (int) ((populationSize - this.sizeOfBest) * crossoverPart);
			this.mutationSize = populationSize - crossoverSize - this.sizeOfBest;
			this.tasksSize = crossoverSize + mutationSize;
		}

		int getTasksSize() {
			return tasksSize;
		}

	}

	private final GeneticSearchSettings settings;

	public StrategyGeneticSearcher(final StatisticsSelector selector, SimulatorSettingsGeneticList algorithmSettings, int threadAmount, int maxSelectionIndex,
			int populationSize) throws InterruptedException {
		this(selector, algorithmSettings, threadAmount, new WeightedSumCostFunction(), maxSelectionIndex, populationSize, BEST_DEFAULT_PART,
				CROSSOVER_DEFAULT_PART);
	}

	public StrategyGeneticSearcher(final StatisticsSelector selector, SimulatorSettingsGeneticList algorithmSettings, int threadAmount,
			CostFunction costFunction, int maxSelectionIndex, int populationSize, double bestPart, double crossoverPart) throws InterruptedException {
		this.selector = selector;
		this.settingsGeneticList = algorithmSettings;
		this.population = Collections.synchronizedList(new ArrayList<PopulationElement>());
		this.sortedPopulation = Collections.synchronizedMap(new HashMap<Statistics, PopulationElement>());
		this.executor = Executors.newFixedThreadPool(threadAmount);

		this.costFunction = costFunction;
		this.countDownLatch = new CountDownLatch(populationSize);
		this.simulatorCalculatingTasks = new ArrayList<>();

		this.settings = new GeneticSearchSettings(maxSelectionIndex, populationSize, bestPart, crossoverPart, selector.size());
		this.lastSelectionIndex = maxSelectionIndex;

		startSearcher();
	}

	private void startSearcher() {
		executor.submit(new GenerateInitialPopulationsTask(this));
	}

	private final class GenerateInitialPopulationsTask implements Runnable {

		private StrategyGeneticSearcher searcher;

		GenerateInitialPopulationsTask(StrategyGeneticSearcher searcher) {
			this.searcher = searcher;
		}

		@Override
		public void run() {
			for (int i = 0; i < settings.populationSize; ++i) {
				try {
					final SimulatorSettings ss = searcher.getRandomSettings();
					final SimulatorCalulatingTask task = new SimulatorCalulatingTask(searcher, ss);
					executor.submit(task);
				} catch (BadAlgorithmException e) {
					logger.error("Problem while generating random simulator settings: " + e.getMessage());
				}
			}
		}
	}

	private final class SimulatorCalulatingTask implements Callable<Boolean> {

		private StrategyGeneticSearcher searcher;
		private SimulatorSettings settings;

		SimulatorCalulatingTask(StrategyGeneticSearcher searcher, SimulatorSettings settings) {
			this.searcher = searcher;
			this.settings = settings;
		}

		@Override
		public Boolean call() throws Exception {
			boolean result = false;
			try {
				final Statistics statistics = simulate();
				if (statistics != null) {
					final boolean addedToStatistics = searcher.selector.addStatistics(statistics);
					final PopulationElement populationElement = new PopulationElement(settings, statistics, addedToStatistics);
					searcher.population.add(populationElement);
					searcher.sortedPopulation.put(statistics, populationElement);
					result = true;
				}
			} finally {
				countDownLatch.countDown();
			}
			return result;
		}

		private Statistics simulate() {
			Simulator simulator = null;
			try {
				simulator = new Simulator(settings);
			} catch (Exception e) {
				logger.error("Error while calculating statistics: " + e.getMessage());
				return null;
			}
			return simulator.getStatistics();
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

	private SimulatorSettings getRandomSettings() throws BadAlgorithmException {
		return settingsGeneticList.generateRandom();
	}

	private void waitResults() throws InterruptedException {
		double lastCostSum = maxCostSum;
		while (currentSelectionIndex < settings.maxSelectionIndex) {
			countDownLatch.await();
			countDownLatch = new CountDownLatch(settings.getTasksSize());
			lastCostSum = geneticAlgorithmIteration(lastCostSum);
		}
	}

	private double geneticAlgorithmIteration(final double lastCostSum) {
		final double newCostSum = calculateCostSum();
		final List<PopulationElement> currentPopulation = population;

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

	private void createNewPopulation(List<PopulationElement> currentPopulation) {
		population = Collections.synchronizedList(new ArrayList<PopulationElement>());

		if (settings.sizeOfBest > 0) {
			for (Statistics statistic : selector.getStatistics()) {
				final PopulationElement pe = sortedPopulation.get(statistic);
				if (pe != null && pe.addedAsBestStatistics) {
					population.add(pe);
					if (population.size() == settings.sizeOfBest) {
						break;
					}
				}
			}
		}

		sortedPopulation.clear();
		for (PopulationElement populationElement : population) {
			sortedPopulation.put(populationElement.statistics, populationElement);
		}
	}

	private void crossover(final List<PopulationElement> currentPopulation) {
		final int size = currentPopulation.size();
		if (size == 0) {
			return;
		}
		final Random r = new Random();

		for (int i = 0; i < settings.crossoverSize; ++i) {
			final int leftIndex = r.nextInt(size);
			final int rightIndex = r.nextInt(size);

			final SimulatorSettings left = currentPopulation.get(leftIndex).settings;
			final SimulatorSettings right = currentPopulation.get(rightIndex).settings;

			final SimulatorSettings mergedStatistics = settingsGeneticList.merge(left, right);

			simulatorCalculatingTasks.add(new SimulatorCalulatingTask(this, mergedStatistics));
		}
	}

	private void mutation(final List<PopulationElement> currentPopulation) {
		final int size = currentPopulation.size();
		if (size == 0) {
			return;
		}
		final Random r = new Random();

		for (int i = 0; i < settings.mutationSize; ++i) {
			final int index = r.nextInt(size);
			final SimulatorSettings settings = currentPopulation.get(index).settings;
			final SimulatorSettings mutatedSettings = settingsGeneticList.mutate(settings);

			simulatorCalculatingTasks.add(new SimulatorCalulatingTask(this, mutatedSettings));
		}
	}

	private boolean shouldTerminate(double newCostSum, double lastCostSum) {
		final boolean isMaxCostSum = DoubleMath.fuzzyEquals(newCostSum, maxCostSum, Settings.doubleEpsilon);
		final boolean costSumNotChanged = DoubleMath.fuzzyEquals(newCostSum, lastCostSum, Settings.doubleEpsilon);
		return isMaxCostSum && costSumNotChanged;
	}

	private double calculateCostSum() {
		double lastCostSum = 0.0;
		for (PopulationElement e : population) {
			lastCostSum += costFunction.calculate(e.statistics);
		}
		return lastCostSum;
	}

	public double getMaxCostSum() {
		return maxCostSum;
	}

	public int getLastSelectionIndex() {
		return lastSelectionIndex;
	}

}
