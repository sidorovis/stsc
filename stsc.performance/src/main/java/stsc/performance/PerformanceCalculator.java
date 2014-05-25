package stsc.performance;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import stsc.algorithms.BadAlgorithmException;
import stsc.common.TimeTracker;
import stsc.signals.BadSignalException;
import stsc.simulator.SimulatorSettings;
import stsc.simulator.multistarter.StrategySearcherException;
import stsc.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.simulator.multistarter.grid.StrategyGridSearcher;
import stsc.statistic.StatisticsCalculationException;
import stsc.statistic.StatisticsInnerProductFunction;
import stsc.statistic.StatisticsSelector;
import stsc.storage.StockStorage;

class PerformanceCalculator {

	private final static int storedStrategyAmount = 100;

	private int currentTestThreadAmount = 1;

	final private StockStorage stockStorage;

	private final static int calculationsForAverage = 10;
	private final static int threadsFrom = 1;
	private final static int threadsTo = 8;

	private static LocalDate startOfPeriod = new LocalDate(1970, 1, 1);

	final List<String> elements = Arrays.asList(new String[] { "open", "high", "low", "close", "value", "open", "high", "low", "close" });

	private StockStorage loadStocks() throws ClassNotFoundException, IOException, InterruptedException {
		return SimulatorSettingsGenerator.StockStorageSingleton.getInstance();
	}

	PerformanceCalculator() throws Exception {
		stockStorage = loadStocks();
		System.out.println("Size of stocks: " + stockStorage.getStockNames().size());
		warmUp();
		calculateAmountOfSimulations();
		calculateStatistics();
	}

	private void calculateStatistics() throws Exception {
		for (int i = 10; i <= 31; ++i) {
			calculateForThreads(startOfPeriod.plusDays(i));
		}

		for (int i = 1; i <= 12; ++i) {
			calculateForThreads(startOfPeriod.plusMonths(i));
		}

		for (int i = 1; i <= 12; i += 1) {
			calculateForThreads(startOfPeriod.plusYears(i));
		}

		for (int i = 42; i <= 42; i += 1) {
			calculateForThreads(startOfPeriod.plusYears(i));
		}

	}

	private void calculateForThreads(LocalDate endDate) throws Exception {
		System.out.print(Days.daysBetween(startOfPeriod, endDate).getDays());
		for (int thread = threadsFrom; thread <= threadsTo; ++thread) {
			currentTestThreadAmount = thread;
			getStatistics(endDate);
		}
		System.out.println();
	}

	private void calculateAmountOfSimulations() throws StrategySearcherException {
		final SimulatorSettingsGridList list = SimulatorSettingsGenerator.getSimulatorSettingsGridList(stockStorage, elements,
				getDateRepresentation(startOfPeriod), getDateRepresentation(startOfPeriod.plusMonths(1)));
		int i = 0;
		final Iterator<SimulatorSettings> iterator = list.iterator();
		while (iterator.hasNext()) {
			iterator.next();
			++i;
		}
		System.out.println("Simulation amount: " + i);
	}

	private void warmUp() throws Exception {
		final LocalDate newDate = startOfPeriod.plusDays(31);
		calculateTime(newDate, false);
		currentTestThreadAmount = 2;
		calculateTime(newDate, false);
		currentTestThreadAmount = 8;
		calculateTime(newDate, false);
		final LocalDate tenDate = startOfPeriod.plusDays(31);
		currentTestThreadAmount = 1;
		calculateTime(tenDate, false);
		currentTestThreadAmount = 2;
		calculateTime(tenDate, false);
		currentTestThreadAmount = 8;
		calculateTime(tenDate, false);
	}

	private void getStatistics(LocalDate endDate) throws Exception {
		calculateTime(endDate, true);
	}

	private void calculateTime(LocalDate endDate, boolean printData) throws Exception {
		calculateTime(getDateRepresentation(endDate), printData);
	}

	private String getDateRepresentation(LocalDate date) {
		int day = date.getDayOfMonth();
		int month = date.getMonthOfYear();
		int year = date.getYear();
		return String.format("%02d-%02d-%04d", day, month, year);
	}

	private void calculateTime(String end, boolean printData) throws Exception {
		final double timeInSeconds = TimeTracker.lengthInSeconds(getTimeFor(currentTestThreadAmount, end));
		if (printData) {
			System.out.print(" " + String.valueOf(timeInSeconds));
		}
	}

	private long getTimeFor(int threads, String endOfPeriod) throws Exception {
		return calculateAverageTime(threads, endOfPeriod);
	}

	private long calculateAverageTime(int threads, String endOfPeriod) throws Exception {
		final int n = calculationsForAverage;
		double av = 0.0;

		for (int i = 0; i < n; ++i) {
			av += timeForSearch(threads, endOfPeriod).length();
		}
		return Math.round(av / calculationsForAverage);
	}

	private TimeTracker timeForSearch(int threadSize, String endOfPeriod) throws StrategySearcherException, BadAlgorithmException,
			StatisticsCalculationException, BadSignalException {
		final TimeTracker timeTracker = new TimeTracker();
		final String startDate = getDateRepresentation(startOfPeriod);
		final SimulatorSettingsGridList list = SimulatorSettingsGenerator.getSimulatorSettingsGridList(stockStorage, elements, startDate, endOfPeriod);
		final StatisticsSelector<Double> selector = new StatisticsSelector<Double>(storedStrategyAmount, new StatisticsInnerProductFunction());
		final StrategyGridSearcher searcher = new StrategyGridSearcher(list, selector, threadSize);
		searcher.getSelector().getSortedStatistics();
		timeTracker.finish();
		return timeTracker;
	}

}
