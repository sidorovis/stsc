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

public class EmaTest extends TestCase {
	public void testEma() throws IOException, BadSignalException {

		StockAlgorithm.Init init = TestHelper.getStockAlgorithmInit();
		init.executionName = "testEma";
		init.stockName = "aapl";
		init.settings.set("P", 0.3);

		final Ema ema = new Ema(init);

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			ema.process(day);
		}

		assertEquals(days.get(aaplIndex).getPrices().getOpen(),
				init.signalsStorage.getStockSignal("aapl", "testEma", 0).getSignal(DoubleSignal.class).value);

		final double secondValue = days.get(aaplIndex).getPrices().getOpen() * 0.7 + 0.3
				* days.get(aaplIndex + 1).getPrices().getOpen();

		assertEquals(secondValue, init.signalsStorage.getStockSignal("aapl", "testEma", 1).getSignal(DoubleSignal.class).value);

		final int size = init.signalsStorage.getIndexSize("aapl", "testEma");
		assertEquals(531.20111321, init.signalsStorage.getStockSignal("aapl", "testEma", size - 1).getSignal(DoubleSignal.class).value,
				0.000001);
	}
}
