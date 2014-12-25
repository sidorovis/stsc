package stsc.integration.tests.algorithms.stock.geometry;

import java.util.ArrayList;
import java.util.List;

import org.ejml.simple.SimpleMatrix;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.geometry.LeastSquaresStraightValue;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.ListOfDoubleSignal;

public class LeastSquaresStraightTest {

	@Test
	public void testMatrixLinearCalculation() {
		final SimpleMatrix A = new SimpleMatrix(2, 2);
		A.set(0, 0, 1);
		A.set(0, 1, 3);
		A.set(1, 0, 1);
		A.set(1, 1, 2);

		final SimpleMatrix b = new SimpleMatrix(2, 1);
		b.set(0, 0, 5);
		b.set(1, 0, 5);

		final SimpleMatrix x = A.solve(b);
		Assert.assertEquals(5, x.get(0, 0), Settings.doubleEpsilon);
		Assert.assertEquals(0, x.get(1, 0), Settings.doubleEpsilon);
	}

	@Test
	public void testLeastSquaresStraight() throws Exception {
		final StockAlgoInitHelper init = new StockAlgoInitHelper("in", "aapl");
		final Input in = new Input(init.getInit());

		final StockAlgoInitHelper lssInit = new StockAlgoInitHelper("lss", "aapl", init.getStorage());
		lssInit.getSettings().addSubExecutionName("in");
		final LeastSquaresStraightValue lss = new LeastSquaresStraightValue(lssInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			in.process(day);
			lss.process(day);

			final List<Double> values = init.getStorage().getStockSignal("aapl", "lss", day.getDate()).getSignal(ListOfDoubleSignal.class)
					.getValues();
			Assert.assertEquals(2, values.size());

			double sumX = 0.0;
			double sumY = 0.0;
			double sumXY = 0.0;
			double sumXX = 0.0;
			for (int u = i - Math.min(5, i - aaplIndex); i + 1 > u; ++u) {
				final double x = (u - aaplIndex);
				final double y = days.get(u).getPrices().getOpen();
				sumX += x;
				sumY += y;
				sumXY += x * y;
				sumXX += x * x;
			}
			final double divider = (5 * sumXX - (sumX * sumX));
			if (Double.compare(divider, 0.0) != 0) {
				calculateKoefficients(sumXX, sumX, sumXY, sumY, Math.min(5, i - aaplIndex) + 1, values);
			}
		}
	}

	private void calculateKoefficients(double sumXX, double sumX, double sumXY, double sumY, double yS, List<Double> values) {
		final SimpleMatrix A = new SimpleMatrix(2, 2);
		A.set(0, 0, sumXX);
		A.set(1, 0, sumX);
		A.set(0, 1, sumX);
		A.set(1, 1, yS);

		final SimpleMatrix b = new SimpleMatrix(2, 1);
		b.set(0, 0, sumXY);
		b.set(1, 0, sumY);

		final SimpleMatrix x = A.solve(b);

		Assert.assertEquals(x.get(1, 0), values.get(0), Settings.doubleEpsilon);
		Assert.assertEquals(x.get(0, 0), values.get(1), Settings.doubleEpsilon);
	}
}
