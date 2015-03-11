package stsc.yahoo.downloader;

import java.util.ArrayList;
import java.util.List;

import stsc.common.service.statistics.DownloaderLogger;
import stsc.common.stocks.UnitedFormatStock;
import stsc.yahoo.StringUtils;
import stsc.yahoo.YahooSettings;

public final class YahooDownloadCourutine {

	private final DownloaderLogger logger;

	private final DownloadYahooStockThread downloadThread;

	private final int downloadThreadSize;
	private final boolean downloadExisted;
	private final YahooSettings settings;
	private final boolean downloadByPattern;
	private final String startPattern;
	private final String endPattern;
	private final int stockNameMinLength;
	private final int stockNameMaxLength;

	private volatile boolean stopped = false;

	public YahooDownloadCourutine(DownloaderLogger logger, boolean downloadExisted, YahooSettings settings, boolean downloadByPattern,
			String startPattern, String endPattern, int stockNameMinLength, int stockNameMaxLength, int downloadThreadSize) {
		this.logger = logger;
		this.downloadExisted = downloadExisted;
		this.settings = settings;
		this.downloadByPattern = downloadByPattern;
		this.startPattern = startPattern;
		this.endPattern = endPattern;
		this.stockNameMinLength = stockNameMinLength;
		this.stockNameMaxLength = stockNameMaxLength;
		this.downloadThreadSize = downloadThreadSize;

		downloadThread = new DownloadYahooStockThread(logger, settings);
	}

	public void start() throws InterruptedException {
		logger.log().trace("starting");
		if (downloadExisted) {
			UnitedFormatStock.loadStockList(settings.getDataFolder(), settings.getTaskQueue());
		} else {
			if (downloadByPattern) {
				String pattern = startPattern;
				while (StringUtils.comparePatterns(pattern, endPattern) <= 0) {
					settings.addTask(pattern);
					pattern = StringUtils.nextPermutation(pattern);
				}
			} else {
				for (int i = stockNameMinLength; i <= stockNameMaxLength; ++i)
					generateTasks(i);
			}
		}
		if (stopped) {
			return;
		}
		logger.log().trace("tasks size: {}", settings.taskQueueSize());
		final List<Thread> threads = new ArrayList<Thread>();
		for (int i = 0; i < downloadThreadSize; ++i) {
			Thread newThread = new Thread(downloadThread);
			threads.add(newThread);
			newThread.start();
		}

		logger.log().info("calculating threads started ( {} )", downloadThreadSize);
		for (Thread thread : threads) {
			thread.join();
		}

		logger.log().trace("finishing");
	}

	private void generateNextElement(char[] generatedText, int currentIndex, int size) {
		for (char c = 'a'; c <= 'z'; ++c) {
			generatedText[currentIndex] = c;
			if (currentIndex == size - 1) {
				String newTask = new String(generatedText);
				settings.addTask(newTask);
			} else {
				generateNextElement(generatedText, currentIndex + 1, size);
			}
		}
	}

	private void generateTasks(int taskLength) {
		char[] generatedText = new char[taskLength];
		generateNextElement(generatedText, 0, taskLength);
	}

	public void stop() throws Exception {
		stopped = true;
		downloadThread.stop();
		logger.log().trace("stop command was processed");
	}

}
