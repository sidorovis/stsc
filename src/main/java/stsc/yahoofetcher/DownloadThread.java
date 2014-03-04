package stsc.yahoofetcher;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import stsc.common.MarketDataContext;
import stsc.common.UnitedFormatStock;
import stsc.liquiditator.FilterThread;
import stsc.liquiditator.StockFilter;

import com.google.common.io.CharStreams;

public class DownloadThread implements Runnable {

	private MarketDataContext marketDataContext;
	private StockFilter stockFilter;
	private static int solvedAmount = 0;
	private boolean deleteFilteredData = true;
	private static Logger logger = LogManager.getLogger("DownloadThread");

	public DownloadThread(MarketDataContext mdc) {
		marketDataContext = mdc;
		stockFilter = new StockFilter();
	}

	public DownloadThread(MarketDataContext mdc, boolean deleteFilteredData) {
		marketDataContext = mdc;
		stockFilter = new StockFilter();
		this.deleteFilteredData = deleteFilteredData;
	}

	public void run() {
		String task = marketDataContext.getTask();
		while (task != null) {
			try {
				UnitedFormatStock s = marketDataContext.getStockFromFileSystem(task);
				if (s == null) {
					s = download(task);
					logger.trace("task {} fully downloaded", task);
				} else {
					partiallyDownload(s, task);
					logger.trace("task {} partially downloaded", task);
				}
				if (stockFilter.test(s)) {
					FilterThread.copyFilteredStockFile(marketDataContext, task);
					logger.info("task {} is liquid and copied to filter stock directory", task);
				} else {
					deleteEmptyFilteredFile(task);
				}
			} catch (Exception e) {
				logger.debug("task {} throwed an exception {}", task, e.toString());
				File file = new File(marketDataContext.generateUniteFormatPath(task));
				if (file.length() == 0)
					file.delete();
			}
			synchronized (marketDataContext) {
				solvedAmount += 1;
				if (solvedAmount % 100 == 0)
					logger.info("solved {} tasks last stock name {}", solvedAmount, task);
			}
			task = marketDataContext.getTask();
		}
	}

	private void deleteEmptyFilteredFile(String stockName) {
		if (deleteFilteredData) {
			String filteredFilePath = marketDataContext.generateFilteredUniteFormatPath(stockName);
			File filteredFile = new File(filteredFilePath);
			if (filteredFile.exists()) {
				logger.debug("deleting filtered file with stock " + stockName
						+ " it doesn't pass new liquidity filter tests");
				filteredFile.delete();
			}
		}
	}

	private final UnitedFormatStock download(String stockName) throws InterruptedException {
		int tries = 0;
		String error = "";
		UnitedFormatStock newStock = null;
		while (tries < 5) {
			try {
				URL url = new URL("http://ichart.finance.yahoo.com/table.csv?s=" + stockName);
				String stockContent = CharStreams.toString(new InputStreamReader(url.openStream()));
				newStock = UnitedFormatStock.newFromString(stockName, stockContent);
				if (newStock.getDays().isEmpty())
					return null;
				newStock.storeUniteFormat(marketDataContext.generateUniteFormatPath(newStock.getName()));
				return newStock;
			} catch (MalformedURLException e) {
				Thread.sleep(300);
				error = e.toString();
			} catch (ParseException e) {
				Thread.sleep(300);
				error = e.toString();
			} catch (IOException e) {
				Thread.sleep(300);
				error = e.toString();
			}
			tries += 1;
		}
		if (newStock == null)
			throw new InterruptedException("5 tries not enought to download data on " + stockName + " stock. " + error);
		return newStock;
	}

	private final void partiallyDownload(UnitedFormatStock stock, String stockName) throws InterruptedException {
		String downloadLink = stock.generatePartiallyDownloadLine();
		String error = "";
		String stockNewContent = "";
		int tries = 0;

		while (tries < 5) {
			try {
				URL url = new URL(downloadLink);
				stockNewContent = CharStreams.toString(new InputStreamReader(url.openStream()));
				boolean newDays = stock.addDaysFromString(stockNewContent);
				if (newDays)
					stock.storeUniteFormat(marketDataContext.generateUniteFormatPath(stock.getName()));
				return;
			} catch (ParseException e) {
				error = "exception " + e.toString() + " with: '" + stockNewContent + "'";
				break;
			} catch (MalformedURLException e) {
				Thread.sleep(300);
			} catch (IOException e) {
				Thread.sleep(300);
			}
			tries += 1;
		}
		throw new InterruptedException("5 tries not enought to partially download data on " + downloadLink + " stock "
				+ error);
	}
}
