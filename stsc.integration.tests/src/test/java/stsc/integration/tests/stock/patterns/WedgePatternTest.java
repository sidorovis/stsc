package stsc.integration.tests.stock.patterns;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.patterns.WedgePattern;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;

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

	@Test
	public void testWedgePattern() throws Exception {
		final String stockName = "aa";

		final StockAlgoInitHelper iniHigh = new StockAlgoInitHelper("inh", stockName);
		iniHigh.getSettings().setString("e", "high");
		final Input inHigh = new Input(iniHigh.getInit());

		final StockAlgoInitHelper iniLow = new StockAlgoInitHelper("inl", stockName, iniHigh.getStorage());
		iniHigh.getSettings().setString("e", "low");
		final Input inLow = new Input(iniLow.getInit());

		final StockAlgoInitHelper wpInit = new StockAlgoInitHelper("wp", stockName, iniLow.getStorage());
		wpInit.getSettings().addSubExecutionName("inh");
		wpInit.getSettings().addSubExecutionName("inl");
		final WedgePattern wp = new WedgePattern(wpInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/" + stockName + ".uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			inHigh.process(day);
			inLow.process(day);
			wp.process(day);

		}
	}
}
