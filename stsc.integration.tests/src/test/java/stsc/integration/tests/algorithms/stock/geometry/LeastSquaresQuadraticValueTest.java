package stsc.integration.tests.algorithms.stock.geometry;

import java.util.ArrayList;
import java.util.List;

import org.ejml.factory.SingularMatrixException;
import org.ejml.simple.SimpleMatrix;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.geometry.LeastSquaresQuadraticValue;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.ListOfDoubleSignal;

public class LeastSquaresQuadraticValueTest {

	@Test
	public void testMatrixLinearCalculation() {
		final SimpleMatrix A = new SimpleMatrix(3, 3);
		A.set(0, 0, 1);
		A.set(0, 1, 2);
		A.set(0, 2, 3);

		A.set(1, 0, 3);
		A.set(1, 1, 2);
		A.set(1, 2, 1);

		A.set(2, 0, 4);
		A.set(2, 1, 2);
		A.set(2, 2, 1);

		final SimpleMatrix b = new SimpleMatrix(3, 1);
		b.set(0, 0, 6);
		b.set(1, 0, 6);
		b.set(2, 0, 7);

		final SimpleMatrix x = A.solve(b);
		Assert.assertEquals(1, x.get(0, 0), Settings.doubleEpsilon);
		Assert.assertEquals(1, x.get(1, 0), Settings.doubleEpsilon);
		Assert.assertEquals(1, x.get(2, 0), Settings.doubleEpsilon);
	}

	@Test
	public void testLeastSquaresQuadraticValue() throws Exception {
		final StockAlgoInitHelper init = new StockAlgoInitHelper("in", "aapl");
		final Input in = new Input(init.getInit());

		final StockAlgoInitHelper lsqInit = new StockAlgoInitHelper("lsq", "aapl", init.getStorage());
		lsqInit.getSettings().addSubExecutionName("in");
		final LeastSquaresQuadraticValue lsq = new LeastSquaresQuadraticValue(lsqInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2001, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			in.process(day);
			lsq.process(day);

			final List<Double> values = init.getStorage().getStockSignal("aapl", "lsq", day.getDate()).getSignal(ListOfDoubleSignal.class)
					.getValues();
			Assert.assertEquals(3, values.size());

			double sumXXXX = 0.0;
			double sumXXX = 0.0;
			double sumXX = 0.0;
			double sumX = 0.0;
			double sumXXY = 0.0;
			double sumXY = 0.0;
			double sumY = 0.0;
			double N = Math.min(4, i - aaplIndex) + 1;

			for (int u = i - Math.min(4, i - aaplIndex); i + 1 > u; ++u) {
				final double x = (u - aaplIndex);
				final double y = days.get(u).getPrices().getOpen();

				sumXXXX += Math.pow(x, 4);
				sumXXX += Math.pow(x, 3);
				sumXX += Math.pow(x, 2);
				sumX += x;
				sumXXY += Math.pow(x, 2) * y;
				sumXY += x * y;
				sumY += y;
			}
			checkValues(values, sumXXXX, sumXXX, sumXX, sumX, sumXXY, sumXY, sumY, N, days.get(i).getPrices().getOpen());
		}
	}

	private void checkValues(List<Double> values, double sumXXXX, double sumXXX, double sumXX, double sumX, double sumXXY, double sumXY,
			double sumY, double n, double y) {
		final SimpleMatrix sm = new SimpleMatrix(3, 3);
		sm.set(0, 0, sumXXXX);
		sm.set(0, 1, sumXXX);
		sm.set(0, 2, sumXX);

		sm.set(1, 0, sumXXX);
		sm.set(1, 1, sumXX);
		sm.set(1, 2, sumX);

		sm.set(2, 0, sumXX);
		sm.set(2, 1, sumX);
		sm.set(2, 2, n);

		final SimpleMatrix b = new SimpleMatrix(3, 1);
		b.set(0, 0, sumXXY);
		b.set(1, 0, sumXY);
		b.set(2, 0, sumY);

		SimpleMatrix x;
		try {
			x = sm.solve(b);
			Assert.assertEquals(x.get(2, 0), values.get(0), 100000 * Settings.doubleEpsilon);
			Assert.assertEquals(x.get(1, 0), values.get(1), 100000 * Settings.doubleEpsilon);
			Assert.assertEquals(x.get(0, 0), values.get(2), 100000 * Settings.doubleEpsilon);
		} catch (SingularMatrixException sme) {
			Assert.assertEquals(y, values.get(0), Settings.doubleEpsilon);
			Assert.assertEquals(0, values.get(1), Settings.doubleEpsilon);
			Assert.assertEquals(0, values.get(2), Settings.doubleEpsilon);
		}
	}
}
