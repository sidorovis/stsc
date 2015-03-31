package stsc.integration.tests.algorithms.stock.indices;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.indices.ResistanceLevel;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.signals.DoubleSignal;

import com.google.common.collect.Multiset;
import com.google.common.collect.TreeMultiset;

public class ResistanceLevelTest {

	@Test
	public void testResistanceLevel() throws ParseException, BadAlgorithmException, IOException, BadSignalException {
		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("testIn", "aapl");
		stockInit.getSettings().setString("e", "open");
		final Input inAlgo = new Input(stockInit.getInit());

		final StockAlgoInitHelper rlInit = new StockAlgoInitHelper("rl", "aapl", stockInit.getStorage());
		rlInit.getSettings().addSubExecutionName("testIn");
		final ResistanceLevel rl = new ResistanceLevel(rlInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			inAlgo.process(days.get(i));
			rl.process(days.get(i));
			final double value = stockInit.getStorage().getStockSignal("aapl", "rl", i - aaplIndex).getContent(DoubleSignal.class)
					.getValue();

			final Multiset<Double> maxValues = TreeMultiset.create(Comparator.reverseOrder());
			final int mathMin = Math.min(i - aaplIndex, 66);
			for (int u = 0; u < mathMin + 1; ++u) {
				maxValues.add(days.get(i - mathMin + u).getPrices().getOpen());
			}
			double sum = 0.0;
			final Iterator<Double> iter = maxValues.iterator();

			for (int u = 0; u < 8 && iter.hasNext(); ++u) {
				final double v = iter.next();
				sum += v;
			}
			Assert.assertEquals(sum / Math.min(8, maxValues.size()), value, Settings.doubleEpsilon);
		}
	}

}
