package stsc.integration.tests.algorithms.stock.indices.adx;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.indices.adx.AdxDi;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;
import stsc.signals.ListOfDoubleSignal;

public class AdxDiTest {

	@Test
	public void testAdxDi() throws ParseException, BadAlgorithmException, IOException, BadSignalException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");
		stockInit.getSettings().setString("e", "open");
		final Input in = new Input(stockInit.getInit());

		final StockAlgoInitHelper adiInit = new StockAlgoInitHelper("adi", "aapl", stockInit.getStorage());
		adiInit.getSettings().addSubExecutionName("in");
		adiInit.getSettings().setInteger("size", 10000);
		final AdxDi adi = new AdxDi(adiInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			in.process(day);
			adi.process(day);
		}

		final double trValue0 = adiInit.getStorage().getStockSignal("aapl", "adi_AdxTrueRange", 0).getSignal(DoubleSignal.class).value;
		final double trValue1 = adiInit.getStorage().getStockSignal("aapl", "adi_AdxTrueRange", 1).getSignal(DoubleSignal.class).value;

		final double adxDmValueMinus0 = adiInit.getStorage().getStockSignal("aapl", "adi_AdxDm", 0).getSignal(ListOfDoubleSignal.class).values
				.get(0);
		final double adxDmValuePlus0 = adiInit.getStorage().getStockSignal("aapl", "adi_AdxDm", 0).getSignal(ListOfDoubleSignal.class).values
				.get(1);

		final double adxDmValueMinus1 = adiInit.getStorage().getStockSignal("aapl", "adi_AdxDm", 1).getSignal(ListOfDoubleSignal.class).values
				.get(0);
		final double adxDmValuePlus1 = adiInit.getStorage().getStockSignal("aapl", "adi_AdxDm", 1).getSignal(ListOfDoubleSignal.class).values
				.get(1);

		Assert.assertEquals(adxDmValueMinus0 / trValue0,
				adiInit.getStorage().getStockSignal("aapl", "adi", 0).getSignal(ListOfDoubleSignal.class).values.get(0),
				Settings.doubleEpsilon);
		Assert.assertEquals(adxDmValuePlus0 / trValue0,
				adiInit.getStorage().getStockSignal("aapl", "adi", 0).getSignal(ListOfDoubleSignal.class).values.get(1),
				Settings.doubleEpsilon);

		Assert.assertEquals(adxDmValueMinus1 / trValue1,
				adiInit.getStorage().getStockSignal("aapl", "adi", 1).getSignal(ListOfDoubleSignal.class).values.get(0),
				Settings.doubleEpsilon);
		Assert.assertEquals(adxDmValuePlus1 / trValue1,
				adiInit.getStorage().getStockSignal("aapl", "adi", 1).getSignal(ListOfDoubleSignal.class).values.get(1),
				Settings.doubleEpsilon);
	}

}
