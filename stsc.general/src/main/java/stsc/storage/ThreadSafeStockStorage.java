package stsc.storage;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import stsc.common.Stock;
import stsc.common.StockLock;

public class ThreadSafeStockStorage implements StockStorage {
	protected ConcurrentHashMap<String, StockLock> datafeed = new ConcurrentHashMap<String, StockLock>();

	public ThreadSafeStockStorage() {
		super();
	}

	@Override
	public Stock getStock(String name) {
		StockLock stockLock = datafeed.get(name);
		if (stockLock == null)
			return null;
		Stock stock = stockLock.getStock();
		return stock;
	}

	@Override
	public void updateStock(Stock stock) {
		String stockName = stock.getName();
		StockLock stockLock = datafeed.get(stockName);
		if (stockLock == null)
			datafeed.put(stockName, new StockLock(stock));
		else
			stockLock.updateStock(stock);
	}

	@Override
	public Set<String> getStockNames() {
		return datafeed.keySet();
	}
}