package stsc.algorithms.stock.factors.primitive;

import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.LocalDate;

import stsc.algorithms.In;
import stsc.algorithms.stock.factors.primitive.Ema;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.signals.DoubleSignal;
import stsc.testhelper.TestAlgorithmsHelper;
import junit.framework.TestCase;

public class EmaTest extends TestCase {
	public void testEma() throws IOException, BadSignalException, BadAlgorithmException {

		StockAlgorithmInit stockInit = TestAlgorithmsHelper.getStockAlgorithmInit("testIn", "aapl");
		stockInit.settings.set("e", "open");
		final In inAlgo = new In(stockInit);

		StockAlgorithmInit init = TestAlgorithmsHelper.getStockAlgorithmInit("testEma", "aapl", stockInit.signalsStorage);
		init.settings.set("P", 0.3);
		init.settings.setInteger("size", 100000);
		init.settings.addSubExecutionName("testIn");

		final Ema ema = new Ema(init);

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			inAlgo.process(day);
			ema.process(day);
		}

		assertEquals(days.get(aaplIndex).getPrices().getOpen(), init.signalsStorage.getStockSignal("aapl", "testEma", 0).getSignal(DoubleSignal.class).value);

		final double secondValue = days.get(aaplIndex).getPrices().getOpen() * 0.7 + 0.3 * days.get(aaplIndex + 1).getPrices().getOpen();

		assertEquals(secondValue, init.signalsStorage.getStockSignal("aapl", "testEma", 1).getSignal(DoubleSignal.class).value);

		final int size = init.signalsStorage.getIndexSize("aapl", "testEma");
		assertEquals(531.20111321, init.signalsStorage.getStockSignal("aapl", "testEma", size - 1).getSignal(DoubleSignal.class).value, Settings.doubleEpsilon);
	}
}