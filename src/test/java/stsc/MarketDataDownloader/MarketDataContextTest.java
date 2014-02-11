package stsc.MarketDataDownloader;

import java.io.IOException;

import junit.framework.TestCase;

public class MarketDataContextTest extends TestCase {
	public void testConstructionAndSave() throws IOException {
		MarketDataContext marketDataContext = new MarketDataContext();
		marketDataContext.dataFolder = "./test/";
		assertEquals(marketDataContext.getTask(), null);
		marketDataContext.addTask("a");
		assertEquals(1, marketDataContext.taskQueueSize());
		assertEquals(marketDataContext.getTask(), "a");
		assertEquals(marketDataContext.getTask(), null);

		assertEquals("./test/asd.bin",
				marketDataContext.generateBinaryFilePath("asd"));
		assertEquals("./filtered_data/asd.bin",
				marketDataContext.generateFilteredBinaryFilePath("asd"));
		assertEquals("./test/asd.csv",
				marketDataContext.generateFilePath("asd"));
		
		marketDataContext.dataFolder = "./test_data/";
		assertNotNull( marketDataContext.getStockFromFileSystem("aapl"));
		assertNull( marketDataContext.getStockFromFileSystem("a"));
	}

}
