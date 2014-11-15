package stsc.yahoo.downloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stsc.common.stocks.UnitedFormatStock;
import stsc.yahoo.YahooSettings;
import stsc.yahoo.YahooUtils;
import stsc.yahoo.liquiditator.StockFilter;

import com.google.common.io.CharStreams;

class DownloadThread implements Runnable {

	private static final int waitTimeBetweenTries = 500;
	private static final int waitTriesAmount = 5;
	private static final int printEach = 100;

	private final YahooSettings settings;
	private final StockFilter stockFilter;
	private static int solvedAmount = 0;
	private boolean deleteFilteredData = true;
	private static Logger logger = LogManager.getLogger("DownloadThread");

	DownloadThread(YahooSettings settings) {
		this.settings = settings;
		this.stockFilter = new StockFilter();
	}

	DownloadThread(YahooSettings settings, boolean deleteFilteredData) {
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
					s = download(task);
					downloaded = true;
					logger.trace("task {} fully downloaded", task);
				} else {
					downloaded = partiallyDownload(s, task);
					logger.trace("task {} partially downloaded", task);
				}
				final boolean filtered = stockFilter.testStock(s);
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

	private final UnitedFormatStock download(String stockName) throws InterruptedException {
		int tries = 0;
		String error = "";
		UnitedFormatStock newStock = null;
		while (tries < waitTriesAmount) {
			try {
				URL url = new URL("http://ichart.finance.yahoo.com/table.csv?s=" + stockName);
				String stockContent = CharStreams.toString(new InputStreamReader(url.openStream()));
				newStock = UnitedFormatStock.newFromString(stockName, stockContent);
				if (newStock.getDays().isEmpty())
					return null;
				newStock.storeUniteFormat(getPath(settings.getDataFolder(), newStock.getName()));
				return newStock;
			} catch (ParseException | IOException e) {
				error = e.toString();
			}
			tries += 1;
			Thread.sleep(waitTimeBetweenTries);
		}
		if (newStock == null)
			throw new InterruptedException(waitTriesAmount + " tries not enought to download data on " + stockName + " stock. " + error);
		return newStock;
	}

	private final boolean partiallyDownload(UnitedFormatStock stock, String stockName) throws InterruptedException {
		String downloadLink = stock.generatePartiallyDownloadLine();
		if (downloadLink.isEmpty()) {
			return false;
		}
		String error = "";
		String stockNewContent = "";
		int tries = 0;

		while (tries < waitTriesAmount) {
			try {
				URL url = new URL(downloadLink);
				stockNewContent = CharStreams.toString(new InputStreamReader(url.openStream()));
				boolean newDays = stock.addDaysFromString(stockNewContent);
				if (newDays)
					stock.storeUniteFormat(getPath(settings.getDataFolder(), stock.getName()));
				return true;
			} catch (ParseException e) {
				error = "exception " + e.toString() + " with: '" + stockNewContent + "'";
			} catch (IOException e) {
			}
			tries += 1;
			Thread.sleep(waitTimeBetweenTries);
		}
		throw new InterruptedException("" + waitTriesAmount + " tries not enought to partially download data on " + downloadLink
				+ " stock " + error);
	}

	private static String getPath(String folder, String taskName) {
		return UnitedFormatStock.generatePath(folder, taskName);
	}
}
