package stsc.storage;

import stsc.common.StockInterface;

public class StockLock {
	StockInterface stock;

	public StockLock(StockInterface stock) {
		this.stock = stock;
	}

	public synchronized void updateStock(StockInterface stock) {
		this.stock = stock;
	}

	public synchronized StockInterface getStock() {
		return stock;
	}
}