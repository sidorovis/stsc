package stsc.storage;

import stsc.common.MarketDataContext;
import junit.framework.TestCase;

public class StockStorageTest extends TestCase {

	public void testStockStorage() throws Exception {
		MarketDataContext marketDataContext = new MarketDataContext();
		marketDataContext.filteredDataFolder = "./test_data/";
		StockStorage stockStorage = new StockStorage(marketDataContext);
		assertNotNull(stockStorage);
		assertNotNull(stockStorage.getStock("aaaa"));
		assertNotNull(stockStorage.getStock("aapl"));
		assertNull(stockStorage.getStock("anse"));
	}
}
