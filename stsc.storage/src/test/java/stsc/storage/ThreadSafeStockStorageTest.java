package stsc.storage;

import java.io.IOException;

import stsc.common.stocks.MemoryStock;
import stsc.common.stocks.Stock;
import stsc.common.storage.StockStorage;
import stsc.storage.ThreadSafeStockStorage;
import junit.framework.TestCase;

public class ThreadSafeStockStorageTest extends TestCase {
	public void testThreadSafeMemoryStoskStorage() throws ClassNotFoundException, IOException, InterruptedException {
		StockStorage stockStorage = new ThreadSafeStockStorage();
		Stock stock = new MemoryStock("aapl");
		stockStorage.updateStock(stock);
		
		assertNull(stockStorage.getStock("nostock"));
		assertNotNull(stockStorage.getStock("aapl"));
	}

}
