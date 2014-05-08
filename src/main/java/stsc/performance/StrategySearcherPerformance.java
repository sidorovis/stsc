package stsc.performance;

import java.util.Arrays;

import stsc.algorithms.BadAlgorithmException;
import stsc.signals.BadSignalException;
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

	private long timeForSearch(final SimulatorSettingsGridIterator iterator) throws BadAlgorithmException,
			StatisticsCalculationException, BadSignalException {
		final TimeSearcher timeSearcher = new TimeSearcher();
		final StatisticsSelector<Double> selector = new StatisticsSelector<>(500, new StatisticsInnerProductFunction());
		final StrategyGridSearcher searcher = new StrategyGridSearcher(iterator, selector);
		searcher.getSelector().getSelect();
		return timeSearcher.finish();
	}

	private long timeForMtSearch(final SimulatorSettingsGridIterator iterator) throws BadAlgorithmException,
			StatisticsCalculationException, BadSignalException, InterruptedException {
		final TimeSearcher timeSearcher = new TimeSearcher();
		final StatisticsSelector<Double> selector = new StatisticsSelector<>(500, new StatisticsInnerProductFunction());
		final MtStrategyGridSearcher searcher = new MtStrategyGridSearcher(iterator, selector, 3);
		searcher.getSelector().getSelect();
		return timeSearcher.finish();
	}

	private long testTime(StockStorage stockStorage, String finishPeriod) throws BadAlgorithmException,
			StatisticsCalculationException, BadSignalException, InterruptedException {
		final SimulatorSettingsGridIterator iterator = TestHelper.getSimulatorSettingsGridIterator(stockStorage,
				Arrays.asList(new String[] { "open", "close", "high", "low" }), finishPeriod);
		final long result = timeForSearch(iterator);
		return result;
	}

	private long testMtTime(StockStorage stockStorage, String finishPeriod) throws BadAlgorithmException,
			StatisticsCalculationException, BadSignalException, InterruptedException {
		final SimulatorSettingsGridIterator iterator = TestHelper.getSimulatorSettingsGridIterator(stockStorage,
				Arrays.asList(new String[] { "open", "close", "high", "low" }), finishPeriod);
		final long result = timeForMtSearch(iterator);
		return result;
	}

	private long testAvTime(StockStorage stockStorage, String finishPeriod) throws BadAlgorithmException,
			StatisticsCalculationException, BadSignalException, InterruptedException {
		final int n = avSize;
		double av = 0.0;
		for (int i = 0; i < n; ++i) {
			av += testTime(stockStorage, finishPeriod);
		}
		return Math.round(av / n);
	}

	private long testAvMtTime(StockStorage stockStorage, String finishPeriod) throws BadAlgorithmException,
			StatisticsCalculationException, BadSignalException, InterruptedException {
		final int n = avSize;
		double av = 0.0;
		for (int i = 0; i < n; ++i) {
			av += testMtTime(stockStorage, finishPeriod);
		}
		return Math.round(av / n);
	}

	public StrategySearcherPerformance() throws BadAlgorithmException, StatisticsCalculationException,
			BadSignalException, InterruptedException {

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
}
