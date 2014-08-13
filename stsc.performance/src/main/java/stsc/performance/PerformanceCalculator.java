package stsc.performance;

import java.io.IOException;
import java.util.List;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import stsc.common.BadSignalException;
import stsc.common.TimeTracker;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.multistarter.StrategySearcher;
import stsc.general.simulator.multistarter.StrategySearcherException;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticList;
import stsc.general.simulator.multistarter.genetic.StrategyGeneticSearcher;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridFactory;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.general.simulator.multistarter.grid.StrategyGridSearcher;
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.StrategySelector;
import stsc.general.statistic.cost.function.WeightedSumCostFunction;
import stsc.general.strategy.TradingStrategy;

class PerformanceCalculator {

	final StockStorage stockStorage;

	final private PerformanceCalculatorSettings settings;

	private StockStorage loadStocks() throws ClassNotFoundException, IOException, InterruptedException {
		return StockStorageSingleton.getInstance();
	}

	PerformanceCalculator(PerformanceCalculatorSettings settings) throws Exception {
		this.settings = settings;
		this.stockStorage = loadStocks();

		if (settings.printAdditionalInfo && settings.searcherType == SearcherType.GENETIC_SEARCHER) {
			System.out.print(settings.maxSelectionIndex + " " + settings.populationSize + " ");
		}
		if (settings.printStarterInfo) {
			System.out.println("Size of stocks: " + stockStorage.getStockNames().size());
			calculateAmountOfSimulations(stockStorage, settings);
		}
		if (settings.shouldWarmUp) {
			warmUp();
		}
	}

	public void calculateTimeStatistics() throws Exception {
		for (int i = 10; i <= 31; ++i) {
			calculateForThreads(settings.startOfPeriod.plusDays(i));
		}

		for (int i = 1; i <= 12; ++i) {
			calculateForThreads(settings.startOfPeriod.plusMonths(i));
		}

		for (int i = 1; i <= 12; i += 1) {
			calculateForThreads(settings.startOfPeriod.plusYears(i));
		}

		for (int i = 42; i <= 42; i += 1) {
			calculateForThreads(settings.startOfPeriod.plusYears(i));
		}

	}

	public void calculateSmallStatistics() throws Exception {
		for (int i = 3; i <= 3; i += 1) {
			calculateForThreads(settings.startOfPeriod.plusYears(i));
		}
	}

	private void calculateForThreads(LocalDate endDate) throws Exception {
		if (settings.printAdditionalInfo)
			System.out.print(Days.daysBetween(settings.startOfPeriod, endDate).getDays());
		for (int thread = settings.threadsFrom; thread <= settings.threadsTo; ++thread) {
			calculateAverageTime(endDate, true, thread);
		}
		if (settings.printAdditionalInfo)
			System.out.println();
	}

	static public void calculateAmountOfSimulations(StockStorage stockStorage, PerformanceCalculatorSettings settings) throws StrategySearcherException {
		final SimulatorSettingsGridFactory factory = SimulatorSettingsGenerator.getGridFactory(settings.performanceForGridTest, stockStorage,
				settings.elements, settings.getStartOfPeriod(), getDateRepresentation(settings.startOfPeriod.plusMonths(1)));
		System.out.println("Simulation amount: " + factory.size());
	}

	private void warmUp() throws Exception {
		final LocalDate newDate = settings.startOfPeriod.plusDays(31);
		final int[] threadToWarmUp = { 1, 2, 8 };
		for (int i : threadToWarmUp) {
			calculateAverageTime(newDate, false, i);
		}
		final LocalDate tenDate = settings.startOfPeriod.plusDays(10);
		for (int i : threadToWarmUp) {
			calculateAverageTime(tenDate, false, i);
		}
	}

	private double calculateAverageTime(LocalDate endDate, boolean printData, int threadAmount) throws Exception {
		final String endOfPeriod = getDateRepresentation(endDate);

		double time = 0.0;
		double avGain = 0.0;
		for (int i = 0; i < settings.calculationsForAverage; ++i) {
			final PerformanceResult result = timeForSearch(threadAmount, endOfPeriod);
			time += TimeTracker.lengthInSeconds(result.timeTracker.length());
			avGain += result.sumAvGainForBest;
		}

		final double avTime = time / settings.calculationsForAverage;
		final double avRes = avGain / settings.calculationsForAverage;

		if (printData && settings.printAvGainAndTime) {
			System.out.print(" " + settings.format(avTime) + " " + settings.format(avRes) + " ");
		}

		return avTime;
	}

	public PerformanceResult timeForSearch(int threadSize, String endOfPeriod) throws StrategySearcherException, BadAlgorithmException, BadSignalException,
			InterruptedException {
		final TimeTracker timeTracker = new TimeTracker();

		final StrategySearcher searcher = generateSearcher(threadSize, endOfPeriod);
		final List<TradingStrategy> strategies = searcher.getSelector().getStrategies();
		return createResult(strategies, timeTracker);
	}

	private StrategySearcher generateSearcher(int threadSize, String endOfPeriod) throws InterruptedException {
		final String startDate = getDateRepresentation(settings.startOfPeriod);
		final StrategySelector selector = new StatisticsByCostSelector(settings.storedStrategyAmount, new WeightedSumCostFunction());
		if (settings.searcherType == SearcherType.GRID_SEARCHER) {
			final SimulatorSettingsGridList list = SimulatorSettingsGenerator.getGridFactory(settings.performanceForGridTest, stockStorage, settings.elements,
					startDate, endOfPeriod).getList();
			return new StrategyGridSearcher(list, selector, threadSize);
		} else {
			final SimulatorSettingsGeneticList list = SimulatorSettingsGenerator.getGeneticFactory(settings.performanceForGridTest, stockStorage,
					settings.elements, startDate, endOfPeriod).getList();
			return new StrategyGeneticSearcher(list, selector, threadSize, settings.maxSelectionIndex, settings.populationSize);
		}
	}

	private static PerformanceResult createResult(List<TradingStrategy> strategies, TimeTracker timeTracker) {
		double sumGetAvGain = 0.0;
		timeTracker.finish();
		sumGetAvGain = strategies.get(0).getStatistics().getAvGain();
		// for (Statistics statistic : statistics) {
		// sumGetAvGain += statistic.getAvGain();
		// }
		return new PerformanceResult(timeTracker, sumGetAvGain);
	}

	static private String getDateRepresentation(LocalDate date) {
		int day = date.getDayOfMonth();
		int month = date.getMonthOfYear();
		int year = date.getYear();
		return String.format("%02d-%02d-%04d", day, month, year);
	}

}
