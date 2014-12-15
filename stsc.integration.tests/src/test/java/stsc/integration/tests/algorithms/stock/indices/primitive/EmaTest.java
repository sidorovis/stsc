package stsc.integration.tests.algorithms.stock.indices.primitive;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.indices.primitive.Ema;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class EmaTest {

	@Test
	public void testEma() throws IOException, BadSignalException, BadAlgorithmException, ParseException {
		final StockAlgoInitHelper inInit = new StockAlgoInitHelper("testIn", "aapl");
		inInit.getSettings().setString("e", "open");
		final Input inAlgo = new Input(inInit.getInit());

		final StockAlgoInitHelper emaInit = new StockAlgoInitHelper("testEma", "aapl", inInit.getStorage());
		emaInit.getSettings().setDouble("P", 0.3);
		emaInit.getSettings().setInteger("size", 100000);
		emaInit.getSettings().addSubExecutionName("testIn");
		final Ema ema = new Ema(emaInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			inAlgo.process(day);
			ema.process(day);
		}
		Assert.assertEquals(days.get(aaplIndex).getPrices().getOpen(),
				emaInit.getStorage().getStockSignal("aapl", "testEma", 0).getSignal(DoubleSignal.class).getValue(), Settings.doubleEpsilon);

		final double secondValue = days.get(aaplIndex).getPrices().getOpen() * 0.7 + 0.3 * days.get(aaplIndex + 1).getPrices().getOpen();
		Assert.assertEquals(secondValue,
				emaInit.getStorage().getStockSignal("aapl", "testEma", 1).getSignal(DoubleSignal.class).getValue(), Settings.doubleEpsilon);

		double lastValue = 0.0;
		final int size = emaInit.getStorage().getIndexSize("aapl", "testEma");
		for (int i = aaplIndex; i < days.size(); ++i) {
			final double open = aapl.getDays().get(i).getPrices().getOpen();
			if (i == aaplIndex) {
				lastValue = open;
			} else {
				lastValue = open * 0.3 + 0.7 * lastValue;
			}
		}
		Assert.assertEquals(lastValue, emaInit.getStorage().getStockSignal("aapl", "testEma", size - 1).getSignal(DoubleSignal.class)
				.getValue(), Settings.doubleEpsilon);
	}
}
