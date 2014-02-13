package stsc.liquiditator;

import java.io.File;
import java.io.IOException;

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
		
		FilterThread filterThread = new FilterThread( marketDataContext );
		{
			Thread th = new Thread(filterThread);
			th.start();
			th.join();
		}
		assertEquals( false, new File( "./test/aaoi.bin ").exists() );
		assertEquals( true, new File( "./test/aapl.bin ").exists() );
		assertEquals( false, new File( "./test/ibm.bin ").exists() );
		assertEquals( true, new File( "./test/spy.bin ").exists() );
		new File( "./test/aapl.bin ").delete();
		new File( "./test/spy.bin ").delete();
	}
}
