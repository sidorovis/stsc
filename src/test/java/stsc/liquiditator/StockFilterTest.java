package stsc.liquiditator;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import org.joda.time.DateTime;

import stsc.common.Day;
import stsc.common.DayComparator;
import stsc.common.InMemoryStock;
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

	public void testLast10Year() throws IOException {
		Calendar cal = Calendar.getInstance();
		cal.set(2014, 1, 10);
		StockFilter stockFilter = new StockFilter(cal.getTime());
		
		StockInterface aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");

		InMemoryStock smallappl = new InMemoryStock("smallaapl");
		ArrayList<Day> days = smallappl.getDays();
		
		ArrayList<Day> copyFromDays = aapl.getDays();
		int indexOfDeletingTo = -Collections.binarySearch(copyFromDays, new Day( new DateTime(2007,1,1,0,0).toDate()), new DayComparator() );
		for (int i = 0 ; i < indexOfDeletingTo; i++ ) {
			days.add( copyFromDays.get(i ));
		}
		int indexOfDeletingFrom = -Collections.binarySearch(copyFromDays, new Day( new DateTime(2009,1,1,0,0).toDate()), new DayComparator() );
		for (int i = indexOfDeletingFrom ; i < copyFromDays.size(); i++ ) {
			days.add( copyFromDays.get(i ));
		}
		assertEquals(false, stockFilter.test(smallappl));
	
	}
}
