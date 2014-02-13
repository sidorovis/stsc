package stsc.common;

import java.io.IOException;

import junit.framework.TestCase;

public class MarketDataContextTest extends TestCase {
	public void testConstructionAndSave() throws IOException {
		MarketDataContext marketDataContext = new MarketDataContext();
		marketDataContext.dataFolder = "./test/";
		marketDataContext.filteredDataFolder = "./test/";
		assertEquals(marketDataContext.getTask(), null);
		marketDataContext.addTask("a");
		assertEquals(1, marketDataContext.taskQueueSize());
		assertEquals(marketDataContext.getTask(), "a");
		assertEquals(marketDataContext.getTask(), null);

		assertEquals("./test/asd.csv", marketDataContext.generateFilePath("asd"));
		assertEquals("./test/asd.uf",marketDataContext.generateUniteFormatPath("asd"));
		
		marketDataContext.dataFolder = "./test_data/";
		assertNotNull(marketDataContext.getStockFromFileSystem("aapl"));
		assertNull(marketDataContext.getStockFromFileSystem("a"));
	}

}
