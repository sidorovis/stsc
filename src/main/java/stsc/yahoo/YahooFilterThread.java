package stsc.yahoo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stsc.common.Stock;
import stsc.common.UnitedFormatStock;
import stsc.liquiditator.StockFilter;

public class YahooFilterThread implements Runnable {

	private final YahooFilesystemDatafeedSettings settings;
	private final StockFilter stockFilter;
	private static Logger logger = LogManager.getLogger("FilterThread");

	public YahooFilterThread(final YahooFilesystemDatafeedSettings settings, Date d) {
		this.settings = settings;
		this.stockFilter = new StockFilter(d);
	}

	public YahooFilterThread(final YahooFilesystemDatafeedSettings settings) {
		this.settings = settings;
		stockFilter = new StockFilter();
	}

	public void run() {
		String task = settings.getTask();
		while (task != null) {
			try {
				Stock s = settings.getStockFromFileSystem(task);
				if (s != null && stockFilter.test(s)) {
					copyFilteredStockFile(settings.getDataFolder(), settings.getFilteredDataFolder(), task);
					logger.trace("stock " + task + " liquid");
				} else {
					deleteIfExisted(task);
				}
			} catch (IOException e) {
				logger.trace("binary file " + task + " processing throw IOException: " + e.toString());
			}
			task = settings.getTask();
		}
	}

	public void deleteIfExisted(String stockName) {
		final File file = new File(UnitedFormatStock.generatePath(settings.getFilteredDataFolder(), stockName));
		if (file.exists()) {
			logger.debug("deleting filtered file with stock " + stockName
					+ " it doesn't pass new liquidity filter tests");
			file.delete();
		}
	}

	public static void copyFilteredStockFile(String dataFolder, String filteredDataFolder, String stockName)
			throws IOException {
		final File originalFile = new File(UnitedFormatStock.generatePath(dataFolder, stockName));
		final File filteredFile = new File(UnitedFormatStock.generatePath(filteredDataFolder, stockName));
		if (filteredFile.exists() && originalFile.exists() && filteredFile.length() == originalFile.length()) {
			// filteter file exists and have the same size, so do nothing
		} else
			Files.copy(originalFile.toPath(), filteredFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

}
