package stsc.integration.tests.algorithms.stock.indeces.primitive;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.indices.primitive.Sma;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class SmaTest {

	@Test
	public void testSma() throws IOException, BadSignalException, BadAlgorithmException, ParseException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("testIn", "aapl");
		stockInit.getSettings().setString("e", "open");
		final Input inAlgo = new Input(stockInit.getInit());

		final StockAlgoInitHelper stockInitClose = new StockAlgoInitHelper("testInClose", "aapl", stockInit.getStorage());
		stockInitClose.getSettings().setString("e", "close");
		final Input inAlgoClose = new Input(stockInitClose.getInit());

		final StockAlgoInitHelper init = new StockAlgoInitHelper("testSma", "aapl", stockInit.getStorage());
		init.getSettings().setInteger("n", 5);
		init.getSettings().setInteger("size", 10000);
		init.getSettings().addSubExecutionName("testIn");
		final Sma sma = new Sma(init.getInit());

		final StockAlgoInitHelper initClose = new StockAlgoInitHelper("testSmaClose", "aapl", stockInit.getStorage());
		initClose.getSettings().setInteger("n", 5);
		initClose.getSettings().setInteger("size", 10000);
		initClose.getSettings().addSubExecutionName("testInClose");
		final Sma smaClose = new Sma(initClose.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			inAlgo.process(day);
			inAlgoClose.process(day);
			sma.process(day);
			smaClose.process(day);
		}

		Assert.assertNotNull(init.getStorage().getStockSignal("aapl", "testSma", days.get(aaplIndex).getDate()));
		Assert.assertNotNull(init.getStorage().getStockSignal("aapl", "testSma", days.get(aaplIndex + 3).getDate()));
		Assert.assertNotNull(init.getStorage().getStockSignal("aapl", "testSma", days.get(aaplIndex + 4).getDate()));
		Assert.assertNotNull(init.getStorage().getStockSignal("aapl", "testSma", days.get(days.size() - 1).getDate()));

		Assert.assertEquals(init.getStorage().getStockSignal("aapl", "testSma", days.get(aaplIndex + 4).getDate()), init.getStorage()
				.getStockSignal("aapl", "testSma", 4));

		Assert.assertEquals(init.getStorage().getStockSignal("aapl", "testSma", days.get(aaplIndex).getDate()), init.getStorage()
				.getStockSignal("aapl", "testSma", 0));

		Double lastSum = 0.0;
		Double lastSumClose = 0.0;
		for (int i = days.size() - 5; i < days.size(); ++i) {
			lastSum += days.get(i).getPrices().getOpen();
			lastSumClose += days.get(i).getPrices().getClose();
		}
		final Day lastDay = days.get(days.size() - 1);
		final double lastSma = init.getStorage().getStockSignal("aapl", "testSma", lastDay.getDate()).getSignal(DoubleSignal.class)
				.getValue();
		Assert.assertEquals(lastSum / 5, lastSma, Settings.doubleEpsilon);

		final double lastSmaClose = init.getStorage().getStockSignal("aapl", "testSmaClose", lastDay.getDate())
				.getSignal(DoubleSignal.class).getValue();
		Assert.assertEquals(lastSumClose / 5, lastSmaClose, Settings.doubleEpsilon);
	}
}
