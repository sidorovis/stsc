package stsc.MarketDataFilter;

import java.io.IOException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stsc.MarketDataDownloader.MarketDataContext;
import stsc.MarketDataDownloader.Stock;

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
						s.store(marketDataContext
								.generateFilteredBinaryFilePath(s.getName()));
						logger.trace("stock " + task + " liquid");
					} else {
//						logger.trace("stock " + task + " not liquid");
					}
				}
			} catch (IOException e) {
				logger.trace("binary file " + task
						+ " processing throw IOException: " + e.toString());
			}
			task = marketDataContext.getTask();
		}

	}
}
