package stsc.integration.tests.algorithms.stock.indices;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.stock.indices.StochasticOscillator;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class StochasticOscillatorTest {

	@Test
	public void testStochasticOscillator() throws Exception {
		final StockAlgoInitHelper init = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper soInit = new StockAlgoInitHelper("so", "aapl", init.getStorage());
		final StochasticOscillator so = new StochasticOscillator(soInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			so.process(day);

			final double ct = day.getPrices().getClose();
			final double ln = init.getStorage().getStockSignal("aapl", "so_Ln", day.getDate()).getSignal(DoubleSignal.class).getValue();
			final double hn = init.getStorage().getStockSignal("aapl", "so_Hn", day.getDate()).getSignal(DoubleSignal.class).getValue();
			final double v = init.getStorage().getStockSignal("aapl", "so", day.getDate()).getSignal(DoubleSignal.class).getValue();

			Assert.assertEquals(100.0 * (ct - ln) / (hn - ln), v, Settings.doubleEpsilon);
		}
	}
}
