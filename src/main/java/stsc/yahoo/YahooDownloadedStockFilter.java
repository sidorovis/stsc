package stsc.yahoo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.UnitedFormatStock;

public class YahooDownloadedStockFilter {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
	}

	static int processThreadSize = 8;
	private static Logger logger = LogManager.getLogger("DownloadedStockFilter");

	static YahooFilesystemDatafeedSettings settings;

	private void readProperties() throws IOException {
		FileInputStream in = new FileInputStream("./config/liquiditator.ini");

		Properties p = new Properties();
		p.load(in);
		in.close();

		processThreadSize = Integer.parseInt(p.getProperty("thread.amount"));
	}

	public YahooDownloadedStockFilter() throws IOException, InterruptedException {
		readProperties();

		logger.trace("downloaded stock filter started");
		settings = new YahooFilesystemDatafeedSettings();
		UnitedFormatStock.loadStockList(settings.getDataFolder(), settings.taskQueue);
		logger.trace("collected stock names to start filter process: {}", settings.taskQueueSize());

		List<Thread> threads = new ArrayList<Thread>();

		YahooFilterThread filterThread = new YahooFilterThread(settings);

		for (int i = 0; i < processThreadSize; ++i) {
			Thread newThread = new Thread(filterThread);
			threads.add(newThread);
			newThread.start();
		}

		logger.info("calculating threads started ( {} )", processThreadSize);

		for (Thread thread : threads) {
			thread.join();
		}

		logger.trace("downloaded stock filter finished");
	}

	public static void main(String[] args) {
		try {
			new YahooDownloadedStockFilter();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
