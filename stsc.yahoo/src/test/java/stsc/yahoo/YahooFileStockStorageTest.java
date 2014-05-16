package stsc.yahoo;

import stsc.storage.StockStorage;
import stsc.yahoo.YahooFileStockStorage;
import junit.framework.TestCase;

public class YahooFileStockStorageTest extends TestCase {

	public void testStockStorage() throws Exception {
		StockStorage stockStorage = new YahooFileStockStorage("./test_data/", "./test_data/");
		assertNotNull(stockStorage);
		assertNotNull(stockStorage.getStock("aaae"));
		assertNotNull(stockStorage.getStock("aapl"));
		assertNull(stockStorage.getStock("anse"));
		assertEquals(7430, stockStorage.getStock("aapl").getDays().size());
	}

	public void testLiqudityStorageReader() throws Exception {
		StockStorage stockStorage = new YahooFileStockStorage("./test_data/", "./test_data/");
		assertNotNull(stockStorage.getStock("aaae"));
		assertNotNull(stockStorage.getStock("aapl"));
		assertNull(stockStorage.getStock("noexistsstock"));
	}
}
