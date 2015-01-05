package stsc.integration.tests.stock.patterns;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.patterns.TrianglePattern;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.signals.SignalContainer;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.SignalsStorage;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;
import stsc.signals.ListOfDoubleSignal;

public class TrianglePatternTest {

	private void testHelper(String sn) throws Exception {
		final StockAlgoInitHelper iniHigh = new StockAlgoInitHelper("inh", sn);
		iniHigh.getSettings().setString("e", "high");
		final Input inHigh = new Input(iniHigh.getInit());

		final StockAlgoInitHelper iniLow = new StockAlgoInitHelper("inl", sn, iniHigh.getStorage());
		iniHigh.getSettings().setString("e", "low");
		final Input inLow = new Input(iniLow.getInit());

		final StockAlgoInitHelper tpInit = new StockAlgoInitHelper("tp", sn, iniLow.getStorage());
		tpInit.getSettings().addSubExecutionName("inh");
		tpInit.getSettings().addSubExecutionName("inl");
		final TrianglePattern tp = new TrianglePattern(tpInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/" + sn + ".uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(1990, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		final SignalsStorage ss = iniHigh.getStorage();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			inHigh.process(day);
			inLow.process(day);
			tp.process(day);

			final SignalContainer<?> sc = ss.getStockSignal(sn, "tp", day.getDate());
			if (sc == null) {
				continue;
			}
			final List<Double> v = sc.getSignal(ListOfDoubleSignal.class).getValues();

			final double x = v.get(1);
			final double y = v.get(2);

			Assert.assertTrue(x >= (i - aaplIndex + 2));
			Assert.assertTrue(x <= (i - aaplIndex + 3));

			final double maxLineStdDev = ss.getStockSignal(sn, "tp_Max", day.getDate()).getSignal(DoubleSignal.class).getValue();
			final double minLineStdDev = ss.getStockSignal(sn, "tp_Min", day.getDate()).getSignal(DoubleSignal.class).getValue();

			final List<Double> lcMax = ss.getStockSignal(sn, "tp_Max_Lss", day.getDate()).getSignal(ListOfDoubleSignal.class).getValues();
			final double eY = lcMax.get(0) + lcMax.get(1) * x;

			final List<Double> lcMin = ss.getStockSignal(sn, "tp_Min_Lss", day.getDate()).getSignal(ListOfDoubleSignal.class).getValues();

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
	public void testTrianglePattern() throws Exception {
		testHelper("aa");
		testHelper("aapl");
		testHelper("adm");
		testHelper("apa");
		testHelper("spy");
	}
}
