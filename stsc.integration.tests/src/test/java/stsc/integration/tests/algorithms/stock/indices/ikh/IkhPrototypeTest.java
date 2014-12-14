package stsc.integration.tests.algorithms.stock.indices.ikh;

import java.util.ArrayList;
import java.util.PriorityQueue;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.stock.indices.ikh.IkhPrototype;
import stsc.algorithms.stock.indices.ikh.IkhTenkan;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

public class IkhPrototypeTest {

	@Test
	public void testPriorityQueueForHigh() {
		final PriorityQueue<Double> highs = new PriorityQueue<>((c1, c2) -> {
			return Double.compare(c2, c1);
		});
		highs.add(new Double(5.0));
		highs.add(new Double(5.0));
		highs.add(new Double(7.0));
		highs.add(new Double(8.0));
		highs.add(new Double(4.0));
		highs.add(new Double(-1.0));
		highs.add(new Double(7.0));
		highs.add(new Double(8.0));
		highs.add(new Double(7.0));
		Assert.assertEquals(8.0, highs.poll(), Settings.doubleEpsilon);
		Assert.assertEquals(8.0, highs.poll(), Settings.doubleEpsilon);
		Assert.assertEquals(7.0, highs.poll(), Settings.doubleEpsilon);
		Assert.assertEquals(7.0, highs.poll(), Settings.doubleEpsilon);
		Assert.assertEquals(7.0, highs.poll(), Settings.doubleEpsilon);
		Assert.assertEquals(5.0, highs.poll(), Settings.doubleEpsilon);
		Assert.assertEquals(5.0, highs.poll(), Settings.doubleEpsilon);
		Assert.assertEquals(4.0, highs.poll(), Settings.doubleEpsilon);
		Assert.assertEquals(-1.0, highs.poll(), Settings.doubleEpsilon);
		Assert.assertTrue(highs.isEmpty());
		highs.add(new Double(0.0));
		highs.add(new Double(10.0));
		Assert.assertEquals(10.0, highs.poll(), Settings.doubleEpsilon);
	}

	@Test
	public void testPriorityQueueForLow() {
		final PriorityQueue<Double> lows = new PriorityQueue<>((c1, c2) -> {
			return Double.compare(c1, c2);
		});
		lows.add(new Double(5.0));
		lows.add(new Double(5.0));
		lows.add(new Double(7.0));
		lows.add(new Double(8.0));
		lows.add(new Double(4.0));
		lows.add(new Double(-1.0));
		lows.add(new Double(7.0));
		lows.add(new Double(8.0));
		lows.add(new Double(7.0));
		Assert.assertEquals(-1.0, lows.poll(), Settings.doubleEpsilon);
		Assert.assertEquals(4.0, lows.poll(), Settings.doubleEpsilon);
		Assert.assertEquals(5.0, lows.poll(), Settings.doubleEpsilon);
		Assert.assertEquals(5.0, lows.poll(), Settings.doubleEpsilon);
		Assert.assertEquals(7.0, lows.poll(), Settings.doubleEpsilon);
		Assert.assertEquals(7.0, lows.poll(), Settings.doubleEpsilon);
		Assert.assertEquals(7.0, lows.poll(), Settings.doubleEpsilon);
		Assert.assertEquals(8.0, lows.poll(), Settings.doubleEpsilon);
		Assert.assertEquals(8.0, lows.poll(), Settings.doubleEpsilon);
		Assert.assertTrue(lows.isEmpty());
		lows.add(new Double(0.0));
		lows.add(new Double(10.0));
		Assert.assertEquals(0.0, lows.poll(), Settings.doubleEpsilon);
	}

	@Test
	public void testIkhPrototype() throws Exception {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final int tm = 26;
		final int ts = 9;

		final StockAlgoInitHelper senkouAInit = new StockAlgoInitHelper("senkouA", "aapl", stockInit.getStorage());
		senkouAInit.getSettings().setInteger("TM", tm);
		senkouAInit.getSettings().setInteger("TS", ts);
		senkouAInit.getSettings().setInteger("size", 10000);
		final IkhPrototype senkoA = new IkhPrototype(senkouAInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			senkoA.process(day);
			if (i - tm - ts + 1 >= aaplIndex) {
				double highMax = -Double.MIN_VALUE;
				double lowMin = Double.MAX_VALUE;
				for (int u = i - tm - ts + 1; u < i - tm + 1; ++u) {
					highMax = Math.max(highMax, days.get(u).getPrices().getHigh());
					lowMin = Math.min(lowMin, days.get(u).getPrices().getLow());
				}
				final double vSenkou = stockInit.getStorage().getStockSignal("aapl", "senkouA", day.getDate())
						.getSignal(DoubleSignal.class).getValue();
				Assert.assertEquals((highMax + lowMin) / 2.0, vSenkou, Settings.doubleEpsilon);
			}
		}
	}

	@Test
	public void testIkhPrototypeWithTenkau() throws Exception {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");

		final int tm = 0;
		final int ts = 9;

		final StockAlgoInitHelper prototypeInit = new StockAlgoInitHelper("prototype", "aapl", stockInit.getStorage());
		prototypeInit.getSettings().setInteger("TM", tm);
		prototypeInit.getSettings().setInteger("TS", ts);
		prototypeInit.getSettings().setInteger("size", 10000);
		final IkhPrototype prototype = new IkhPrototype(prototypeInit.getInit());

		final StockAlgoInitHelper tenkanInit = new StockAlgoInitHelper("tenkan", "aapl", stockInit.getStorage());
		tenkanInit.getSettings().setInteger("TS", ts);
		tenkanInit.getSettings().setInteger("size", 10000);
		final IkhTenkan tenkan = new IkhTenkan(tenkanInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			prototype.process(day);
			tenkan.process(day);
			if (i - tm - ts >= aaplIndex) {
				final double vPrototype = stockInit.getStorage().getStockSignal("aapl", "prototype", day.getDate())
						.getSignal(DoubleSignal.class).getValue();
				final double vTenkan = stockInit.getStorage().getStockSignal("aapl", "tenkan", day.getDate()).getSignal(DoubleSignal.class)
						.getValue();
				Assert.assertEquals(vTenkan, vPrototype, Settings.doubleEpsilon);
			}
		}
	}

}
