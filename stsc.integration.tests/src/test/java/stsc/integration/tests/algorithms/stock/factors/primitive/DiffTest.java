package stsc.integration.tests.algorithms.stock.factors.primitive;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;

import stsc.algorithms.In;
import stsc.algorithms.stock.factors.primitive.Diff;
import stsc.algorithms.stock.factors.primitive.Ema;
import stsc.algorithms.stock.factors.primitive.Sma;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;
import junit.framework.TestCase;

public class DiffTest extends TestCase {
	public void testDiff() throws BadAlgorithmException, IOException, BadSignalException, ParseException {

		StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");
		stockInit.getSettings().set("e", "open");
		final In in = new In(stockInit.getInit());

		StockAlgoInitHelper emaInit = new StockAlgoInitHelper("ema", "aapl", stockInit.getStorage());
		emaInit.getSettings().addSubExecutionName("in");
		Ema ema = new Ema(emaInit.getInit());

		StockAlgoInitHelper smaInit = new StockAlgoInitHelper("sma", "aapl", stockInit.getStorage());
		smaInit.getSettings().addSubExecutionName("in");
		Sma sma = new Sma(smaInit.getInit());

		StockAlgoInitHelper diffInit = new StockAlgoInitHelper("diff", "aapl", stockInit.getStorage());
		diffInit.getSettings().addSubExecutionName("ema").addSubExecutionName("sma");
		diffInit.getSettings().setInteger("size", 10000);
		Diff diff = new Diff(diffInit.getInit());

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

		assertNotNull(stockInit.getStorage().getStockSignal("aapl", "diff", 0));
		assertEquals(0.0, stockInit.getStorage().getStockSignal("aapl", "diff", 0).getSignal(DoubleSignal.class).value, 0.001);
		assertEquals(0.928176, stockInit.getStorage().getStockSignal("aapl", "diff", 4).getSignal(DoubleSignal.class).value, 0.001);
	}
}
