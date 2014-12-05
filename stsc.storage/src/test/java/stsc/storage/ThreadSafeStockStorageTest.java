package stsc.storage;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.stocks.MemoryStock;
import stsc.common.stocks.Stock;
import stsc.common.storage.StockStorage;

public class ThreadSafeStockStorageTest {
	
	@Test
	public void testThreadSafeMemoryStoskStorage() throws ClassNotFoundException, IOException, InterruptedException {
		StockStorage stockStorage = new ThreadSafeStockStorage();
		Stock stock = new MemoryStock("aapl");
		stockStorage.updateStock(stock);
		
		Assert.assertNull(stockStorage.getStock("nostock"));
		Assert.assertNotNull(stockStorage.getStock("aapl"));
	}

}
