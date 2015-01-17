package stsc.integration.tests.algorithms.stock.indices.ikh;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.stock.indices.ikh.IkhChikou;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class IkhChikouTest {

	@Test
	public void testIkhChikou() throws Exception {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final int tm = 18;

		final StockAlgoInitHelper chikouInit = new StockAlgoInitHelper("chikou", "aapl", stockInit.getStorage());
		chikouInit.getSettings().setInteger("TM", tm);
		final IkhChikou chikou = new IkhChikou(chikouInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			chikou.process(day);
			final double v = stockInit.getStorage().getStockSignal("aapl", "chikou", day.getDate()).getContent(DoubleSignal.class)
					.getValue();
			if (i - aaplIndex < tm) {
				Assert.assertEquals(days.get(aaplIndex).getPrices().getClose(), v, Settings.doubleEpsilon);
			} else {
				Assert.assertEquals(days.get(i - tm).getPrices().getClose(), v, Settings.doubleEpsilon);
			}
		}
	}
}
