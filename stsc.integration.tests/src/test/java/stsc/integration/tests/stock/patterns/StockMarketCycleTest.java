package stsc.integration.tests.stock.patterns;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.stock.indices.StockMarketCycle;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.signals.SignalContainer;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.SignalsStorage;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class StockMarketCycleTest {

	private void testHelper(String sn) throws Exception {
		final StockAlgoInitHelper smcInit = new StockAlgoInitHelper("smc", sn);
		final StockMarketCycle smc = new StockMarketCycle(smcInit.getInit());

		final Stock stock = UnitedFormatStock.readFromUniteFormatFile("./test_data/" + sn + UnitedFormatStock.EXTENSION);
		final int stockIndex = stock.findDayIndex(new LocalDate(1990, 9, 4).toDate());
		final ArrayList<Day> days = stock.getDays();

		final SignalsStorage ss = smcInit.getStorage();

		for (int i = stockIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			smc.process(day);

			final SignalContainer<?> sc = ss.getStockSignal(sn, "smc", day.getDate());

			if (i - stockIndex < 5) {
				Assert.assertNotNull(sc);
				Assert.assertEquals(0.0, sc.getContent(DoubleSignal.class).getValue(), Settings.doubleEpsilon);
			} else {
				Assert.assertNotNull(sc);
			}
		}
	}

	@Test
	public void testStockMarketCycle() throws Exception {
		testHelper("aa");
		testHelper("aapl");
		testHelper("adm");
		testHelper("apa");
		testHelper("spy");
	}
	
	@Test
	public void testOnSpy() {
		
	}
}
