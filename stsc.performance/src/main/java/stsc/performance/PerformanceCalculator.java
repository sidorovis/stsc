package stsc.performance;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import stsc.common.BadSignalException;
import stsc.common.TimeTracker;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.multistarter.StrategySearcherException;
import stsc.general.simulator.multistarter.genetic.SimulatorSettingsGeneticList;
import stsc.general.simulator.multistarter.genetic.StrategyGeneticSearcher;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridFactory;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.general.simulator.multistarter.grid.StrategyGridSearcher;
import stsc.general.statistic.Statistics;
import stsc.general.statistic.StatisticsCalculationException;
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.StatisticsSelector;
import stsc.general.statistic.cost.function.WeightedSumCostFunction;
import stsc.performance.PerformanceSearcher.PerformanceResult;

class PerformanceCalculator {

	private final static int storedStrategyAmount = 100;

	private int currentTestThreadAmount = 1;

	final StockStorage stockStorage;

	private final static int calculationsForAverage = 1;
	private final static int threadsFrom = 6;
	private final static int threadsTo = 6;

	private static LocalDate startOfPeriod = new LocalDate(1970, 1, 1);

	private static boolean shouldWarmUp = false;

	private static int maxSelectionIndex = 150;
	private static int populationSize = 150;

	final List<String> elements = Arrays.asList(new String[] { "open", "high", "low", "close", "value", "open", "high", "low", "close" });

	private final SearcherType searcherType;
	private PerformanceSearcher searcher;

	private StockStorage loadStocks() throws ClassNotFoundException, IOException, InterruptedException {
		return StockStorageSingleton.getInstance();
	}

	PerformanceCalculator(final SearcherType type) throws Exception {
		this.searcherType = type;
		this.stockStorage = loadStocks();
		System.out.println("Size of stocks: " + stockStorage.getStockNames().size());
		if (shouldWarmUp) {
			warmUp();
		}
		calculateAmountOfSimulations();
		calculateStatistics();
	}

	private void calculateStatistics() throws Exception {
		for (int i = 31; i <= 31; ++i) {
			calculateForThreads(startOfPeriod.plusDays(i));
		}
		//
		// for (int i = 1; i <= 12; ++i) {
		// calculateForThreads(startOfPeriod.plusMonths(i));
		// }

		// for (int i = 1; i <= 12; i += 1) {
		// calculateForThreads(startOfPeriod.plusYears(i));
		// }
		//
		// for (int i = 42; i <= 42; i += 1) {
		// calculateForThreads(startOfPeriod.plusYears(i));
		// }

	}

	private void calculateForThreads(LocalDate endDate) throws Exception {
		System.out.print(Days.daysBetween(startOfPeriod, endDate).getDays());
		for (int thread = threadsFrom; thread <= threadsTo; ++thread) {
			currentTestThreadAmount = thread;
			calculateAverageTime(endDate, true);
		}
		System.out.println();
	}

	private void calculateAmountOfSimulations() throws StrategySearcherException {
		final SimulatorSettingsGridFactory factory = SimulatorSettingsGenerator.getGridFactory(stockStorage, elements, getDateRepresentation(startOfPeriod),
				getDateRepresentation(startOfPeriod.plusMonths(1)));
		System.out.println("Simulation amount: " + factory.size());
	}

	private void warmUp() throws Exception {
		final LocalDate newDate = startOfPeriod.plusDays(31);
		calculateAverageTime(newDate, false);
		currentTestThreadAmount = 2;
		calculateAverageTime(newDate, false);
		currentTestThreadAmount = 8;
		calculateAverageTime(newDate, false);
		final LocalDate tenDate = startOfPeriod.plusDays(31);
		currentTestThreadAmount = 1;
		calculateAverageTime(tenDate, false);
		currentTestThreadAmount = 2;
		calculateAverageTime(tenDate, false);
		currentTestThreadAmount = 8;
		calculateAverageTime(tenDate, false);
	}

	private long calculateAverageTime(LocalDate endDate, boolean printData) throws Exception {
		final String endOfPeriod = getDateRepresentation(endDate);

		if (searcherType == SearcherType.GENETIC_SEARCHER) {
			searcher = new GeneticSearcher(this, elements, currentTestThreadAmount, endOfPeriod);
		} else {
			searcher = new GridSearcher(this, elements, currentTestThreadAmount, endOfPeriod);
		}

		double time = 0.0;
		double avGain = 0.0;
		for (int i = 0; i < calculationsForAverage; ++i) {
			final PerformanceResult result = searcher.search();
			time += TimeTracker.lengthInSeconds(result.timeTracker.length());
			avGain += result.sumAvGainForBest;
		}

		final double avTime = time / calculationsForAverage;
		final double avRes = avGain / calculationsForAverage;

		final DecimalFormat formatter = new DecimalFormat("#0.000000000");

		if (printData) {
			System.out.print(" T:" + formatter.format(avTime) + " (Gain:" + formatter.format(avRes) + ") ");
		}

		return Math.round(time / calculationsForAverage);
	}

	static PerformanceResult timeForGridSearch(final StockStorage stockStorage, final List<String> openTypes, int threadSize, String endOfPeriod)
			throws StrategySearcherException, BadAlgorithmException, StatisticsCalculationException, BadSignalException {
		final TimeTracker timeTracker = new TimeTracker();
		final String startDate = getDateRepresentation(startOfPeriod);
		final SimulatorSettingsGridList list = SimulatorSettingsGenerator.getGridFactory(stockStorage, openTypes, startDate, endOfPeriod).getList();
		final StatisticsSelector selector = new StatisticsByCostSelector(storedStrategyAmount, new WeightedSumCostFunction());
		final StrategyGridSearcher searcher = new StrategyGridSearcher(list, selector, threadSize);
		return createResult(searcher.getSelector().getStatistics(), timeTracker);
	}

	static PerformanceResult timeForGeneticSearch(final StockStorage stockStorage, final List<String> openTypes, int threadSize, String endOfPeriod)
			throws InterruptedException, StrategySearcherException {
		final TimeTracker timeTracker = new TimeTracker();
		final String startDate = getDateRepresentation(startOfPeriod);
		final SimulatorSettingsGeneticList list = SimulatorSettingsGenerator.getGeneticFactory(stockStorage, openTypes, startDate, endOfPeriod).getList();
		final StatisticsSelector selector = new StatisticsByCostSelector(storedStrategyAmount, new WeightedSumCostFunction());
		final StrategyGeneticSearcher searcher = new StrategyGeneticSearcher(list, selector, threadSize, maxSelectionIndex, populationSize);
		return createResult(searcher.getSelector().getStatistics(), timeTracker);
	}

	private static PerformanceResult createResult(List<Statistics> statistics, TimeTracker timeTracker) {
		double sumGetAvGain = 0.0;
		timeTracker.finish();
		sumGetAvGain = statistics.get(0).getAvGain();
//		for (Statistics statistic : statistics) {
//			sumGetAvGain += statistic.getAvGain();
//		}
		return new PerformanceResult(timeTracker, sumGetAvGain);
	}

	static private String getDateRepresentation(LocalDate date) {
		int day = date.getDayOfMonth();
		int month = date.getMonthOfYear();
		int year = date.getYear();
		return String.format("%02d-%02d-%04d", day, month, year);
	}

}
