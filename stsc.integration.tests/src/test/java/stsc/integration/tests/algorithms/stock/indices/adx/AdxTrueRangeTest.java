package stsc.integration.tests.algorithms.stock.indices.adx;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.stock.indices.adx.AdxTrueRange;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class AdxTrueRangeTest {
	@Test
	public void testBollingerBands() throws BadAlgorithmException, ParseException, IOException, BadSignalException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper atrInit = new StockAlgoInitHelper("atr", "aapl", stockInit.getStorage());
		atrInit.getSettings().setInteger("size", 10000);
		final AdxTrueRange atr = new AdxTrueRange(atrInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			atr.process(day);
		}

		{
			final double max = Math.max(days.get(aaplIndex).getPrices().getHigh(), days.get(aaplIndex).getPrices().getLow());
			final double min = Math.min(days.get(aaplIndex).getPrices().getLow(), days.get(aaplIndex).getPrices().getClose());

			Assert.assertEquals(max - min,
					atrInit.getStorage().getStockSignal("aapl", "atr", days.get(aaplIndex).getDate()).getSignal(DoubleSignal.class).value,
					Settings.doubleEpsilon);
		}
		{
			final double max = Math.max(days.get(aaplIndex + 1).getPrices().getHigh(), days.get(aaplIndex).getPrices().getLow());
			final double min = Math.min(days.get(aaplIndex + 1).getPrices().getLow(), days.get(aaplIndex).getPrices().getClose());

			Assert.assertEquals(
					max - min,
					atrInit.getStorage().getStockSignal("aapl", "atr", days.get(aaplIndex + 1).getDate()).getSignal(DoubleSignal.class).value,
					Settings.doubleEpsilon);
		}
	}
}
