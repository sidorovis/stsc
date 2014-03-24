package stsc.liquiditator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.XMLConfigurationFactory;

import stsc.common.MarketDataContext;

public class DownloadedStockFilter {

	static {
		System.setProperty(XMLConfigurationFactory.CONFIGURATION_FILE_PROPERTY, "./config/log4j2.xml");
	}

	static int processThreadSize = 8;
	private static Logger logger = LogManager.getLogger("DownloadedStockFilter");

	static MarketDataContext marketDataContext;

	private void collectDownloadedStockNames() {
		File folder = new File(marketDataContext.dataFolder);
		File[] listOfFiles = folder.listFiles();
		String extension = ".uf";
		for (File file : listOfFiles) {
			String filename = file.getName();
			if (file.isFile() && filename.endsWith(extension))
				marketDataContext.addTask(filename.substring(0, filename.length() - extension.length()));
		}
	}

	private void readProperties() throws IOException {
		FileInputStream in = new FileInputStream("./config/liquiditator.ini");

		Properties p = new Properties();
		p.load(in);
		in.close();

		processThreadSize = Integer.parseInt(p.getProperty("thread.amount"));
	}

	public DownloadedStockFilter() throws IOException, InterruptedException {
		readProperties();

		logger.trace("downloaded stock filter started");
		marketDataContext = new MarketDataContext();
		collectDownloadedStockNames();
		logger.trace("collected stock names to start filter process: {}", marketDataContext.taskQueueSize());

		List<Thread> threads = new ArrayList<Thread>();

		FilterThread filterThread = new FilterThread(marketDataContext);

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
			new DownloadedStockFilter();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
