package stsc.algorithms.factors.primitive;

import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.LocalDate;

import stsc.algorithms.AlgorithmSettings;
import stsc.common.Day;
import stsc.common.Stock;
import stsc.common.UnitedFormatStock;
import stsc.storage.BadSignalException;
import stsc.storage.SignalsStorage;
import junit.framework.TestCase;

public class SmaTest extends TestCase {
	public void testSma() throws IOException, BadSignalException {

		final SignalsStorage signalsStorage = new SignalsStorage();
		AlgorithmSettings settings = new AlgorithmSettings();

		final Sma sma = new Sma("testSma",signalsStorage,settings.set("n", 5));

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			sma.process("aapl", day);
		}

		assertNull(signalsStorage.getStockSignal("testSma", days.get(aaplIndex).getDate()));
		assertNull(signalsStorage.getStockSignal("testSma", days.get(aaplIndex + 2).getDate()));
		assertNotNull(signalsStorage.getStockSignal("testSma", days.get(aaplIndex + 4).getDate()));
		assertNotNull(signalsStorage.getStockSignal("testSma", days.get(days.size() - 1).getDate()));

		Double lastSum = 0.0;
		for (int i = days.size() - 5; i < days.size(); ++i) {
			lastSum += days.get(i).getPrices().getOpen();
		}
		Day lastDay = days.get(days.size() - 1);
		final double lastSma = ((Sma.Signal) signalsStorage.getStockSignal("testSma", lastDay.getDate())).value;
		assertEquals(lastSum / 5, lastSma, 0.000001);
	}
}
