package stsc.integration.tests.algorithms.stock.indices;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.indices.Momentum;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class MomentumTest {

	@Test
	public void testMomentum() throws Exception {
		final StockAlgoInitHelper init = new StockAlgoInitHelper("in", "aapl");
		final Input in = new Input(init.getInit());

		final StockAlgoInitHelper momInit = new StockAlgoInitHelper("mom", "aapl", init.getStorage());
		momInit.getSettings().addSubExecutionName("in");
		final Momentum mom = new Momentum(momInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			in.process(day);
			mom.process(day);
			if (i == aaplIndex) {
				Assert.assertEquals(0.0, init.getStorage().getStockSignal("aapl", "mom", day.getDate()).getSignal(DoubleSignal.class)
						.getValue(), Settings.doubleEpsilon);
			} else if (i - aaplIndex < 5) {
				Assert.assertEquals(days.get(i).getPrices().getOpen() - days.get(aaplIndex).getPrices().getOpen(), init.getStorage()
						.getStockSignal("aapl", "mom", day.getDate()).getSignal(DoubleSignal.class).getValue(), Settings.doubleEpsilon);
			} else {
				Assert.assertEquals(days.get(i).getPrices().getOpen() - days.get(i - 5).getPrices().getOpen(), init.getStorage()
						.getStockSignal("aapl", "mom", day.getDate()).getSignal(DoubleSignal.class).getValue(), Settings.doubleEpsilon);
			}
		}
	}
}
