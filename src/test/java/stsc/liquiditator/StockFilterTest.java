package stsc.liquiditator;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

import stsc.common.UnitedFormatStock;
import stsc.common.StockInterface;
import junit.framework.TestCase;

public class StockFilterTest extends TestCase {
	public void testStockFilter() throws IOException, ParseException, ClassNotFoundException {

		Calendar cal = Calendar.getInstance();
		cal.set(2014, 0, 13);

		StockFilter stockFilter = new StockFilter(cal.getTime());
		StockInterface s1 = UnitedFormatStock.readFromCsvFile("ibm", "./test_data/ibm.csv");
		assertEquals(true, stockFilter.test(s1));
		StockInterface s2 = UnitedFormatStock.readFromCsvFile("anse", "./test_data/anse.csv");
		assertEquals(false, stockFilter.test(s2));

		cal.set(2014, 1, 10);
		StockFilter stockFilter2 = new StockFilter(cal.getTime());

		StockInterface s3 = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		assertEquals(true, stockFilter2.test(s3));

		StockInterface s4 = UnitedFormatStock.readFromUniteFormatFile("./test_data/spy.uf");
		assertEquals(true, stockFilter2.test(s4));

		StockInterface s5 = UnitedFormatStock.readFromUniteFormatFile("./test_data/aaaa.uf");
		assertEquals(false, stockFilter2.test(s5));
	}
}
