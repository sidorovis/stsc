package stsc.yahoo;

import java.io.IOException;

import stsc.common.storage.StockStorage;
import stsc.yahoo.YahooFileStockStorage;
import junit.framework.TestCase;

public class YahooFileStockStorageTest extends TestCase {

	private static StockStorage stockStorage = null;

	private static synchronized StockStorage getStockStorage() throws ClassNotFoundException, IOException, InterruptedException {
		if (stockStorage == null) {
			YahooFileStockStorage ss = new YahooFileStockStorage("./test_data/", "./test_data/");
			ss.waitForLoad();
			stockStorage = ss;
		}
		return stockStorage;
	}

	public void testStockStorage() throws Exception {
		final StockStorage stockStorage = getStockStorage();
		assertNotNull(stockStorage);
		assertNotNull(stockStorage.getStock("aaae"));
		assertNotNull(stockStorage.getStock("aapl"));
		assertFalse(stockStorage.getStock("anse").isPresent());
		assertEquals(7430, stockStorage.getStock("aapl").get().getDays().size());
	}

	public void testLiqudityStorageReader() throws Exception {
		final StockStorage stockStorage = getStockStorage();
		assertNotNull(stockStorage);
		assertNotNull(stockStorage.getStock("aaae"));
		assertNotNull(stockStorage.getStock("aapl"));
		assertFalse(stockStorage.getStock("noexistsstock").isPresent());
	}
}
