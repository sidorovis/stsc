package stsc.storage;

import java.io.IOException;
import java.util.Set;
import java.util.TimeZone;

import com.google.common.collect.Sets;

import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.StockStorage;

public class StockStorageFactory {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	private StockStorageFactory() {
		// use static method to create StockStorage
	}

	public static StockStorage createStockStorage(final String stockName) throws ClassNotFoundException, IOException, InterruptedException {
		return createStockStorage(Sets.newHashSet(new String[] { stockName }), "./filtered_data/");
	}

	public static StockStorage createStockStorage(String stockName, String filterDataFolderPath) throws ClassNotFoundException,
			IOException, InterruptedException {
		return createStockStorage(Sets.newHashSet(new String[] { stockName }), filterDataFolderPath);
	}

	public static StockStorage createStockStorage(Set<String> stockNames, String filterDataFolderPath) throws ClassNotFoundException,
			IOException, InterruptedException {
		StockStorage stockStorage = new ThreadSafeStockStorage();
		for (String name : stockNames) {
			final String path = filterDataFolderPath + name + UnitedFormatStock.EXTENSION;
			stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile(path));
		}
		return stockStorage;
	}
}
