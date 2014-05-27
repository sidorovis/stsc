package stsc.algorithms.stock.factors.primitive;

import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.LocalDate;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.In;
import stsc.algorithms.StockAlgorithmInit;
import stsc.algorithms.stock.factors.primitive.Sma;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.Stock;
import stsc.common.UnitedFormatStock;
import stsc.signals.DoubleSignal;
import stsc.testhelper.TestAlgorithmsHelper;
import junit.framework.TestCase;

public class SmaTest extends TestCase {
	public void testSma() throws IOException, BadSignalException, BadAlgorithmException {

		StockAlgorithmInit stockInit = TestAlgorithmsHelper.getStockAlgorithmInit("testIn", "aapl");
		stockInit.settings.set("e", "open");
		final In inAlgo = new In(stockInit);

		StockAlgorithmInit stockInitClose = TestAlgorithmsHelper.getStockAlgorithmInit("testInClose", "aapl", stockInit.signalsStorage);
		stockInitClose.settings.set("e", "close");
		final In inAlgoClose = new In(stockInitClose);

		final StockAlgorithmInit init = TestAlgorithmsHelper.getStockAlgorithmInit("testSma", "aapl", stockInit.signalsStorage);
		init.settings.setInteger("n", 5);
		init.settings.setInteger("size", 10000);
		init.settings.addSubExecutionName("testIn");
		final Sma sma = new Sma(init);

		final StockAlgorithmInit initClose = TestAlgorithmsHelper.getStockAlgorithmInit("testSmaClose", "aapl", stockInit.signalsStorage);
		initClose.settings.setInteger("n", 5);
		initClose.settings.setInteger("size", 10000);
		initClose.settings.addSubExecutionName("testInClose");
		final Sma smaClose = new Sma(initClose);

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			inAlgo.process(day);
			inAlgoClose.process(day);
			sma.process(day);
			smaClose.process(day);
		}

		assertNotNull(init.signalsStorage.getStockSignal("aapl", "testSma", days.get(aaplIndex).getDate()));
		assertNotNull(init.signalsStorage.getStockSignal("aapl", "testSma", days.get(aaplIndex + 3).getDate()));
		assertNotNull(init.signalsStorage.getStockSignal("aapl", "testSma", days.get(aaplIndex + 4).getDate()));
		assertNotNull(init.signalsStorage.getStockSignal("aapl", "testSma", days.get(days.size() - 1).getDate()));

		assertEquals(init.signalsStorage.getStockSignal("aapl", "testSma", days.get(aaplIndex + 4).getDate()),
				init.signalsStorage.getStockSignal("aapl", "testSma", 4));

		assertEquals(init.signalsStorage.getStockSignal("aapl", "testSma", days.get(aaplIndex).getDate()),
				init.signalsStorage.getStockSignal("aapl", "testSma", 0));

		Double lastSum = 0.0;
		Double lastSumClose = 0.0;
		for (int i = days.size() - 5; i < days.size(); ++i) {
			lastSum += days.get(i).getPrices().getOpen();
			lastSumClose += days.get(i).getPrices().getClose();
		}
		final Day lastDay = days.get(days.size() - 1);
		final double lastSma = init.signalsStorage.getStockSignal("aapl", "testSma", lastDay.getDate()).getSignal(DoubleSignal.class).value;
		assertEquals(lastSum / 5, lastSma, Settings.doubleEpsilon);

		final double lastSmaClose = init.signalsStorage.getStockSignal("aapl", "testSmaClose", lastDay.getDate()).getSignal(DoubleSignal.class).value;
		assertEquals(lastSumClose / 5, lastSmaClose, Settings.doubleEpsilon);
	}
}
