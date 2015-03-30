package stsc.integration.tests.algorithms.stock.indices;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.indices.SupportLevel;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

import com.google.common.collect.TreeMultiset;

public class SupportLevelTest {

	@Test
	public void testSortedTreeSet() {
		final TreeMultiset<Double> lastValuesByMax = TreeMultiset.create(new Comparator<Double>() {
			@Override
			public int compare(Double o1, Double o2) {
				return o1.compareTo(o2);
			}
		});
		lastValuesByMax.add(Double.valueOf(15.0));
		lastValuesByMax.add(Double.valueOf(15.0));
		lastValuesByMax.add(Double.valueOf(15.0));
		lastValuesByMax.add(Double.valueOf(15.0));
		Assert.assertEquals(4, lastValuesByMax.size());
		Assert.assertEquals(15.0, lastValuesByMax.iterator().next(), Settings.doubleEpsilon);
		lastValuesByMax.add(Double.valueOf(9.0));
		lastValuesByMax.add(Double.valueOf(21.0));
		Assert.assertEquals(9.0, lastValuesByMax.iterator().next(), Settings.doubleEpsilon);
		lastValuesByMax.remove(lastValuesByMax.firstEntry().getElement());
		Assert.assertEquals(15.0, lastValuesByMax.iterator().next(), Settings.doubleEpsilon);
		lastValuesByMax.remove(lastValuesByMax.firstEntry().getElement(), 4);
		Assert.assertEquals(21.0, lastValuesByMax.iterator().next(), Settings.doubleEpsilon);
		lastValuesByMax.remove(lastValuesByMax.firstEntry().getElement());
		Assert.assertEquals(0, lastValuesByMax.size());
	}

	@Test
	public void testSortedPairs() {
		final TreeMultiset<Pair<Integer, Double>> elementsSortedByMax = TreeMultiset.create(new Comparator<Pair<Integer, Double>>() {
			@Override
			public int compare(Pair<Integer, Double> arg0, Pair<Integer, Double> arg1) {
				return arg1.getRight().compareTo(arg0.getRight());
			}
		});

		elementsSortedByMax.add(new ImmutablePair<Integer, Double>(15, 25.4));
		elementsSortedByMax.add(new ImmutablePair<Integer, Double>(17, 25.4));
		elementsSortedByMax.add(new ImmutablePair<Integer, Double>(19, 19.15));

		Assert.assertEquals(3, elementsSortedByMax.size());
		Assert.assertTrue(elementsSortedByMax.remove(new ImmutablePair<Integer, Double>(17, 25.4)));
		Assert.assertEquals(2, elementsSortedByMax.size());
		Assert.assertTrue(elementsSortedByMax.remove(new ImmutablePair<Integer, Double>(15, 25.4)));
		Assert.assertEquals(1, elementsSortedByMax.size());
		Assert.assertTrue(elementsSortedByMax.remove(new ImmutablePair<Integer, Double>(19, 19.15)));

		elementsSortedByMax.add(new ImmutablePair<Integer, Double>(19, 19.15));
		elementsSortedByMax.add(new ImmutablePair<Integer, Double>(15, 25.4));
		elementsSortedByMax.add(new ImmutablePair<Integer, Double>(17, 23.7));

		Iterator<Pair<Integer, Double>> iterator = elementsSortedByMax.iterator();
		Assert.assertEquals(25.4, iterator.next().getRight(), Settings.doubleEpsilon);
		Assert.assertEquals(23.7, iterator.next().getRight(), Settings.doubleEpsilon);
		Assert.assertEquals(19.15, iterator.next().getRight(), Settings.doubleEpsilon);
		Assert.assertFalse(iterator.hasNext());
	}

	@Test
	public void testSupportLevel() throws ParseException, BadAlgorithmException, IOException, BadSignalException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("testIn", "aapl");
		stockInit.getSettings().setString("e", "open");
		final Input inAlgo = new Input(stockInit.getInit());

		final StockAlgoInitHelper slInit = new StockAlgoInitHelper("sl", "aapl", stockInit.getStorage());
		slInit.getSettings().addSubExecutionName("testIn");
		final SupportLevel sl = new SupportLevel(slInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			inAlgo.process(days.get(i));
			sl.process(days.get(i));
			final double value = stockInit.getStorage().getStockSignal("aapl", "sl", i - aaplIndex).getContent(DoubleSignal.class)
					.getValue();
		}
	}

}
