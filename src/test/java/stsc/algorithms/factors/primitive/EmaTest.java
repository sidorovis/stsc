package stsc.algorithms.factors.primitive;

import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.LocalDate;

import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.DoubleSignal;
import stsc.common.Day;
import stsc.common.Stock;
import stsc.common.UnitedFormatStock;
import stsc.storage.BadSignalException;
import stsc.storage.SignalsStorage;
import junit.framework.TestCase;

public class EmaTest extends TestCase {
	public void testEma() throws IOException, BadSignalException {
		final SignalsStorage ss = new SignalsStorage();
		AlgorithmSettings settings = new AlgorithmSettings();

		final Ema ema = new Ema("aapl", "testEma", ss, settings.set("P", 0.3));

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			ema.process(day);
		}

		assertEquals(days.get(aaplIndex).getPrices().getOpen(),
				ss.getStockSignal("aapl", "testEma", 0).getSignal(DoubleSignal.class).value);

		final double secondValue = days.get(aaplIndex).getPrices().getOpen() * 0.7 + 0.3
				* days.get(aaplIndex + 1).getPrices().getOpen();

		assertEquals(secondValue, ss.getStockSignal("aapl", "testEma", 1).getSignal(DoubleSignal.class).value);

		final int size = ss.getCurrentStockIndex("aapl", "testEma");
		assertEquals(531.20111321, ss.getStockSignal("aapl", "testEma", size - 1).getSignal(DoubleSignal.class).value, 0.000001);
	}
}
