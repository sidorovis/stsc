package stsc.integration.tests.algorithms.stock.indices;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.indices.Trix;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class TrixTest {

	@Test
	public void testTrix() throws Exception {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");
		stockInit.getSettings().setString("e", "open");
		final Input inAlgo = new Input(stockInit.getInit());

		final StockAlgoInitHelper trixInit = new StockAlgoInitHelper("trix", "aapl", stockInit.getStorage());
		trixInit.getSettings().setInteger("size", 10000);
		trixInit.getSettings().addSubExecutionName("in");
		final Trix trix = new Trix(trixInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			inAlgo.process(day);
			trix.process(day);

			final double v = stockInit.getStorage().getStockSignal("aapl", "trix", day.getDate()).getContent(DoubleSignal.class).getValue();
			if (i == aaplIndex) {
				Assert.assertEquals(0.0, v, Settings.doubleEpsilon);
			} else {
				final double pTmaV = stockInit.getStorage().getStockSignal("aapl", "trix_Tma", days.get(i - 1).getDate())
						.getContent(DoubleSignal.class).getValue();
				final double tmaV = stockInit.getStorage().getStockSignal("aapl", "trix_Tma", day.getDate()).getContent(DoubleSignal.class)
						.getValue();
				final double p = stockInit.getStorage().getStockSignal("aapl", "trix", days.get(i).getDate()).getContent(DoubleSignal.class)
						.getValue();
				Assert.assertEquals(100.0 * (tmaV - pTmaV) / pTmaV, p, Settings.doubleEpsilon);
			}
		}
	}
}
