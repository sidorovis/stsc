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

public class EmaTest extends TestCase {
	public void testEma() throws IOException, BadSignalException {
		final SignalsStorage signalsStorage = new SignalsStorage();
		AlgorithmSettings settings = new AlgorithmSettings();

		final Sma sma = new Sma("aapl", "testSma", signalsStorage, settings.set("n", 5));

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			sma.process(day);
		}
	}
}
