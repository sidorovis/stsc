package stsc.storage;

import stsc.common.StockInterface;

public interface StockStorage {

	public abstract StockInterface getStock(String name);

	public abstract void updateStock(StockInterface stock);

}
