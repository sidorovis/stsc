package stsc.storage;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import stsc.common.stocks.Stock;
import stsc.common.stocks.StockLock;
import stsc.common.storage.StockStorage;

public class ThreadSafeStockStorage implements StockStorage {
	protected ConcurrentHashMap<String, StockLock> datafeed = new ConcurrentHashMap<String, StockLock>();

	public ThreadSafeStockStorage() {
		super();
	}

	@Override
	public Optional<Stock> getStock(String name) {
		StockLock stockLock = datafeed.get(name);
		if (stockLock == null)
			return Optional.empty();
		Stock stock = stockLock.getStock();
		return Optional.of(stock);
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