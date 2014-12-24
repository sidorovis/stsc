package stsc.integration.tests.algorithms.stock.geometry;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.geometry.LeastSquaresStraight;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.ListOfDoubleSignal;

public class LeastSquaresStraightTest {

	@Test
	public void testLeastSquaresStraight() throws Exception {
		final StockAlgoInitHelper init = new StockAlgoInitHelper("in", "aapl");
		final Input in = new Input(init.getInit());

		final StockAlgoInitHelper lssInit = new StockAlgoInitHelper("lss", "aapl", init.getStorage());
		lssInit.getSettings().addSubExecutionName("in");
		final LeastSquaresStraight lss = new LeastSquaresStraight(lssInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			in.process(day);
			lss.process(day);

			if (i - aaplIndex < 5) {
				final List<Double> values = init.getStorage().getStockSignal("aapl", "lss", day.getDate())
						.getSignal(ListOfDoubleSignal.class).getValues();
				Assert.assertEquals(2, values.size());
				Assert.assertEquals(0.0, values.get(0), Settings.doubleEpsilon);
				Assert.assertEquals(0.0, values.get(1), Settings.doubleEpsilon);
			} else {
				final List<Double> values = init.getStorage().getStockSignal("aapl", "lss", day.getDate())
						.getSignal(ListOfDoubleSignal.class).getValues();
				Assert.assertEquals(2, values.size());

				double sumX = 0.0;
				double sumY = 0.0;
				double sumXY = 0.0;
				double sumXX = 0.0;
				for (int u = i - 4; i <= u; ++u) {
					final double x = (u - i);
					final double y = days.get(u).getPrices().getOpen();
					sumX += x;
					sumY += y;
					sumXY += x * y;
					sumXX += x * x;
					final double divider = (5 * sumXX - (sumX * sumX));
					final double a0 = (sumY * sumXX - sumX * sumXY) / divider;
					final double a1 = (5 * sumXY - sumY * sumX) / divider;
					Assert.assertEquals(a0, values.get(0), Settings.doubleEpsilon);
					Assert.assertEquals(a1, values.get(1), Settings.doubleEpsilon);
				}
			}
		}
	}
}
