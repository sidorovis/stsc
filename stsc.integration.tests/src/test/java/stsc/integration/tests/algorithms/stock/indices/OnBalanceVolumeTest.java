package stsc.integration.tests.algorithms.stock.indices;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.stock.indices.OnBalanceVolume;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class OnBalanceVolumeTest {

	@Test
	public void testMomentum() throws Exception {
		final StockAlgoInitHelper init = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper obvInit = new StockAlgoInitHelper("obv", "aapl", init.getStorage());
		obvInit.getSettings().setInteger("size", 10000);
		final OnBalanceVolume obv = new OnBalanceVolume(obvInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			obv.process(day);
			if (i - aaplIndex >= 5) {
				final double oldObValue = init.getStorage().getStockSignal("aapl", "obv", days.get(i - 5).getDate())
						.getContent(DoubleSignal.class).getValue();
				final double obValue = init.getStorage().getStockSignal("aapl", "obv", day.getDate()).getContent(DoubleSignal.class)
						.getValue();

				final double vtn = days.get(i - 5).getPrices().getClose();
				final double vt = day.getPrices().getClose();
				double v = 0.0;
				if (vt > vtn) {
					v = day.getVolume();
				} else if (vt < vtn) {
					v = -day.getVolume();
				}
				Assert.assertEquals(obValue, oldObValue + v, Settings.doubleEpsilon);
			}
		}
	}
}
