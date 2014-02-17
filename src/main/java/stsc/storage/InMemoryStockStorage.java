package stsc.storage;

import java.util.concurrent.ConcurrentHashMap;

import stsc.common.StockInterface;

public class InMemoryStockStorage implements StockStorage{

	protected ConcurrentHashMap<String, StockLock> datafeed = new ConcurrentHashMap<String, StockLock>();

	public InMemoryStockStorage() {
		super();
	}

	@Override
	public StockInterface getStock(String name) {
		StockLock stockLock = datafeed.get(name);
		if (stockLock == null)
			return null;
		StockInterface stock = stockLock.getStock();
		return stock;
	}

	@Override
	public void updateStock(StockInterface stock) {
		String stockName = stock.getName();
		StockLock stockLock = datafeed.get(stockName);
		if (stockLock == null)
			datafeed.put(stockName, new StockLock(stock));
		else
			stockLock.updateStock(stock);
	}

}