package stsc.liquiditator;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;

import stsc.common.Stock;
import junit.framework.TestCase;

public class StockFilterTest extends TestCase {
	public void testStockFilter() throws IOException, ParseException,
			ClassNotFoundException {

		Calendar cal = Calendar.getInstance();
		cal.set(2014, 0, 13);

		StockFilter stockFilter = new StockFilter(cal.getTime());
		Stock s1 = Stock.readFromCsvFile("ibm", "./test_data/ibm.csv");
		assertEquals(true, stockFilter.test(s1));
		Stock s2 = Stock.readFromCsvFile("anse", "./test_data/anse.csv");
		assertEquals(false, stockFilter.test(s2));

		cal.set(2014, 1, 10);
		StockFilter stockFilter2 = new StockFilter(cal.getTime());

		Stock s3 = Stock.readFromBinFile("./test_data/aapl.bin");
		assertEquals(true, stockFilter2.test(s3));

		Stock s4 = Stock.readFromBinFile("./test_data/spy.bin");
		assertEquals(true, stockFilter2.test(s4));
	
		Stock s5 = Stock.readFromBinFile("./test_data/aaaa.bin");
		assertEquals(false, stockFilter2.test(s5));
	}
}
