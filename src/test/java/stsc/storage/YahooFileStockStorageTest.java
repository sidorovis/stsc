package stsc.storage;

import stsc.common.MarketDataContext;
import junit.framework.TestCase;

public class YahooFileStockStorageTest extends TestCase {

	public void testStockStorage() throws Exception {
		MarketDataContext marketDataContext = new MarketDataContext();
		marketDataContext.dataFolder = "./test_data/";
		marketDataContext.filteredDataFolder = "./test_data/";
		StockStorage stockStorage = new YahooFileStockStorage(marketDataContext);
		assertNotNull(stockStorage);
		assertNotNull(stockStorage.getStock("aaae"));
		assertNotNull(stockStorage.getStock("aapl"));
		assertNull(stockStorage.getStock("anse"));
		assertEquals(7430, stockStorage.getStock("aapl").getDays().size());
	}

	public void testLiqudityStorageReader() throws Exception {
		MarketDataContext marketDataContext = new MarketDataContext();
		marketDataContext.dataFolder = "./test_data/";
		marketDataContext.filteredDataFolder = "./test_data/";
		StockStorage stockStorage = new YahooFileStockStorage(marketDataContext);
		assertNotNull(stockStorage.getStock("aaae"));
		assertNotNull(stockStorage.getStock("aapl"));
		assertNull(stockStorage.getStock("noexistsstock"));
	}
}
