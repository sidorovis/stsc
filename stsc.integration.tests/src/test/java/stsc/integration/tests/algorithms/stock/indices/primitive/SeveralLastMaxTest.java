package stsc.integration.tests.algorithms.stock.indices.primitive;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.indices.primitive.SeveralLastMax;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.signals.SignalContainer;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class SeveralLastMaxTest {

	@Test
	public void testSeveralLastMax() throws Exception {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("testIn", "aapl");
		final Input inAlgo = new Input(stockInit.getInit());

		final StockAlgoInitHelper init = new StockAlgoInitHelper("slm", "aapl", stockInit.getStorage());
		init.getSettings().addSubExecutionName("testIn");
		final SeveralLastMax slm = new SeveralLastMax(init.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			inAlgo.process(day);
			if (i == 6824) {
				System.out.println(i);
			}
			slm.process(day);

			final SignalContainer<?> s = stockInit.getStorage().getStockSignal("aapl", "slm", day.getDate());
			if (s != null) {
				final double v = s.getSignal(DoubleSignal.class).getValue();
				final double pre = days.get(i - 2).getPrices().getOpen();
				final double current = days.get(i - 1).getPrices().getOpen();
				final double next = days.get(i).getPrices().getOpen();
				Assert.assertEquals(current, v, Settings.doubleEpsilon);
//				Assert.assertTrue(current > pre);
				Assert.assertTrue(current > next);
			}
		}
	}

}
