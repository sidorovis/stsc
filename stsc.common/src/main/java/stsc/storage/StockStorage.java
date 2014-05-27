package stsc.storage;

import java.util.Set;

import stsc.stocks.Stock;

public interface StockStorage {

	public abstract Stock getStock(String name);

	public abstract void updateStock(Stock stock);

	public abstract Set<String> getStockNames();
}
