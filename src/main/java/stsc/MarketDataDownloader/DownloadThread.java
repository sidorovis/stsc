package stsc.MarketDataDownloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.io.CharStreams;

public class DownloadThread implements Runnable {

	MarketDataContext marketDataContext;
	static int solvedAmount = 0;
	private static Logger logger = LogManager.getLogger("DownloadThread");

	DownloadThread(MarketDataContext mdc) {
		marketDataContext = mdc;
	}

	public void run() {
		String task = marketDataContext.getTask();
		while (task != null) {
			try {
				Stock s = marketDataContext.getStockFromFileSystem(task);
				if (s == null) {
					download(task);
					logger.trace("task {} fully downloaded", task);
				} else {
					partiallyDownload(s, task);
					logger.trace("task {} partially downloaded", task);
				}
			} catch (Exception e) {
				logger.warn("task {} throwed an exception {}", task,
						e.toString());
				File file = new File(marketDataContext.generateFilePath(task));
				if (file.length() == 0)
					file.delete();
			}
			synchronized (marketDataContext) {
				solvedAmount += 1;
				if (solvedAmount % 100 == 0)
					logger.info("solved {} tasks", solvedAmount);
			}
			task = marketDataContext.getTask();
		}
	}

	public final void download(String stockName) throws ParseException,
			MalformedURLException, InterruptedException {
		int tries = 0;

		Stock newStock = null;
		while (tries < 5) {
			URL url = new URL("http://ichart.finance.yahoo.com/table.csv?s="
					+ stockName);
			try {
				String stockContent = CharStreams
						.toString(new InputStreamReader(url.openStream()));
				newStock = Stock.newFromString(stockName, stockContent);
				if (newStock.getDays().isEmpty())
					return;
				newStock.store(marketDataContext
						.generateBinaryFilePath(newStock.name));
				return;
			} catch (IOException e) {
				Thread.sleep(100);
			}
			tries += 1;
		}
		if (newStock == null)
			throw new InterruptedException(
					"5 tries not enought to download data on " + stockName
							+ " stock");
		return;
	}

	public final void partiallyDownload(Stock stock, String stockName)
			throws IOException, ParseException, InterruptedException {
		String downloadLink = stock.generatePartiallyDownloadLine();

		int tries = 0;

		while (tries < 5) {
			URL url = new URL(downloadLink);
			try {
				String stockNewContent = CharStreams
						.toString(new InputStreamReader(url.openStream()));
				boolean newDays = stock.addDaysFromString(stockNewContent);
				if (newDays)
					stock.store(marketDataContext
							.generateBinaryFilePath(stock.name));
				return;
			} catch (IOException e) {
				Thread.sleep(100);
			}
			tries += 1;
		}
		throw new InterruptedException(
				"5 tries not enought to partially download data on "
						+ downloadLink + " stock");
	}
}
