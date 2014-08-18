package stsc.distributed.hadoop;

import java.io.IOException;

import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.StockStorage;
import stsc.storage.ThreadSafeStockStorage;
import stsc.yahoo.YahooFileStockStorage;

class StockStorageSingleton {

	private static StockStorage stockStorage = null;

	static StockStorage getInstance(final String dataFolder, final String filteredDataFolder) throws ClassNotFoundException, IOException,
			InterruptedException {
		if (stockStorage == null) {
			stockStorage = new YahooFileStockStorage(dataFolder, filteredDataFolder);
		}
		return stockStorage;
	}

	static StockStorage getInstance() {
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