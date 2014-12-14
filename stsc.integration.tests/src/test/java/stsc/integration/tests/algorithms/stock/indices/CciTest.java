package stsc.integration.tests.algorithms.stock.indices;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.stock.indices.Cci;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class CciTest {

	@Test
	public void testCci() throws Exception {
		final StockAlgoInitHelper init = new StockAlgoInitHelper("in", "aapl");

		final StockAlgoInitHelper cciInit = new StockAlgoInitHelper("cci", "aapl", init.getStorage());
		final Cci cci = new Cci(cciInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			cci.process(day);

			final double tp = init.getStorage().getStockSignal("aapl", "cci_TypicalPrice", day.getDate()).getSignal(DoubleSignal.class)
					.getValue();
			final double sma = init.getStorage().getStockSignal("aapl", "cci_Sma", day.getDate()).getSignal(DoubleSignal.class).getValue();
			final double stdev = init.getStorage().getStockSignal("aapl", "cci_SmStDev", day.getDate()).getSignal(DoubleSignal.class)
					.getValue();

			final double v = init.getStorage().getStockSignal("aapl", "cci", day.getDate()).getSignal(DoubleSignal.class).getValue();

			if (Double.compare(stdev, 0.0) == 0) {
				Assert.assertEquals(0.0, v, Settings.doubleEpsilon);
			} else {
				final double ev = (1.0 / 0.015) * (tp - sma) / stdev;
				Assert.assertEquals(ev, v, Settings.doubleEpsilon);
			}
		}
	}
}
