package stsc.integration.tests.algorithms.stock.indices.atr;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.stock.indices.atr.AtrAtr;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class AtrAtrTest {

	@Test
	public void testAtrAtr() throws Exception {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper atrAtrInit = new StockAlgoInitHelper("atr", "aapl", stockInit.getStorage());
		atrAtrInit.getSettings().setInteger("size", 10000);
		final AtrAtr atrAtr = new AtrAtr(atrAtrInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		double sum = 0.0;

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			atrAtr.process(day);

			final double atrTrValue = stockInit.getStorage().getStockSignal("aapl", "atr_AtrTr", day.getDate())
					.getSignal(DoubleSignal.class).getValue();
			final double value = stockInit.getStorage().getStockSignal("aapl", "atr", day.getDate()).getSignal(DoubleSignal.class)
					.getValue();

			if (i - aaplIndex < 14) {
				sum += atrTrValue;
				Assert.assertEquals(sum / (i - aaplIndex + 1), value, Settings.doubleEpsilon);
			} else {
				final double previousValue = stockInit.getStorage().getStockSignal("aapl", "atr", days.get(i - 1).getDate())
						.getSignal(DoubleSignal.class).getValue();

				Assert.assertEquals((previousValue * 13 + atrTrValue) / 14, value, Settings.doubleEpsilon);
			}
		}
	}
}
