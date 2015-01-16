package stsc.common.storage;

import java.util.Optional;
import java.util.Set;

import stsc.common.stocks.Stock;

public interface StockStorage {

	public abstract Optional<Stock> getStock(String name);

	public abstract void updateStock(Stock stock);

	public abstract Set<String> getStockNames();
}
