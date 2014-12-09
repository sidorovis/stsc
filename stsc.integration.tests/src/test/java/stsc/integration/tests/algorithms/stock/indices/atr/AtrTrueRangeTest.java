package stsc.integration.tests.algorithms.stock.indices.atr;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.stock.indices.atr.AtrTrueRange;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Prices;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class AtrTrueRangeTest {

	@Test
	public void testAtrTrueRange() throws ParseException, BadAlgorithmException, IOException, BadSignalException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper atrTrInit = new StockAlgoInitHelper("atrTr", "aapl", stockInit.getStorage());
		atrTrInit.getSettings().setInteger("size", 10000);
		final AtrTrueRange atrTr = new AtrTrueRange(atrTrInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			atrTr.process(day);

			if (i == aaplIndex) {
				continue;
			}
			final Prices p = day.getPrices();
			final Prices pp = days.get(i - 1).getPrices();

			final double v1 = p.getHigh() - p.getLow();
			final double v2 = Math.abs(p.getHigh() - pp.getClose());
			final double v3 = Math.abs(p.getLow() - pp.getClose());

			final double value = stockInit.getStorage().getStockSignal("aapl", "atrTr", day.getDate()).getSignal(DoubleSignal.class)
					.getValue();

			Assert.assertEquals(Math.max(Math.max(v1, v2), v3), value, Settings.doubleEpsilon);
		}
	}
}
