package stsc.integration.tests.algorithms.stock.indices.macd;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.indices.macd.MacdMacd;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class MacdMacdTest {

	@Test
	public void testMacdMacd() throws ParseException, BadAlgorithmException, IOException, BadSignalException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");
		final Input in = new Input(stockInit.getInit());

		final StockAlgoInitHelper macdInit = new StockAlgoInitHelper("macd", "aapl", stockInit.getStorage());
		macdInit.getSettings().addSubExecutionName("in");
		final MacdMacd macd = new MacdMacd(macdInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			in.process(day);
			macd.process(day);

			final double s = stockInit.getStorage().getStockSignal("aapl", "macd_EmaS", day.getDate()).getContent(DoubleSignal.class)
					.getValue();
			final double l = stockInit.getStorage().getStockSignal("aapl", "macd_EmaL", day.getDate()).getContent(DoubleSignal.class)
					.getValue();
			final double v = stockInit.getStorage().getStockSignal("aapl", "macd", day.getDate()).getContent(DoubleSignal.class).getValue();

			Assert.assertEquals(s - l, v, Settings.doubleEpsilon);
		}
	}
}
