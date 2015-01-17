package stsc.yahoo;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.storage.StockStorage;

public class YahooFileStockStorageTest {

	private static StockStorage stockStorage = null;

	private static synchronized StockStorage getStockStorage() throws ClassNotFoundException, IOException, InterruptedException {
		if (stockStorage == null) {
			YahooFileStockStorage ss = new YahooFileStockStorage("./test_data/", "./test_data/");
			ss.waitForLoad();
			stockStorage = ss;
		}
		return stockStorage;
	}

	@Test
	public void testStockStorage() throws Exception {
		final StockStorage stockStorage = getStockStorage();
		Assert.assertNotNull(stockStorage);
		Assert.assertNotNull(stockStorage.getStock("aaae"));
		Assert.assertNotNull(stockStorage.getStock("aapl"));
		Assert.assertFalse(stockStorage.getStock("anse").isPresent());
		Assert.assertEquals(7430, stockStorage.getStock("aapl").get().getDays().size());
	}

	@Test
	public void testLiqudityStorageReader() throws Exception {
		final StockStorage stockStorage = getStockStorage();
		Assert.assertNotNull(stockStorage);
		Assert.assertNotNull(stockStorage.getStock("aaae"));
		Assert.assertNotNull(stockStorage.getStock("aapl"));
		Assert.assertFalse(stockStorage.getStock("noexistsstock").isPresent());
	}
}
