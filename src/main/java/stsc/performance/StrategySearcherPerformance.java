package stsc.performance;

import java.util.Arrays;

import stsc.algorithms.BadAlgorithmException;
import stsc.signals.BadSignalException;
import stsc.simulator.multistarter.StrategySearcher;
import stsc.simulator.multistarter.StrategySearcherException;
import stsc.simulator.multistarter.grid.MtStrategyGridSearcher;
import stsc.simulator.multistarter.grid.SimulatorSettingsGridIterator;
import stsc.simulator.multistarter.grid.StrategyGridSearcher;
import stsc.statistic.StatisticsCalculationException;
import stsc.statistic.StatisticsInnerProductFunction;
import stsc.statistic.StatisticsSelector;
import stsc.storage.StockStorage;
import stsc.testhelper.TestHelper;
import stsc.testhelper.TimeSearcher;

public class StrategySearcherPerformance {

	private static int avSize = 4;
	private static int storedStrategyAmount = 500;
	private static int threadSize = 4;

	private long timeForSearch(final StrategySearcher<Double> searcher) throws StrategySearcherException,
			BadAlgorithmException, StatisticsCalculationException, BadSignalException {
		final TimeSearcher timeSearcher = new TimeSearcher();
		searcher.getSelector().getSelect();
		return timeSearcher.finish();
	}

	private long calculateAverageTime(StrategySearcher<Double> searcher) throws Exception {
		final int n = avSize;
		double av = 0.0;

		for (int i = 0; i < n; ++i) {
			av += timeForSearch(searcher);
		}
		return Math.round(av / n);
	}

	private long testAvTime(StockStorage stockStorage, String finishPeriod) throws Exception {
		final SimulatorSettingsGridIterator iterator = TestHelper.getSimulatorSettingsGridIterator(stockStorage,
				Arrays.asList(new String[] { "open", "close", "high", "low" }), finishPeriod);
		final StatisticsSelector<Double> selector = new StatisticsSelector<Double>(storedStrategyAmount,
				new StatisticsInnerProductFunction());
		final StrategyGridSearcher searcher = new StrategyGridSearcher(iterator, selector);
		return calculateAverageTime(searcher);
	}

	private long testAvMtTime(StockStorage stockStorage, String finishPeriod) throws Exception {
		final SimulatorSettingsGridIterator iterator = TestHelper.getSimulatorSettingsGridIterator(stockStorage,
				Arrays.asList(new String[] { "open", "close", "high", "low" }), finishPeriod);
		final StatisticsSelector<Double> selector = new StatisticsSelector<Double>(storedStrategyAmount,
				new StatisticsInnerProductFunction());
		final MtStrategyGridSearcher searcher = new MtStrategyGridSearcher(iterator, selector, threadSize);
		return calculateAverageTime(searcher);
	}

	public StrategySearcherPerformance() throws Exception {

		final StockStorage stockStorage = TestHelper.getStockStorage();
		System.out.println(testAvTime(stockStorage, "31-01-2000"));
		System.out.println(testAvMtTime(stockStorage, "31-01-2000"));
		System.out.println(testAvTime(stockStorage, "31-01-2000"));
		System.out.println(testAvMtTime(stockStorage, "31-01-2000"));
		for (int i = 3; i <= 12; i += 3) {
			final long testAvTime = testAvTime(stockStorage, "31-" + String.format("%02d", i) + "-2000");
			final long testAvMtTime = testAvMtTime(stockStorage, "31-" + String.format("%02d", i) + "-2000");
			System.out.println(i + " : " + testAvTime + " " + testAvMtTime);
		}
		avSize = 4;
		for (int i = 2001; i <= 2001; i += 2) {
			final long testAvTime = testAvTime(stockStorage, "31-01-" + String.format("%04d", i));
			System.out.println(i + " 1t: " + testAvTime + " ");
			final long testAvMtTime = testAvMtTime(stockStorage, "31-01-" + String.format("%04d", i));
			System.out.println(i + " mt: " + testAvMtTime);
		}
	}

	public static void main(String[] args) {
		try {
			new StrategySearcherPerformance();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
