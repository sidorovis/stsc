package stsc.testhelper;

import java.util.Set;

import com.google.common.collect.Sets;

import stsc.common.Stock;
import stsc.storage.StockStorage;

public class StockStorageHelper implements StockStorage {

	@Override
	public Stock getStock(String name) {
		return null;
	}

	@Override
	public void updateStock(Stock stock) {
	}

	@Override
	public Set<String> getStockNames() {
		return Sets.newHashSet(new String[] { "aapl", "goog", "epl" });
	}

}