package stsc.MarketDataDownloader;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.MarketDataContext;

/**
 * Download Market Data from Yahoo API.
 * 
 */
public final class MarketDataDownloader {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY,
				"./log4j2.xml");
	}

	private static Logger logger = LogManager.getLogger("MarketDataDownloader");

	MarketDataContext marketDataContext = new MarketDataContext();
	static int downloadThreadSize = 8;
	static int stockNameMinLength = 5;
	static int stockNameMaxLength = 5;

	void generateNextElement(char[] generatedText, int currentIndex, int size) {
		for (char c = 'a'; c <= 'z'; ++c) {
			generatedText[currentIndex] = c;
			if (currentIndex == size - 1) {
				String newTask = new String(generatedText);
				marketDataContext.addTask(newTask);
			} else {
				generateNextElement(generatedText, currentIndex + 1, size);
			}
		}
	}

	void generateTasks(int taskLength) {
		char[] generatedText = new char[taskLength];
		generateNextElement(generatedText, 0, taskLength);
	}

	private void readProperties() throws IOException {
		FileInputStream in = new FileInputStream("MarketDataDownloader.ini");

		Properties p = new Properties();
		p.load(in);
		in.close();

		downloadThreadSize = Integer.parseInt(p.getProperty("thread.amount"));
		stockNameMinLength = Integer.parseInt(p.getProperty("stock_name_min.size"));
		stockNameMaxLength = Integer.parseInt(p.getProperty("stock_name_max.size"));
	}

	MarketDataDownloader() throws InterruptedException, IOException {

		readProperties();

		DownloadThread downloadThread = new DownloadThread(marketDataContext);

		logger.trace("starting");

		for (int i = stockNameMinLength; i <= stockNameMaxLength; ++i)
			generateTasks(i);

		logger.trace("tasks size: {}", marketDataContext.taskQueueSize());

		List<Thread> threads = new ArrayList<Thread>();

		for (int i = 0; i < downloadThreadSize; ++i) {
			Thread newThread = new Thread(downloadThread);
			threads.add(newThread);
			newThread.start();
		}

		logger.info("calculating threads started ( {} )", downloadThreadSize);

		for (Thread thread : threads) {
			thread.join();
		}

		logger.trace("finishing");
	}

	public static void main(String[] args) {
		try {
			new MarketDataDownloader();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
