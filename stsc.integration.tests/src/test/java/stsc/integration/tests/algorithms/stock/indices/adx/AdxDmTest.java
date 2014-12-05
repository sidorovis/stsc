package stsc.integration.tests.algorithms.stock.indices.adx;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.stock.indices.adx.AdxDm;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.ListOfDoubleSignal;

public class AdxDmTest {

	@Test
	public void testAdxDm() throws BadAlgorithmException, ParseException, IOException, BadSignalException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper admInit = new StockAlgoInitHelper("adm", "aapl", stockInit.getStorage());
		admInit.getSettings().setInteger("size", 10000);
		final AdxDm adm = new AdxDm(admInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			adm.process(day);
		}

		Assert.assertEquals(0.0,
				admInit.getStorage().getStockSignal("aapl", "adm", days.get(aaplIndex).getDate()).getSignal(ListOfDoubleSignal.class)
						.getValues().get(0), Settings.doubleEpsilon);
		Assert.assertEquals(0.0,
				admInit.getStorage().getStockSignal("aapl", "adm", days.get(aaplIndex).getDate()).getSignal(ListOfDoubleSignal.class)
						.getValues().get(1), Settings.doubleEpsilon);

		Assert.assertEquals(Math.max(days.get(aaplIndex).getPrices().getLow() - days.get(aaplIndex + 1).getPrices().getLow(), 0.0), admInit
				.getStorage().getStockSignal("aapl", "adm", days.get(aaplIndex + 1).getDate()).getSignal(ListOfDoubleSignal.class)
				.getValues().get(0), Settings.doubleEpsilon);
		Assert.assertEquals(Math.max(0.0, days.get(aaplIndex + 1).getPrices().getHigh() - days.get(aaplIndex).getPrices().getHigh()),
				admInit.getStorage().getStockSignal("aapl", "adm", days.get(aaplIndex + 1).getDate()).getSignal(ListOfDoubleSignal.class)
						.getValues().get(1), Settings.doubleEpsilon);

	}
}
