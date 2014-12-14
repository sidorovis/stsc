package stsc.integration.tests.algorithms.stock.indices;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.indices.MinForNDays;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class MinForNDaysTest {

	@Test
	public void testMaxForNDays() throws Exception {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");
		stockInit.getSettings().setString("e", "high");
		final Input inAlgo = new Input(stockInit.getInit());

		final StockAlgoInitHelper min4NInit = new StockAlgoInitHelper("min4N", "aapl", stockInit.getStorage());
		min4NInit.getSettings().setInteger("size", 10000);
		min4NInit.getSettings().setInteger("P", 5);
		min4NInit.getSettings().setInteger("SP", 5);
		min4NInit.getSettings().addSubExecutionName("in");
		final MinForNDays min4N = new MinForNDays(min4NInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			inAlgo.process(day);
			min4N.process(day);
			final double v = stockInit.getStorage().getStockSignal("aapl", "min4N", day.getDate()).getSignal(DoubleSignal.class).getValue();
			if (i - aaplIndex < 5) {
				Assert.assertEquals(day.getPrices().getHigh(), v, Settings.doubleEpsilon);
			} else if (i - aaplIndex < 10) {
				double max = Double.MAX_VALUE;
				for (int u = aaplIndex; u < i - 5 + 1; ++u) {
					max = Math.min(max, days.get(u).getPrices().getHigh());
				}
				Assert.assertEquals(max, v, Settings.doubleEpsilon);
			} else {
				double max = Double.MAX_VALUE;
				for (int u = i - 9; u < i - 5 + 1; ++u) {
					max = Math.min(max, days.get(u).getPrices().getHigh());
				}
				Assert.assertEquals(max, v, Settings.doubleEpsilon);
			}
		}
	}
}
