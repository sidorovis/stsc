package stsc.integration.tests.algorithms.stock.indices.adx;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.stock.indices.adx.AdxDxi;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;
import stsc.signals.ListOfDoubleSignal;

public class AdxDxiTest {

	@Test
	public void testAdxDxi() throws ParseException, BadAlgorithmException, IOException, BadSignalException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper adxInit = new StockAlgoInitHelper("adx", "aapl", stockInit.getStorage());
		adxInit.getSettings().setInteger("size", 10000);
		final AdxDxi adxDxi = new AdxDxi(adxInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			adxDxi.process(day);

			final ListOfDoubleSignal s = adxInit.getStorage().getStockSignal("aapl", "adx_adxSmaDiName", day.getDate())
					.getSignal(ListOfDoubleSignal.class);

			final double m = s.getValues().get(0);
			final double p = s.getValues().get(1);

			final double r = adxInit.getStorage().getStockSignal("aapl", "adx", day.getDate()).getSignal(DoubleSignal.class).getValue();

			if (Double.compare(p + m, 0.0) == 0) {
				Assert.assertEquals(100.0, r, Settings.doubleEpsilon);
			} else {
				Assert.assertEquals(100.0 * Math.abs(p - m) / (p + m), r, Settings.doubleEpsilon);
			}
		}
	}
}
