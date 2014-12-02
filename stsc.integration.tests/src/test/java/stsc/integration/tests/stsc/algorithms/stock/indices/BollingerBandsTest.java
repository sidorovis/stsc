package stsc.integration.tests.stsc.algorithms.stock.indices;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.indices.BollingerBands;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;
import stsc.signals.ListOfDoubleSignal;

public class BollingerBandsTest {

	@Test
	public void testBollingerBands() throws BadAlgorithmException, ParseException, IOException, BadSignalException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");
		stockInit.getSettings().setString("e", "open");
		final Input in = new Input(stockInit.getInit());

		final StockAlgoInitHelper bbInit = new StockAlgoInitHelper("bb", "aapl", stockInit.getStorage());
		bbInit.getSettings().addSubExecutionName("in");
		bbInit.getSettings().setInteger("size", 10000);
		final BollingerBands bb = new BollingerBands(bbInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			in.process(day);
			bb.process(day);
		}

		final Day lastDay = days.get(days.size() - 1);

		final Double smaValue = bbInit.getStorage().getStockSignal("aapl", "BB_Sma_bb", lastDay.getDate()).getSignal(DoubleSignal.class).value;
		final Double stdDevValue = bbInit.getStorage().getStockSignal("aapl", "BB_StdDev_bb", lastDay.getDate())
				.getSignal(DoubleSignal.class).value;

		final Double bbLowValue = bbInit.getStorage().getStockSignal("aapl", "bb", lastDay.getDate()).getSignal(ListOfDoubleSignal.class).values
				.get(0);
		final Double bbHighValue = bbInit.getStorage().getStockSignal("aapl", "bb", lastDay.getDate()).getSignal(ListOfDoubleSignal.class).values
				.get(1);

		Assert.assertEquals(smaValue - 2 * stdDevValue, bbLowValue, Settings.doubleEpsilon);
		Assert.assertEquals(smaValue + 2 * stdDevValue, bbHighValue, Settings.doubleEpsilon);
	}
}
