package stsc.integration.tests.algorithms.stock.indices.rsi;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.stock.indices.rsi.RsiRsi;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class RsiRsiTest {

	@Test
	public void testRsiRsi() throws ParseException, BadAlgorithmException, IOException, BadSignalException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper rsiInit = new StockAlgoInitHelper("rsi", "aapl", stockInit.getStorage());
		rsiInit.getSettings().setDouble("P", 0.5);
		rsiInit.getSettings().setInteger("size", 10000);
		final RsiRsi rsi = new RsiRsi(rsiInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			rsi.process(day);
		}
		final Day lastDay = days.get(days.size() - 1);

		final double u = stockInit.getStorage().getStockSignal("aapl", "rsi_RsiEmaU", lastDay.getDate()).getSignal(DoubleSignal.class)
				.getValue();
		final double d = stockInit.getStorage().getStockSignal("aapl", "rsi_RsiEmaD", lastDay.getDate()).getSignal(DoubleSignal.class)
				.getValue();

		final double v = stockInit.getStorage().getStockSignal("aapl", "rsi", lastDay.getDate()).getSignal(DoubleSignal.class).getValue();

		Assert.assertEquals(100 - 100 / (1 + u / d), v, Settings.doubleEpsilon);
	}
}
