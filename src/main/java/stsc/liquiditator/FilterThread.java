package stsc.liquiditator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stsc.common.MarketDataContext;
import stsc.common.Stock;

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
				Stock s = marketDataContext.getStockFromFileSystem(task);
				if (s != null) {
					if (stockFilter.test(s)) {
						File filteredFile = new File(marketDataContext.generateFilteredBinaryFilePath(task));
						File originalFile = new File(marketDataContext.generateBinaryFilePath(task));
						if (filteredFile.exists() && originalFile.exists()
								&& filteredFile.length() == originalFile.length()) {
							// do nothing
						} else
							Files.copy( originalFile.toPath(), filteredFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
						logger.trace("stock " + task + " liquid");
					} else {
					}
				}
			} catch (IOException e) {
				logger.trace("binary file " + task + " processing throw IOException: " + e.toString());
			}
			task = marketDataContext.getTask();
		}

	}
}
