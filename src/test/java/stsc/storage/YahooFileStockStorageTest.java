package stsc.storage;

import java.io.File;
import stsc.common.MarketDataContext;
import junit.framework.TestCase;

public class YahooFileStockStorageTest extends TestCase {

	public void testStockStorage() throws Exception {
		MarketDataContext marketDataContext = new MarketDataContext();
		marketDataContext.filteredDataFolder = "./test_data/";
		StockStorage stockStorage = new YahooFileStockStorage(marketDataContext);
		assertNotNull(stockStorage);
		assertNotNull(stockStorage.getStock("aaaa"));
		assertNotNull(stockStorage.getStock("aapl"));
		assertNull(stockStorage.getStock("anse"));
		assertEquals(7428, stockStorage.getStock("aapl").getDays().size());
	}

	public void testLiqudityStorageReader() throws Exception {
		if (new File("./filtered_data/").exists()) {
			MarketDataContext marketDataContext = new MarketDataContext();
			StockStorage stockStorage = new YahooFileStockStorage(marketDataContext);
			if (new File("./filtered_data/aa.uf").exists())
				assertNotNull(stockStorage.getStock("aa"));
			if (new File("./filtered_data/aapl.uf").exists())
				assertNotNull(stockStorage.getStock("aapl"));
		}
	}
}
