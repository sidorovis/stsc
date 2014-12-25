package stsc.integration.tests.algorithms.stock.geometry;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.geometry.LeastSquaresQuadraticStdDev;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;
import stsc.signals.ListOfDoubleSignal;

public class LeastSquaresQuadraticStdDevTest {

	@Test
	public void testLeastSquaresStraightStdDev() throws Exception {
		final StockAlgoInitHelper init = new StockAlgoInitHelper("in", "aapl");
		final Input in = new Input(init.getInit());

		final StockAlgoInitHelper lsqInit = new StockAlgoInitHelper("lsqp", "aapl", init.getStorage());
		lsqInit.getSettings().addSubExecutionName("in");
		final LeastSquaresQuadraticStdDev lsqp = new LeastSquaresQuadraticStdDev(lsqInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2005, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			in.process(day);
			lsqp.process(day);

			final double a0 = init.getStorage().getStockSignal("aapl", "lsqp_Lsq", day.getDate()).getSignal(ListOfDoubleSignal.class)
					.getValues().get(0);
			final double a1 = init.getStorage().getStockSignal("aapl", "lsqp_Lsq", day.getDate()).getSignal(ListOfDoubleSignal.class)
					.getValues().get(1);
			final double a2 = init.getStorage().getStockSignal("aapl", "lsqp_Lsq", day.getDate()).getSignal(ListOfDoubleSignal.class)
					.getValues().get(2);
			double diffSum = 0.0;
			double x = i - Math.min(5, i - aaplIndex);
			for (int u = i - Math.min(5, i - aaplIndex); i + 1 > u; ++u) {
				final double xV = (x - aaplIndex);
				diffSum += Math.pow(days.get(u).getPrices().getOpen() - (a0 + a1 * xV + a2 * (xV * xV)), 2.0);
				x += 1.0;
			}

			final double v = init.getStorage().getStockSignal("aapl", "lsqp", day.getDate()).getSignal(DoubleSignal.class).getValue();
			Assert.assertEquals(diffSum, v, Settings.doubleEpsilon);
		}
	}
}
