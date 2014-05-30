package stsc.yahoo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.stocks.Stock;
import stsc.common.stocks.StockLock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.general.storage.ThreadSafeStockStorage;

public class YahooFileStockStorage extends ThreadSafeStockStorage implements StockReadThread.StockReceiver {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger("YahooFileStorage");

	private YahooSettings settings;
	private int readStockThreadSize = 4;

	public YahooFileStockStorage(YahooSettings settings) throws ClassNotFoundException, IOException, InterruptedException {
		super();
		this.settings = settings;
		loadStocksFromFileSystem();
	}

	public YahooFileStockStorage() throws ClassNotFoundException, IOException, InterruptedException {
		this("./data/", "./filtered_data/");
	}

	public static YahooFileStockStorage forData(String dataFolder) throws ClassNotFoundException, IOException, InterruptedException {
		return new YahooFileStockStorage(dataFolder, "./filtered_data/");
	}

	public static YahooFileStockStorage forFilteredData(String dataFilterFolder) throws ClassNotFoundException, IOException, InterruptedException {
		return new YahooFileStockStorage("./data/", dataFilterFolder);
	}

	public YahooFileStockStorage(String dataFolder, String filteredDataFolder) throws ClassNotFoundException, IOException, InterruptedException {
		super();
		this.settings = new YahooSettings(dataFolder, filteredDataFolder);
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
		UnitedFormatStock.loadStockList(settings.getFilteredDataFolder(), settings.getTaskQueue());
	}

	private void loadStocks() throws ClassNotFoundException, IOException, InterruptedException {
		StockReadThread stockReadThread = new StockReadThread(settings, this);
		List<Thread> threads = new ArrayList<Thread>();

		for (int i = 0; i < readStockThreadSize; ++i) {
			final Thread newThread = new Thread(stockReadThread);
			newThread.setName("YahooFileStockStorage Reading Thread - " + String.valueOf(i));
			threads.add(newThread);
			newThread.start();
		}

		for (Thread thread : threads) {
			thread.join();
		}
	}

	@Override
	public void newStock(Stock newStock) {
		datafeed.put(newStock.getName(), new StockLock(newStock));
	}
}
