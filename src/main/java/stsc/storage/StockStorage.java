package stsc.storage;

import stsc.common.Stock;

public interface StockStorage {

	public abstract Stock getStock(String name);

	public abstract void updateStock(Stock stock);

}
