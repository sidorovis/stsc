package stsc.algorithms.stock.factors.primitive;

import java.util.ArrayList;

import org.joda.time.LocalDate;

import stsc.algorithms.In;
import stsc.algorithms.StockAlgorithmInit;
import stsc.algorithms.stock.factors.primitive.Level;
import stsc.common.Day;
import stsc.common.Stock;
import stsc.common.UnitedFormatStock;
import stsc.testhelper.TestAlgorithmsHelper;
import junit.framework.TestCase;

public class LevelTest extends TestCase {
	public void testLevel() throws Exception {

		final StockAlgorithmInit stockInit = TestAlgorithmsHelper.getStockAlgorithmInit("in", "aapl");
		stockInit.settings.set("e", "open");
		final In in = new In(stockInit);

		StockAlgorithmInit levelInit = TestAlgorithmsHelper.getStockAlgorithmInit("level", "aapl", stockInit.signalsStorage);
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
