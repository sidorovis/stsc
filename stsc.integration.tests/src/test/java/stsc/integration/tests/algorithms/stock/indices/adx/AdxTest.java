package stsc.integration.tests.algorithms.stock.indices.adx;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.stock.indices.adx.Adx;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class AdxTest {

	@Test
	public void testAdx() throws ParseException, BadAlgorithmException, IOException, BadSignalException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper adxInit = new StockAlgoInitHelper("adx", "aapl", stockInit.getStorage());
		adxInit.getSettings().setInteger("size", 10000);
		final Adx adx = new Adx(adxInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		double sum = 0.0;

		for (int i = aaplIndex; i < aaplIndex + 14; ++i) {
			final Day day = days.get(i);
			adx.process(day);

			final double dxiV = adxInit.getStorage().getStockSignal("aapl", "adx_adxDxi", day.getDate()).getSignal(DoubleSignal.class)
					.getValue();
			sum += dxiV;
			final double adxV = adxInit.getStorage().getStockSignal("aapl", "adx", day.getDate()).getSignal(DoubleSignal.class).getValue();
			Assert.assertEquals(sum / (i - aaplIndex + 1), adxV, Settings.doubleEpsilon);
		}
	}
}
