package stsc.liquiditator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stsc.common.MarketDataContext;
import stsc.common.StockInterface;

public class FilterThread implements Runnable {

	MarketDataContext marketDataContext;
	StockFilter stockFilter;
	private static Logger logger = LogManager.getLogger("FilterThread");

	public FilterThread(MarketDataContext m, Date d) {
		marketDataContext = m;
		stockFilter = new StockFilter(d);
	}

	public FilterThread(MarketDataContext m) {
		marketDataContext = m;
		stockFilter = new StockFilter();
	}

	public void run() {
		String task = marketDataContext.getTask();
		while (task != null) {
			try {
				StockInterface s = marketDataContext.getStockFromFileSystem(task);
				if (s != null && stockFilter.test(s)) {
					copyFilteredStockFile(marketDataContext, task);
					logger.trace("stock " + task + " liquid");
				} else {
					deleteIfExisted(task);
				}
			} catch (IOException e) {
				logger.trace("binary file " + task + " processing throw IOException: " + e.toString());
			}
			task = marketDataContext.getTask();
		}
	}

	public void deleteIfExisted(String stockName) {
		File file = new File(marketDataContext.generateFilteredUniteFormatPath(stockName));
		if (file.exists()) {
			logger.debug("deleting filtered file with stock " + stockName
					+ " it doesn't pass new liquidity filter tests");
			file.delete();
		}
	}

	public static void copyFilteredStockFile(MarketDataContext marketDataContext, String stockName) throws IOException {
		File filteredFile = new File(marketDataContext.generateFilteredUniteFormatPath(stockName));
		File originalFile = new File(marketDataContext.generateUniteFormatPath(stockName));
		if (filteredFile.exists() && originalFile.exists() && filteredFile.length() == originalFile.length()) {
			// do nothing
		} else
			Files.copy(originalFile.toPath(), filteredFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
}
