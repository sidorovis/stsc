package stsc.integration.tests.algorithms.stock.geometry;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.geometry.LeastSquaresStraightStdDev;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;
import stsc.signals.ListOfDoubleSignal;

public class LeastSquaresStraightStdDevTest {

	@Test
	public void testLeastSquaresStraightStdDev() throws Exception {
		final StockAlgoInitHelper init = new StockAlgoInitHelper("in", "aapl");
		final Input in = new Input(init.getInit());

		final StockAlgoInitHelper lssInit = new StockAlgoInitHelper("lssp", "aapl", init.getStorage());
		lssInit.getSettings().addSubExecutionName("in");
		final LeastSquaresStraightStdDev lssp = new LeastSquaresStraightStdDev(lssInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			in.process(day);
			lssp.process(day);

			final double a0 = init.getStorage().getStockSignal("aapl", "lssp_Lss", day.getDate()).getContent(ListOfDoubleSignal.class)
					.getValues().get(0);
			final double a1 = init.getStorage().getStockSignal("aapl", "lssp_Lss", day.getDate()).getContent(ListOfDoubleSignal.class)
					.getValues().get(1);
			double diffSum = 0.0;
			double x = i - Math.min(5, i - aaplIndex);
			for (int u = i - Math.min(5, i - aaplIndex); i + 1 > u; ++u) {
				diffSum += Math.pow(days.get(u).getPrices().getOpen() - (a0 + a1 * (x - aaplIndex)), 2.0);
				x += 1.0;
			}

			final double v = init.getStorage().getStockSignal("aapl", "lssp", day.getDate()).getContent(DoubleSignal.class).getValue();
			Assert.assertEquals(diffSum, v, Settings.doubleEpsilon);
		}
	}
}
