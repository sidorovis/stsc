package stsc.yahoo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.stocks.Stock;
import stsc.common.stocks.StockLock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.storage.ThreadSafeStockStorage;

public class YahooFileStockStorage extends ThreadSafeStockStorage implements StockReadThread.StockReceiver {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger("YahooFileStorage");

	private YahooSettings settings;
	private int readStockThreadSize = 4;
	private List<Thread> threads = new ArrayList<Thread>();

	public YahooFileStockStorage(YahooSettings settings) throws ClassNotFoundException, IOException {
		super();
		this.settings = settings;
		loadStocksFromFileSystem();
	}

	public YahooFileStockStorage() throws ClassNotFoundException, IOException {
		this("./data/", "./filtered_data/");
	}

	public static YahooFileStockStorage forData(String dataFolder) throws ClassNotFoundException, IOException {
		return new YahooFileStockStorage(dataFolder, "./filtered_data/");
	}

	public static YahooFileStockStorage forFilteredData(String dataFilterFolder) throws ClassNotFoundException, IOException {
		return new YahooFileStockStorage("./data/", dataFilterFolder);
	}

	public YahooFileStockStorage(String dataFolder, String filteredDataFolder) throws ClassNotFoundException, IOException {
		super();
		this.settings = new YahooSettings(dataFolder, filteredDataFolder);
		loadStocksFromFileSystem();
	}

	private void loadStocksFromFileSystem() throws ClassNotFoundException, IOException {
		logger.trace("created");
		loadFilteredDatafeed();
		logger.info("filtered datafeed header readed: {} stocks", settings.taskQueueSize());
		loadStocks();
		logger.info("stocks were loaded");
	}

	private void loadFilteredDatafeed() {
		UnitedFormatStock.loadStockList(settings.getFilteredDataFolder(), settings.getTaskQueue());
	}

	private void loadStocks() throws ClassNotFoundException, IOException {
		StockReadThread stockReadThread = new StockReadThread(settings, this);

		for (int i = 0; i < readStockThreadSize; ++i) {
			final Thread newThread = new Thread(stockReadThread);
			newThread.setName("YahooFileStockStorage Reading Thread - " + String.valueOf(i));
			threads.add(newThread);
			newThread.start();
		}
	}

	public void waitForLoad() throws InterruptedException {
		for (Thread thread : threads) {
			thread.join();
		}
	}

	@Override
	public void newStock(Stock newStock) {
		datafeed.put(newStock.getName(), new StockLock(newStock));
	}

	public Queue<String> getTasks() {
		return settings.getTaskQueue();
	}
}
