package stsc.yahoo.downloader;

import java.io.File;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	private static Logger logger = LogManager.getLogger("DownloadThread");

	private volatile boolean stopped = false;

	DownloadYahooStockThread(YahooSettings settings) {
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
					logger.trace("task {} fully downloaded", task);
				} else {
					downloaded = YahooDownloadHelper.partiallyDownload(s.get(), task);
					if (downloaded) {
						s.get().storeUniteFormat(getPath(settings.getDataFolder(), s.get().getName()));
					}
					logger.trace("task {} partially downloaded", task);
				}
				if (downloaded) {
					final boolean filtered = stockFilter.isLiquid(s.get()) && stockFilter.isValid(s.get());
					if (filtered) {
						YahooUtils.copyFilteredStockFile(settings.getDataFolder(), settings.getFilteredDataFolder(), task);
						logger.info("task {} is liquid and copied to filter stock directory", task);
					} else {
						final boolean deleted = YahooDownloadHelper.deleteFilteredFile(deleteFilteredData,
								settings.getFilteredDataFolder(), task);
						if (deleted) {
							logger.debug("deleting filtered file with stock " + task + " it doesn't pass new liquidity filter tests");
						}
					}
				} else {
					logger.info("task {} is considered as downloaded", task);
				}
			} catch (Exception e) {
				logger.debug("task {} throwed an exception {}", task, e.toString());
				File file = new File(getPath(settings.getDataFolder(), task));
				if (file.length() == 0)
					file.delete();
			}
			synchronized (settings) {
				solvedAmount += 1;
				if (solvedAmount % printEach == 0)
					logger.info("solved {} tasks last stock name {}", solvedAmount, task);
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
