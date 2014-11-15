package stsc.yahoo.liquiditator;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import org.joda.time.LocalDate;

import stsc.common.Day;
import stsc.common.stocks.MemoryStock;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.yahoo.liquiditator.StockFilter;
import junit.framework.TestCase;

public class StockFilterTest extends TestCase {
	public void testStockFilter() throws IOException, ParseException, ClassNotFoundException {

		StockFilter stockFilter = new StockFilter(new LocalDate(2013, 1, 13).toDate());
		Stock s1 = UnitedFormatStock.readFromCsvFile("ibm", "./test_data/ibm.csv");
		assertEquals(true, stockFilter.isLiquidTest(s1));
		Stock s2 = UnitedFormatStock.readFromCsvFile("anse", "./test_data/anse.csv");
		assertEquals(false, stockFilter.isLiquidTest(s2));

		StockFilter stockFilter2 = new StockFilter(new LocalDate(2014, 2, 10).toDate());

		Stock s3 = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		assertEquals(true, stockFilter2.isLiquidTest(s3));

		Stock s4 = UnitedFormatStock.readFromUniteFormatFile("./test_data/spy.uf");
		assertEquals(true, stockFilter2.isLiquidTest(s4));

		Stock s5 = UnitedFormatStock.readFromUniteFormatFile("./test_data/aaae.uf");
		assertEquals(false, stockFilter2.isLiquidTest(s5));
	}

	public void testLast10Year() throws IOException {
		StockFilter stockFilter = new StockFilter(new LocalDate(2014, 2, 10).toDate());

		Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		ArrayList<Day> copyFromDays = aapl.getDays();

		MemoryStock smallappl = new MemoryStock("smallaapl");
		ArrayList<Day> days = smallappl.getDays();

		int indexOfDeletingTo = aapl.findDayIndex(new LocalDate(2007, 1, 1).toDate());

		for (int i = 0; i < indexOfDeletingTo; i++) {
			days.add(copyFromDays.get(i));
		}
		int indexOfDeletingFrom = aapl.findDayIndex(new LocalDate(2009, 1, 1).toDate());
		for (int i = indexOfDeletingFrom; i < copyFromDays.size(); i++) {
			days.add(copyFromDays.get(i));
		}
		assertEquals(false, stockFilter.isLiquidTest(smallappl));

	}
}
