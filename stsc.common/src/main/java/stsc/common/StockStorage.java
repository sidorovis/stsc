package stsc.common;

import java.util.Set;

public interface StockStorage {

	public abstract Stock getStock(String name);

	public abstract void updateStock(Stock stock);

	public abstract Set<String> getStockNames();
}
