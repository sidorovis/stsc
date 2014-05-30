package stsc.general.testhelper;

import java.io.IOException;
import java.util.Set;

import com.google.common.collect.Sets;

import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.StockStorage;
import stsc.general.storage.ThreadSafeStockStorage;

public class TestStockStorageHelper implements StockStorage {

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

	static StockStorage stockStorage = null;

	public static StockStorage getStockStorage() {
		if (stockStorage == null) {
			stockStorage = new ThreadSafeStockStorage();
			try {
				stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf"));
				stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/adm.uf"));
				stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/spy.uf"));
			} catch (IOException e) {
			}
		}
		return stockStorage;
	}
}