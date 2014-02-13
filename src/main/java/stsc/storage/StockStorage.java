package stsc.storage;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stsc.common.MarketDataContext;
import stsc.common.Stock;

public class StockStorage {

	private static Logger logger = LogManager.getLogger("SharedStockStorage");

	private MarketDataContext marketDataContext;

	private HashMap<String, Stock> datafeed = new HashMap<String, Stock>();

	public StockStorage(MarketDataContext marketDataContext) throws ClassNotFoundException, IOException {
		this.marketDataContext = marketDataContext;
		logger.trace("created");
		loadFilteredDatafeed();
		logger.info("filtered datafeed header readed: {} stocks", marketDataContext.taskQueueSize());
		loadStocks();
		logger.info("stocks were loaded");
	}

	private void loadFilteredDatafeed() {
		File folder = new File(marketDataContext.filteredDataFolder);
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			String filename = file.getName();
			if (file.isFile() && filename.endsWith(".uf"))
				marketDataContext.addTask(filename.substring(0, filename.length() - 3));
		}
	}

	private void loadStocks() throws ClassNotFoundException, IOException {
		String task = marketDataContext.getTask();
		while (task != null) {
			Stock s = marketDataContext.getStockFromFileSystem(task);
			if (s != null)
				datafeed.put(s.getName(), s);
			task = marketDataContext.getTask();
		}
	}

	Stock getStock(String name) {
		return datafeed.get(name);
	}
}
