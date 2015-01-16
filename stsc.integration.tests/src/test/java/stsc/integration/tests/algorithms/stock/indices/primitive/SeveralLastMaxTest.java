package stsc.integration.tests.algorithms.stock.indices.primitive;

import java.util.ArrayList;
import java.util.TreeSet;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.indices.primitive.SeveralLastMax;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class SeveralLastMaxTest {

	@Test
	public void testDifferentSortOrder() {
		final TreeSet<Double> asc = new TreeSet<>((c1, c2) -> {
			return Double.compare(c2, c1);
		});
		final TreeSet<Double> desc = new TreeSet<>((c1, c2) -> {
			return Double.compare(c1, c2);
		});
		asc.add(15.0);
		asc.add(17.0);
		asc.add(13.0);
		asc.add(14.0);
		asc.add(19.0);

		desc.add(15.0);
		desc.add(17.0);
		desc.add(13.0);
		desc.add(14.0);
		desc.add(19.0);

		Assert.assertEquals(13.0, desc.first(), Settings.doubleEpsilon);
		Assert.assertEquals(19.0, asc.first(), Settings.doubleEpsilon);
	}

	@Test
	public void testSeveralLastMax() throws Exception {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("testIn", "aa");
		final Input inAlgo = new Input(stockInit.getInit());

		final StockAlgoInitHelper init = new StockAlgoInitHelper("slm", "aa", stockInit.getStorage());
		init.getSettings().setInteger("N", 2);
		init.getSettings().addSubExecutionName("testIn");
		final SeveralLastMax slm = new SeveralLastMax(init.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aa.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			inAlgo.process(day);

			slm.process(day);

			if (stockInit.getStorage().getStockSignal("aa", "slm", day.getDate()) == null) {
				continue;
			}

			final double v = stockInit.getStorage().getStockSignal("aa", "slm", day.getDate()).getContent(DoubleSignal.class).getValue();

			double max = Double.MIN_VALUE;
			for (int u = i - Math.min(2, i - aaplIndex + 1) + 1; u <= i; ++u) {
				max = Math.max(max, days.get(u).getPrices().getOpen());
			}

			Assert.assertEquals(max, v, Settings.doubleEpsilon);
		}
	}
}
