package stsc.integration.tests.algorithms.stock.geometry;

import java.util.ArrayList;

import org.joda.time.LocalDate;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.stock.geometry.FibonacciRetracementBullStdDev;
import stsc.common.Day;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.integration.tests.helper.StockAlgoInitHelper;

public class FibonacciRetracementBullStdDevTest {

	@Test
	public void testFibonacciRetracementBullStdDev() throws Exception {
		final StockAlgoInitHelper init = new StockAlgoInitHelper("in", "aapl");
		init.getSettings().setInteger("size", 6);
		final Input in = new Input(init.getInit());

		final StockAlgoInitHelper frInit = new StockAlgoInitHelper("fr", "aapl", init.getStorage());
		frInit.getSettings().addSubExecutionName("in");
		final FibonacciRetracementBullStdDev fr = new FibonacciRetracementBullStdDev(frInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final int aaplIndex = aapl.findDayIndex(new LocalDate(2005, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			in.process(day);
			fr.process(day);

		}
	}
}
