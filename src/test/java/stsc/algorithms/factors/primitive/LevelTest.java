package stsc.algorithms.factors.primitive;

import java.util.ArrayList;

import org.joda.time.LocalDate;

import stsc.algorithms.In;
import stsc.algorithms.StockAlgorithm;
import stsc.common.Day;
import stsc.common.Stock;
import stsc.common.UnitedFormatStock;
import stsc.testhelper.TestHelper;
import junit.framework.TestCase;

public class LevelTest extends TestCase {
	public void testLevel() throws Exception {

		final StockAlgorithm.Init stockInit = TestHelper.getStockAlgorithmInit("in", "aapl");
		stockInit.settings.set("e", "open");
		final In in = new In(stockInit);

		StockAlgorithm.Init levelInit = TestHelper.getStockAlgorithmInit("level", "aapl", stockInit.signalsStorage);
		levelInit.settings.addSubExecutionName("in");
		levelInit.settings.set("f", "699.0");
		final Level level = new Level(levelInit);

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			in.process(day);
			level.process(day);
		}
		assertEquals(5, stockInit.signalsStorage.getIndexSize("aapl", "level"));
	}
}
