package stsc.storage;

import java.io.IOException;

import stsc.common.InMemoryStock;
import stsc.common.StockInterface;
import junit.framework.TestCase;

public class InMemoryStockStorageTest extends TestCase {
	public void testInMemoryStoskStorage() throws ClassNotFoundException, IOException, InterruptedException {
		StockStorage stockStorage = new InMemoryStockStorage();
		StockInterface stock = new InMemoryStock("aapl");
		stockStorage.updateStock(stock);
	}

}
