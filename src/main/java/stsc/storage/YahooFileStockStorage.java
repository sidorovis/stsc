package stsc.storage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.MarketDataContext;
import stsc.common.StockInterface;

public class YahooFileStockStorage implements StockStorage {

	private class StockReadThread implements Runnable {

		private MarketDataContext marketDataContext;

		public StockReadThread(MarketDataContext marketDataContext) {
			this.marketDataContext = marketDataContext;
		}

		@Override
		public void run() {
			String task = marketDataContext.getTask();
			while (task != null) {
				StockInterface s = marketDataContext.getStockFromFileSystem(task);
				if (s != null)
					datafeed.put(s.getName(), new StockLock(s));
				task = marketDataContext.getTask();
			}
		}
	};

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger("SharedStockStorage");

	private MarketDataContext marketDataContext;
	private int readStockThreadSize = 4;

	private ConcurrentHashMap<String, StockLock> datafeed = new ConcurrentHashMap<String, StockLock>();

	public YahooFileStockStorage(MarketDataContext marketDataContext) throws ClassNotFoundException, IOException,
			InterruptedException {
		this.marketDataContext = marketDataContext;
		loadStocksFromFileSystem();
	}

	public static YahooFileStockStorage newStockStorageFromFilesystem() throws ClassNotFoundException, IOException,
			InterruptedException {
		return new YahooFileStockStorage(true);
	}

	public static YahooFileStockStorage newInMemoryStockStorage() throws ClassNotFoundException, IOException,
			InterruptedException {
		return new YahooFileStockStorage(false);
	}

	private YahooFileStockStorage(boolean readFromFileSystem) throws ClassNotFoundException, IOException,
			InterruptedException {
		this.marketDataContext = new MarketDataContext();
		if (readFromFileSystem)
			loadStocksFromFileSystem();
	}

	private void loadStocksFromFileSystem() throws ClassNotFoundException, IOException, InterruptedException {
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

	private void loadStocks() throws ClassNotFoundException, IOException, InterruptedException {
		StockReadThread stockReadThread = new StockReadThread(marketDataContext);
		List<Thread> threads = new ArrayList<Thread>();

		for (int i = 0; i < readStockThreadSize; ++i) {
			Thread newThread = new Thread(stockReadThread);
			threads.add(newThread);
			newThread.start();
		}

		for (Thread thread : threads) {
			thread.join();
		}
	}

	@Override
	public StockInterface getStock(String name) {
		StockLock stockLock = datafeed.get(name);
		if (stockLock == null)
			return null;
		StockInterface stock = stockLock.getStock();
		return stock;
	}

	@Override
	public void updateStock(StockInterface stock) {
		String stockName = stock.getName();
		StockLock stockLock = datafeed.get(stockName);
		if (stockLock == null)
			datafeed.put(stockName, new StockLock(stock));
		else
			stockLock.updateStock(stock);
	}
}
