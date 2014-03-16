package stsc.algorithms.factors.primitive;

import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.LocalDate;

import stsc.algorithms.StockAlgorithm;
import stsc.common.Day;
import stsc.common.Stock;
import stsc.common.UnitedFormatStock;
import stsc.signals.BadSignalException;
import stsc.signals.DoubleSignal;
import stsc.testhelper.TestHelper;
import junit.framework.TestCase;

public class SmaTest extends TestCase {
	public void testSma() throws IOException, BadSignalException {

		final StockAlgorithm.Init init = TestHelper.getStockAlgorithmInit();
		init.stockName = "aapl";
		init.executionName = "testSma";
		init.settings.set("n", 5);

		final Sma sma = new Sma(init);

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			sma.process(day);
		}

		assertNull(init.signalsStorage.getStockSignal("aapl", "testSma", days.get(aaplIndex).getDate()));
		assertNull(init.signalsStorage.getStockSignal("aapl", "testSma", days.get(aaplIndex + 3).getDate()));
		assertNotNull(init.signalsStorage.getStockSignal("aapl", "testSma", days.get(aaplIndex + 4).getDate()));
		assertNotNull(init.signalsStorage.getStockSignal("aapl", "testSma", days.get(days.size() - 1).getDate()));

		assertEquals(init.signalsStorage.getStockSignal("aapl", "testSma", days.get(aaplIndex + 4).getDate()),
				init.signalsStorage.getStockSignal("aapl", "testSma", 0));

		Double lastSum = 0.0;
		for (int i = days.size() - 5; i < days.size(); ++i) {
			lastSum += days.get(i).getPrices().getOpen();
		}
		final Day lastDay = days.get(days.size() - 1);
		final double lastSma = init.signalsStorage.getStockSignal("aapl", "testSma", lastDay.getDate()).getSignal(
				DoubleSignal.class).value;
		assertEquals(lastSum / 5, lastSma, 0.000001);
	}
}
