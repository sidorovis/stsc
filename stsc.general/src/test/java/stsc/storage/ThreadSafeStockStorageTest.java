package stsc.storage;

import java.io.IOException;

import stsc.stocks.MemoryStock;
import stsc.stocks.Stock;
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
