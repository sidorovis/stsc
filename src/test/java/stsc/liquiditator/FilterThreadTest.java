package stsc.liquiditator;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import stsc.common.MarketDataContext;
import junit.framework.TestCase;

public class FilterThreadTest extends TestCase {
	public void testFilterThread() throws IOException, InterruptedException {
		MarketDataContext marketDataContext = new MarketDataContext();
		marketDataContext.dataFolder = "./test_data/";
		marketDataContext.filteredDataFolder = "./test/";
		
		marketDataContext.addTask("aaoi");
		marketDataContext.addTask("aapl");
		marketDataContext.addTask("ibm");
		marketDataContext.addTask("spy");
		
		Calendar cal = Calendar.getInstance();
		cal.set(2014, 0, 13);
		
		FilterThread filterThread = new FilterThread( marketDataContext, cal.getTime());
		{
			Thread th = new Thread(filterThread);
			th.start();
			th.join();
		}
		assertEquals( false, new File( "./test/aaoi.uf").exists() );
		assertEquals( true, new File( "./test/aapl.uf").exists() );
		assertEquals( false, new File( "./test/ibm.uf").exists() );
		assertEquals( true, new File( "./test/spy.uf").exists() );
		new File( "./test/aapl.uf").delete();
		new File( "./test/spy.uf").delete();
	}
}
