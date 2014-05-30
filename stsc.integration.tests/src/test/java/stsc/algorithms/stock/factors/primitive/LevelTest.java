package stsc.algorithms.stock.factors.primitive;

import java.util.ArrayList;

import org.joda.time.LocalDate;

import stsc.algorithms.In;
import stsc.algorithms.stock.factors.primitive.Level;
import stsc.common.Day;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.testhelper.StockAlgoInitHelper;
import junit.framework.TestCase;

public class LevelTest extends TestCase {
	public void testLevel() throws Exception {

		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");
		stockInit.getSettings().set("e", "open");
		final In in = new In(stockInit.getInit());

		final StockAlgoInitHelper levelInit = new StockAlgoInitHelper("level", "aapl", stockInit.getStorage());
		levelInit.getSettings().addSubExecutionName("in");
		levelInit.getSettings().set("f", "699.0");
		final Level level = new Level(levelInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			in.process(day);
			level.process(day);
		}
		assertEquals(5, stockInit.getStorage().getIndexSize("aapl", "level"));
	}
}
