package stsc.yahoo.downloader;

import java.io.File;
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
				UnitedFormatStock s = settings.getStockFromFileSystem(task);
				boolean downloaded = false;
				if (s == null) {
					s = YahooDownloadHelper.download(task);
					if (s != null) {
						s.storeUniteFormat(getPath(settings.getDataFolder(), s.getName()));
					}
					downloaded = true;
					logger.trace("task {} fully downloaded", task);
				} else {
					downloaded = YahooDownloadHelper.partiallyDownload(settings, s, task);
					if (downloaded)
						s.storeUniteFormat(getPath(settings.getDataFolder(), s.getName()));
					logger.trace("task {} partially downloaded", task);
				}
				final boolean filtered = stockFilter.isLiquid(s) && stockFilter.isValid(s);
				if (downloaded) {
					if (filtered) {
						YahooUtils.copyFilteredStockFile(settings.getDataFolder(), settings.getFilteredDataFolder(), task);
						logger.info("task {} is liquid and copied to filter stock directory", task);
					} else {
						deleteEmptyFilteredFile(task);
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
			task = settings.getTask();
		}
	}

	private void deleteEmptyFilteredFile(String stockName) {
		if (deleteFilteredData) {
			String filteredFilePath = getPath(settings.getFilteredDataFolder(), stockName);
			File filteredFile = new File(filteredFilePath);
			if (filteredFile.exists()) {
				logger.debug("deleting filtered file with stock " + stockName + " it doesn't pass new liquidity filter tests");
				filteredFile.delete();
			}
		}
	}

	private static String getPath(String folder, String taskName) {
		return YahooDownloadHelper.getPath(folder, taskName);
	}
}
