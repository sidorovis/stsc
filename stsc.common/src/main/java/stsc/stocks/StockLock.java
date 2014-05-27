package stsc.stocks;

public final class StockLock {
	Stock stock;

	public StockLock(Stock stock) {
		this.stock = stock;
	}

	public synchronized void updateStock(Stock stock) {
		this.stock = stock;
	}

	public synchronized Stock getStock() {
		return stock;
	}
}