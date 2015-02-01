package stsc.integration.tests.algorithms.stock.geometry;

import java.util.ArrayList;
import java.util.Optional;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.geometry.FibonacciRetracementBearStdDev;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class FibonacciRetracementBearStdDevTest {

	@Test
	public void testFibonacciRetracementBearStdDev() throws Exception {
		final StockAlgoInitHelper init = new StockAlgoInitHelper("in", "aapl");
		init.getSettings().setInteger("size", 8);
		final Input in = new Input(init.getInit());

		final StockAlgoInitHelper frInit = new StockAlgoInitHelper("fr", "aapl", init.getStorage());
		frInit.getSettings().addSubExecutionName("in");
		frInit.getSettings().setInteger("N", 8);
		final FibonacciRetracementBearStdDev fr = new FibonacciRetracementBearStdDev(frInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2005, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		final int len = FibonacciRetracementBearStdDev.ratios.length;

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			in.process(day);
			fr.process(day);

			final Optional<DoubleSignal> v = init.getStorage().getStockSignal("aapl", "fr", day.getDate()).getSignal(DoubleSignal.class);
			Assert.assertTrue(v.isPresent());
			if (i - aaplIndex >= 8) {
				final double firstP = init.getStorage().getStockSignal("aapl", "in", i - aaplIndex - 8).getSignal(DoubleSignal.class).get()
						.getValue();
				final double lastP = init.getStorage().getStockSignal("aapl", "in", i - aaplIndex).getSignal(DoubleSignal.class).get()
						.getValue();
				if (lastP < firstP) {
					final double diff = firstP - lastP;
					double stdDev = 0.0;
					for (int u = 1; u < len; ++u) {
						final int mappedIndex = u * (8 / len);
						final int index = i - aaplIndex - 8 + mappedIndex;
						final double actualP = init.getStorage().getStockSignal("aapl", "in", index).getSignal(DoubleSignal.class).get()
								.getValue();
						final double expectedP = firstP - FibonacciRetracementBearStdDev.ratios[u] * diff;
						stdDev += Math.pow(expectedP - actualP, 2.0);
					}
					Assert.assertEquals(stdDev, v.get().getValue(), Settings.doubleEpsilon);
				} else {
					Assert.assertEquals(Double.MAX_VALUE, v.get().getValue(), Settings.doubleEpsilon);
				}
			} else {
				Assert.assertEquals(Double.MAX_VALUE, v.get().getValue(), Settings.doubleEpsilon);
			}
		}
	}
}
