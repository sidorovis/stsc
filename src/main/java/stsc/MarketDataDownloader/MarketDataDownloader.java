package stsc.MarketDataDownloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

/**
 * Download Market Data from Yahoo API.
 * 
 */
public final class MarketDataDownloader {

	static {
        System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "D:/dev/java/MarketDataDownloader/log4j2.xml");
    }
	
	private static Logger logger = LogManager.getLogger("MarketDataDownloader");

	MarketDataContext marketDataContext = new MarketDataContext();
	static final int downloadThreadSize = 8;
	static final int stockNameMaxLength = 1;

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

	MarketDataDownloader() throws InterruptedException, IOException {
		DownloadThread downloadThread = new DownloadThread(marketDataContext);
		
		logger.trace("starting");

		for (int i = 1; i <= stockNameMaxLength; ++i)
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
