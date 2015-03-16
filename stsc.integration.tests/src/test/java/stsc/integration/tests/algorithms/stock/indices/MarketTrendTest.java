package stsc.integration.tests.algorithms.stock.indices;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.stock.indices.MarketTrend;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class MarketTrendTest {

	@Test
	public void testMarketTrend() throws Exception {
		final StockAlgoInitHelper spyInit = new StockAlgoInitHelper("in", "spy");
		final MarketTrend spyMt = new MarketTrend(spyInit.getInit());

		final StockAlgoInitHelper aaplInit = new StockAlgoInitHelper("in", "aapl", spyInit.getStorage());
		final MarketTrend aaplMt = new MarketTrend(aaplInit.getInit());

		final Stock spy = UnitedFormatStock.readFromUniteFormatFile("./test_data/spy.uf");
		final int spyIndex = spy.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> spyDays = spy.getDays();

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> aaplDays = aapl.getDays();

		for (int i = 0; i < (spyDays.size() - spyIndex) && i < (aaplDays.size() - aaplIndex); ++i) {
			final Day am = aaplDays.get(i + aaplIndex);
			final Day sm = spyDays.get(i + spyIndex);

			aaplMt.process(am);
			spyMt.process(sm);

			final double spyValue = spyInit.getStorage().getStockSignal("spy", "in", sm.getDate()).getContent(DoubleSignal.class)
					.getValue();
			final double aaplValue = aaplInit.getStorage().getStockSignal("aapl", "in", am.getDate()).getContent(DoubleSignal.class)
					.getValue();

			Assert.assertEquals(spyValue, aaplValue, Settings.doubleEpsilon);
		}
	}

}
