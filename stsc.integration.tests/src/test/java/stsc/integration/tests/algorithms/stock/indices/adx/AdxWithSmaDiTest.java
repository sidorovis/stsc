package stsc.integration.tests.algorithms.stock.indices.adx;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.stock.indices.adx.AdxWithSmaDi;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;
import stsc.signals.ListOfDoubleSignal;

public class AdxWithSmaDiTest {

	@Test
	public void testAdxWithSimpleMaDi() throws BadAlgorithmException, ParseException, IOException, BadSignalException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper adiInit = new StockAlgoInitHelper("smadi", "aapl", stockInit.getStorage());
		adiInit.getSettings().setInteger("size", 10000);
		final AdxWithSmaDi adiSma = new AdxWithSmaDi(adiInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			adiSma.process(day);

			final double adxMinusSma = adiInit.getStorage().getStockSignal("aapl", "smadi_AdxDi_MinusAdapter_Sma", day.getDate())
					.getSignal(DoubleSignal.class).value;
			final double adxPlusSma = adiInit.getStorage().getStockSignal("aapl", "smadi_AdxDi_PlusAdapter_Sma", day.getDate())
					.getSignal(DoubleSignal.class).value;

			final double aMinus = adiInit.getStorage().getStockSignal("aapl", "smadi", day.getDate()).getSignal(ListOfDoubleSignal.class).values
					.get(0);
			final double aPlus = adiInit.getStorage().getStockSignal("aapl", "smadi", day.getDate()).getSignal(ListOfDoubleSignal.class).values
					.get(1);

			Assert.assertEquals(adxMinusSma, aMinus, Settings.doubleEpsilon);
			Assert.assertEquals(adxPlusSma, aPlus, Settings.doubleEpsilon);
		}

	}
}
