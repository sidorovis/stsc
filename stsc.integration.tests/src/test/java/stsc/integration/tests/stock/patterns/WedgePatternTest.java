package stsc.integration.tests.stock.patterns;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.patterns.WedgePattern;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.signals.SignalContainer;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.SignalsStorage;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;
import stsc.signals.ListOfDoubleSignal;

public class WedgePatternTest {

	@Test
	public void testGetCrossX() {
		// maximum line: y = a1 * x + b1
		// minimum line: y = a2 * x + b2
		Assert.assertEquals(2.0, WedgePattern.getCrossXY(0.5, 0, -1, 3).get(0), Settings.doubleEpsilon);
		Assert.assertEquals(1.0, WedgePattern.getCrossXY(0.5, 0, -1, 3).get(1), Settings.doubleEpsilon);

		Assert.assertEquals(2.0, WedgePattern.getCrossXY(0.25, 0, -0.5, 1.5).get(0), Settings.doubleEpsilon);
		Assert.assertEquals(0.5, WedgePattern.getCrossXY(0.25, 0, -0.5, 1.5).get(1), Settings.doubleEpsilon);

		Assert.assertEquals(2.0, WedgePattern.getCrossXY(1, 0, 0, 2).get(0), Settings.doubleEpsilon);
		Assert.assertEquals(2.0, WedgePattern.getCrossXY(1, 0, 0, 2).get(1), Settings.doubleEpsilon);

		Assert.assertEquals(Double.NaN, WedgePattern.getCrossXY(0, 3, 0, 2).get(0), Settings.doubleEpsilon);
		Assert.assertEquals(Double.NaN, WedgePattern.getCrossXY(0, 3, 0, 2).get(1), Settings.doubleEpsilon);
	}

	private void testHelper(String sn) throws Exception {
		final StockAlgoInitHelper iniHigh = new StockAlgoInitHelper("inh", sn);
		iniHigh.getSettings().setString("e", "high");
		final Input inHigh = new Input(iniHigh.getInit());

		final StockAlgoInitHelper iniLow = new StockAlgoInitHelper("inl", sn, iniHigh.getStorage());
		iniHigh.getSettings().setString("e", "low");
		final Input inLow = new Input(iniLow.getInit());

		final StockAlgoInitHelper wpInit = new StockAlgoInitHelper("wp", sn, iniLow.getStorage());
		wpInit.getSettings().addSubExecutionName("inh");
		wpInit.getSettings().addSubExecutionName("inl");
		final WedgePattern wp = new WedgePattern(wpInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/" + sn + ".uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(1990, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		final SignalsStorage ss = iniHigh.getStorage();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			inHigh.process(day);
			inLow.process(day);
			wp.process(day);

			final SignalContainer<?> sc = ss.getStockSignal(sn, "wp", day.getDate());
			if (sc == null) {
				continue;
			}
			final List<Double> v = sc.getSignal(ListOfDoubleSignal.class).getValues();

			final double x = v.get(1);
			final double y = v.get(2);

			Assert.assertTrue(x >= (i - aaplIndex + 2));
			Assert.assertTrue(x <= (i - aaplIndex + 3));

			final double maxLineStdDev = ss.getStockSignal(sn, "wp_Max", day.getDate()).getSignal(DoubleSignal.class).getValue();
			final double minLineStdDev = ss.getStockSignal(sn, "wp_Min", day.getDate()).getSignal(DoubleSignal.class).getValue();

			final List<Double> lcMax = ss.getStockSignal(sn, "wp_Max_Lss", day.getDate()).getSignal(ListOfDoubleSignal.class).getValues();
			final double eY = lcMax.get(0) + lcMax.get(1) * x;

			final List<Double> lcMin = ss.getStockSignal(sn, "wp_Min_Lss", day.getDate()).getSignal(ListOfDoubleSignal.class).getValues();

			Assert.assertEquals(eY, y, Settings.doubleEpsilon);

			Assert.assertTrue(0.5 > maxLineStdDev);
			Assert.assertTrue(0.5 > minLineStdDev);

			if (v.get(0) < 0.0) {
				Assert.assertTrue(lcMax.get(1) < -0.05);
				Assert.assertTrue(lcMin.get(1) < -0.05);
			} else {
				Assert.assertTrue(lcMax.get(1) > 0.05);
				Assert.assertTrue(lcMin.get(1) > 0.05);
			}
		}
	}

	@Test
	public void testWedgePattern() throws Exception {
		testHelper("aa");
		testHelper("aapl");
		testHelper("adm");
		testHelper("apa");
		testHelper("spy");
	}
}
