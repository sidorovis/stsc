package stsc.yahoo.downloader;

import java.io.File;
import java.util.Optional;

import stsc.common.service.statistics.StatisticType;
import stsc.common.service.statistics.DownloaderLogger;
import stsc.common.stocks.UnitedFormatStock;
import stsc.yahoo.YahooSettings;
import stsc.yahoo.YahooUtils;
import stsc.yahoo.liquiditator.StockFilter;

class DownloadYahooStockThread implements Runnable {

	private static final int printEach = 100;

	private final YahooSettings settings;
	private final StockFilter stockFilter;
	private static int solvedAmount = 0;
	private boolean deleteFilteredData = true;
	private DownloaderLogger logger;

	private volatile boolean stopped = false;

	DownloadYahooStockThread(DownloaderLogger logger, YahooSettings settings) {
		this.logger = logger;
		this.settings = settings;
		this.stockFilter = new StockFilter();
	}

	DownloadYahooStockThread(YahooSettings settings, boolean deleteFilteredData) {
		this.settings = settings;
		this.stockFilter = new StockFilter();
		this.deleteFilteredData = deleteFilteredData;
	}

	public void run() {
		String task = settings.getTask();
		while (task != null) {
			try {
				Optional<UnitedFormatStock> s = settings.getStockFromFileSystem(task);
				boolean downloaded = false;
				if (!s.isPresent()) {
					s = YahooDownloadHelper.download(task);
					if (s.isPresent()) {
						s.get().storeUniteFormat(getPath(settings.getDataFolder(), s.get().getName()));
					}
					downloaded = true;
					logger.log(StatisticType.TRACE, "task fully downloaded: " + task);
				} else {
					downloaded = YahooDownloadHelper.partiallyDownload(s.get(), task);
					if (downloaded) {
						s.get().storeUniteFormat(getPath(settings.getDataFolder(), s.get().getName()));
					}
					logger.log(StatisticType.TRACE, "task partially downloaded: " + task);
				}
				if (downloaded) {
					final boolean filtered = stockFilter.isLiquid(s.get()) && stockFilter.isValid(s.get());
					if (filtered) {
						YahooUtils.copyFilteredStockFile(settings.getDataFolder(), settings.getFilteredDataFolder(), task);
						logger.log(StatisticType.INFO, "task is liquid and copied to filter stock directory: " + task);
					} else {
						final boolean deleted = YahooDownloadHelper.deleteFilteredFile(deleteFilteredData,
								settings.getFilteredDataFolder(), task);
						if (deleted) {
							logger.log(StatisticType.DEBUG, "deleting filtered file with stock " + task
									+ " it doesn't pass new liquidity filter tests");
						}
					}
				} else {
					logger.log(StatisticType.INFO, "task is considered as downloaded: " + task);
				}
			} catch (Exception e) {
				logger.log(StatisticType.TRACE, "task " + task + " throwed an exception: " + e.toString());
				File file = new File(getPath(settings.getDataFolder(), task));
				if (file.length() == 0)
					file.delete();
			}
			synchronized (settings) {
				solvedAmount += 1;
				if (solvedAmount % printEach == 0)
					logger.log().info("solved {} tasks last stock name {}", solvedAmount, task);
			}
			if (stopped) {
				break;
			}
			task = settings.getTask();
		}
	}

	public void stop() {
		stopped = true;
	}

	private static String getPath(String folder, String taskName) {
		return YahooDownloadHelper.getPath(folder, taskName);
	}
}
