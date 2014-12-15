package stsc.integration.tests.algorithms.stock.indices.primitive;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.indices.primitive.Level;
import stsc.common.Day;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;

public class LevelTest {

	@Test
	public void testLevel() throws Exception {

		final StockAlgoInitHelper stockInit = new StockAlgoInitHelper("in", "aapl");
		stockInit.getSettings().setString("e", "open");
		final Input in = new Input(stockInit.getInit());

		final StockAlgoInitHelper levelInit = new StockAlgoInitHelper("level", "aapl", stockInit.getStorage());
		levelInit.getSettings().addSubExecutionName("in");
		levelInit.getSettings().setDouble("f", 667.0);
		final Level level = new Level(levelInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			in.process(day);
			level.process(day);
		}
		Assert.assertEquals(5, stockInit.getStorage().getIndexSize("aapl", "level"));
	}
}
