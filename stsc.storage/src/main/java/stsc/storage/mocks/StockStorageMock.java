package stsc.storage.mocks;

import java.io.IOException;
import java.util.Set;

import com.google.common.collect.Sets;

import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.StockStorage;
import stsc.storage.ThreadSafeStockStorage;

public class StockStorageMock implements StockStorage {

	@Override
	public Stock getStock(String name) {
		return null;
	}

	@Override
	public void updateStock(Stock stock) {
	}

	@Override
	public Set<String> getStockNames() {
		return Sets.newHashSet(new String[] { "aapl", "adm", "spy" });
	}

	private static StockStorage stockStorage = null;

	public synchronized static StockStorage getStockStorage() {
		if (stockStorage == null) {
			stockStorage = new ThreadSafeStockStorage();
			try {
				stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf"));
				stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/adm.uf"));
				stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/spy.uf"));
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
		return stockStorage;
	}
}