package stsc.storage;

import java.io.IOException;

import stsc.common.MemoryStock;
import stsc.common.Stock;
import junit.framework.TestCase;

public class InMemoryStockStorageTest extends TestCase {
	public void testInMemoryStoskStorage() throws ClassNotFoundException, IOException, InterruptedException {
		StockStorage stockStorage = new InMemoryStockStorage();
		Stock stock = new MemoryStock("aapl");
		stockStorage.updateStock(stock);
		
		assertNull(stockStorage.getStock("nostock"));
		assertNotNull(stockStorage.getStock("aapl"));
	}

}
