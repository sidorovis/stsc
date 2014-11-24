package stsc.integration.tests.algorithms.stock.factors.primitive;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.In;
import stsc.algorithms.stock.factors.primitive.Diff;
import stsc.algorithms.stock.factors.primitive.Ema;
import stsc.algorithms.stock.factors.primitive.Sma;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.SignalsStorage;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class DiffTest {

	@Test
	public void testDiff() throws BadAlgorithmException, IOException, BadSignalException, ParseException {

		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");
		stockInit.getSettings().setString("e", "open");
		final In in = new In(stockInit.getInit());

		final StockAlgoInitHelper emaInit = new StockAlgoInitHelper("ema", "aapl", stockInit.getStorage());
		emaInit.getSettings().addSubExecutionName("in");
		emaInit.getSettings().setInteger("size", 10000);
		final Ema ema = new Ema(emaInit.getInit());

		final StockAlgoInitHelper smaInit = new StockAlgoInitHelper("sma", "aapl", stockInit.getStorage());
		smaInit.getSettings().addSubExecutionName("in");
		smaInit.getSettings().setInteger("size", 10000);
		final Sma sma = new Sma(smaInit.getInit());

		final StockAlgoInitHelper diffInit = new StockAlgoInitHelper("diff", "aapl", stockInit.getStorage());
		diffInit.getSettings().addSubExecutionName("ema").addSubExecutionName("sma");
		diffInit.getSettings().setInteger("size", 10000);
		final Diff diff = new Diff(diffInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			in.process(day);
			ema.process(day);
			sma.process(day);
			diff.process(day);
		}

		final SignalsStorage ss = stockInit.getStorage();
		Assert.assertNotNull(ss.getStockSignal("aapl", "diff", 0));
		Assert.assertEquals(0.0, ss.getStockSignal("aapl", "diff", 0).getSignal(DoubleSignal.class).value, Settings.doubleEpsilon);

		final double emaValue = ss.getStockSignal("aapl", "ema", 4).getSignal(DoubleSignal.class).value;
		final double smaValue = ss.getStockSignal("aapl", "sma", 4).getSignal(DoubleSignal.class).value;

		Assert.assertEquals(emaValue - smaValue, ss.getStockSignal("aapl", "diff", 4).getSignal(DoubleSignal.class).value,
				Settings.doubleEpsilon);
	}
}
