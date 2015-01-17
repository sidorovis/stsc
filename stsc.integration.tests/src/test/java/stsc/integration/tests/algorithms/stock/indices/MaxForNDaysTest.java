package stsc.integration.tests.algorithms.stock.indices;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.indices.MaxForNDays;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class MaxForNDaysTest {

	@Test
	public void testMaxForNDays() throws Exception {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");
		stockInit.getSettings().setString("e", "open");
		final Input inAlgo = new Input(stockInit.getInit());

		final StockAlgoInitHelper max4NInit = new StockAlgoInitHelper("max4N", "aapl", stockInit.getStorage());
		max4NInit.getSettings().setInteger("size", 10000);
		max4NInit.getSettings().setInteger("P", 5);
		max4NInit.getSettings().setInteger("SP", 5);
		max4NInit.getSettings().addSubExecutionName("in");
		final MaxForNDays max4N = new MaxForNDays(max4NInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			inAlgo.process(day);
			max4N.process(day);
			final double v = stockInit.getStorage().getStockSignal("aapl", "max4N", day.getDate()).getContent(DoubleSignal.class).getValue();
			if (i - aaplIndex < 5) {
				Assert.assertEquals(day.getPrices().getOpen(), v, Settings.doubleEpsilon);
			} else if (i - aaplIndex < 10) {
				double max = Double.MIN_VALUE;
				for (int u = aaplIndex; u < i - 5 + 1; ++u) {
					max = Math.max(max, days.get(u).getPrices().getOpen());
				}
				Assert.assertEquals(max, v, Settings.doubleEpsilon);
			} else {
				double max = Double.MIN_VALUE;
				for (int u = i - 9; u < i - 5 + 1; ++u) {
					max = Math.max(max, days.get(u).getPrices().getOpen());
				}
				Assert.assertEquals(max, v, Settings.doubleEpsilon);
			}
		}
	}
}
