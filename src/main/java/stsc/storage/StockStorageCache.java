package stsc.storage;

import java.util.HashMap;
import stsc.common.StockInterface;

public class StockStorageCache implements StockStorage {

	private HashMap<String, StockInterface> stocks = new HashMap<>();
	
	@Override
	public StockInterface getStock(String name) {
		return stocks.get(name);
	}

	@Override
	public void updateStock(StockInterface stock) {
		String stockName = stock.getName();
		stocks.put(stockName, stock);
	}
	
}
