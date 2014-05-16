package stsc.yahoo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.Stock;
import stsc.common.StockLock;
import stsc.common.UnitedFormatStock;
import stsc.storage.ThreadSafeStockStorage;

public class YahooFileStockStorage extends ThreadSafeStockStorage {

	private class StockReadThread implements Runnable {

		private YahooFilesystemDatafeedSettings settings;

		public StockReadThread(YahooFilesystemDatafeedSettings settings) {
			this.settings = settings;
		}

		@Override
		public void run() {
			String task = settings.getTask();
			while (task != null) {
				Stock s = settings.getStockFromFileSystem(task);
				if (s != null)
					datafeed.put(s.getName(), new StockLock(s));
				task = settings.getTask();
			}
		}
	};

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger("YahooFileStorage");

	private YahooFilesystemDatafeedSettings settings;
	private int readStockThreadSize = 4;

	public YahooFileStockStorage(YahooFilesystemDatafeedSettings settings) throws ClassNotFoundException, IOException,
			InterruptedException {
		super();
		this.settings = settings;
		loadStocksFromFileSystem();
	}

	public YahooFileStockStorage() throws ClassNotFoundException, IOException, InterruptedException {
		this("./data/", "./filtered_data/");
	}

	public static YahooFileStockStorage forData(String dataFolder) throws ClassNotFoundException, IOException,
			InterruptedException {
		return new YahooFileStockStorage(dataFolder, "./filtered_data/");
	}

	public static YahooFileStockStorage forFilteredData(String dataFilterFolder) throws ClassNotFoundException,
			IOException, InterruptedException {
		return new YahooFileStockStorage("./data/", dataFilterFolder);
	}

	public YahooFileStockStorage(String dataFolder, String filteredDataFolder) throws ClassNotFoundException,
			IOException, InterruptedException {
		super();
		this.settings = new YahooFilesystemDatafeedSettings(dataFolder, filteredDataFolder);
		loadStocksFromFileSystem();
	}

	private void loadStocksFromFileSystem() throws ClassNotFoundException, IOException, InterruptedException {
		logger.trace("created");
		loadFilteredDatafeed();
		logger.info("filtered datafeed header readed: {} stocks", settings.taskQueueSize());
		loadStocks();
		logger.info("stocks were loaded");
	}

	private void loadFilteredDatafeed() {
		UnitedFormatStock.loadStockList(settings.getFilteredDataFolder(), settings.taskQueue);
		File folder = new File(settings.getFilteredDataFolder());
		File[] listOfFiles = folder.listFiles();
		for (File file : listOfFiles) {
			String filename = file.getName();
			if (file.isFile() && filename.endsWith(".uf"))
				settings.addTask(filename.substring(0, filename.length() - 3));
		}
	}

	private void loadStocks() throws ClassNotFoundException, IOException, InterruptedException {
		StockReadThread stockReadThread = new StockReadThread(settings);
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
}
