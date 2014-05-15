package stsc.performance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stsc.algorithms.BadAlgorithmException;
import stsc.common.MarketDataContext;
import stsc.common.TimeSearcher;
import stsc.signals.BadSignalException;
import stsc.simulator.multistarter.StrategySearcher;
import stsc.simulator.multistarter.StrategySearcherException;
import stsc.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.simulator.multistarter.grid.StrategyGridSearcher;
import stsc.statistic.StatisticsCalculationException;
import stsc.statistic.StatisticsInnerProductFunction;
import stsc.statistic.StatisticsSelector;
import stsc.storage.StockStorage;
import stsc.storage.YahooFileStockStorage;

public class PerformanceCalculator {

	private static int storedStrategyAmount = 500;

	private static int threadsFrom = 1;
	private static int threadsTo = 4;
	private static int threadsStep = 1;

	final private StockStorage stockStorage;

	// private List<String> periods = Arrays.asList(new String[] { "31-01-2000"
	// });

	private List<String> periods = Arrays.asList(new String[] { "31-01-2000", "31-02-2000", "31-03-2000", "31-04-2000",
			"31-05-2000", "31-06-2000", "01-01-2001", "01-01-2002", "01-01-2003", "01-01-2004", "01-01-2005",
			"01-01-2006", "01-01-2007", "01-01-2008", "01-01-2009" });

	private static int calculationsForAverage = 5;

	static private class PerformanceStatistic {
		public PerformanceStatistic(int threads, String period, double avTime) {
			super();
			this.threads = threads;
			this.period = period;
			this.avTime = avTime;
		}

		final public int threads;
		final public String period;
		final public double avTime;
	}

	private List<PerformanceStatistic> statistics = new ArrayList<>();

	private StockStorage loadStocks() throws ClassNotFoundException, IOException, InterruptedException {
		MarketDataContext marketDataContext = new MarketDataContext();
		marketDataContext.dataFolder = "D:/dev/java/StscData/data/";
		marketDataContext.filteredDataFolder = "D:/dev/java/StscData/filtered_data/";
		StockStorage ss = new YahooFileStockStorage(marketDataContext);
		return ss;
	}

	public PerformanceCalculator() throws Exception {
		stockStorage = loadStocks();
		System.out.println("Size of threads: " + stockStorage.getStockNames().size());
		getTimeFor(1, "31-01-2000");
		for (int threads = threadsFrom; threads <= threadsTo; threads += threadsStep) {
			for (String endOfPeriod : periods) {
				final double avTime = getTimeFor(threads, endOfPeriod);
				statistics.add(new PerformanceStatistic(threads, endOfPeriod, avTime));
			}
		}
	}

	private long getTimeFor(int threads, String endOfPeriod) throws Exception {
		final List<String> elements = Arrays.asList(new String[] { "open", "close", "high", "low" });
		final SimulatorSettingsGridList list = SimulatorSettingsGenerator.getSimulatorSettingsGridList(stockStorage,
				elements, endOfPeriod);
		final StatisticsSelector<Double> selector = new StatisticsSelector<Double>(storedStrategyAmount,
				new StatisticsInnerProductFunction());
		final StrategyGridSearcher searcher = new StrategyGridSearcher(list, selector, threads);
		return calculateAverageTime(searcher);
	}

	private long timeForSearch(final StrategySearcher<Double> searcher) throws StrategySearcherException,
			BadAlgorithmException, StatisticsCalculationException, BadSignalException {
		final TimeSearcher timeSearcher = new TimeSearcher();
		searcher.getSelector().getSortedStatistics();
		return timeSearcher.finish();
	}

	private long calculateAverageTime(StrategySearcher<Double> searcher) throws Exception {
		final int n = calculationsForAverage;
		double av = 0.0;

		for (int i = 0; i < n; ++i) {
			av += timeForSearch(searcher);
		}
		return Math.round(av / calculationsForAverage);
	}

	public void printStdOut() {
		for (PerformanceStatistic ps : statistics) {
			System.out.println(ps.threads + "\t" + ps.period + "\t" + String.format("%02f", ps.avTime));
		}
	}
}
