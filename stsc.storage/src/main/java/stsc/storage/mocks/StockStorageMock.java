package stsc.storage.mocks;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Sets;

import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.StockStorage;
import stsc.storage.ThreadSafeStockStorage;

public class StockStorageMock implements StockStorage {

	@Override
	public Optional<Stock> getStock(String name) {
		return Optional.empty();
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