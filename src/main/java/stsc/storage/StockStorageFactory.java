package stsc.storage;

import java.io.IOException;
import java.util.Set;

import com.google.common.collect.Sets;

import stsc.common.MarketDataContext;
import stsc.common.UnitedFormatStock;

public class StockStorageFactory {

	private StockStorageFactory() {
		// use static method to create StockStorage
	}

	public static StockStorage createStockStorage(final String stockName) throws ClassNotFoundException, IOException,
			InterruptedException {
		return createStockStorage(Sets.newHashSet(new String[] { stockName }), "./filtered_data/");
	}

	public static StockStorage createStockStorage(String stockName, String filterDataFolderPath)
			throws ClassNotFoundException, IOException, InterruptedException {
		return createStockStorage(Sets.newHashSet(new String[] { stockName }), filterDataFolderPath);
	}

	public static StockStorage createStockStorage(Set<String> stockNames, String filterDataFolderPath)
			throws ClassNotFoundException, IOException, InterruptedException {
		final int AMOUNT_TO_MULTI_THREAD_LOAD = 100;
		if (stockNames.size() > AMOUNT_TO_MULTI_THREAD_LOAD) {
			final MarketDataContext context = new MarketDataContext();
			context.filteredDataFolder = filterDataFolderPath;
			return new YahooFileStockStorage(context);
		} else {
			StockStorage stockStorage = new ThreadSafeStockStorage();
			for (String name : stockNames) {
				final String path = filterDataFolderPath + name + ".uf";
				stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile(path));
			}
			return stockStorage;
		}
	}
}
