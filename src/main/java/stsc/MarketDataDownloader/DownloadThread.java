package stsc.MarketDataDownloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.text.ParseException;
import com.google.common.io.CharStreams;

public class DownloadThread implements Runnable {

	MarketDataContext marketDataContext;
	static int amount = 0;

	DownloadThread(MarketDataContext mdc) {
		marketDataContext = mdc;
	}

	public void run() {
		String task = marketDataContext.getTask();
		while (task != null) {
			try {
				Stock s = getStockFromFileSystem(task);
				if (s == null) {
					download(task);
				} else {
					partiallyDownload(s, task);
				}
			} catch (Exception e) {
				File file = new File(generateFilePath(task));
				if (file.length() == 0)
					file.delete();
			}
			task = marketDataContext.getTask();
		}
	}

	public final Stock download(String stockName) throws IOException,
			ParseException {
		URL url = new URL("http://ichart.finance.yahoo.com/table.csv?s="
				+ stockName);
		String stockContent = CharStreams.toString(new InputStreamReader(url
				.openStream()));
		Stock newStock = Stock.newStockFromString(stockName, stockContent);
		if (newStock.getDays().isEmpty())
			return null;
		printOutStock(newStock);
		return newStock;
	}

	public final void partiallyDownload(Stock stock, String stockName)
			throws IOException, ParseException {
		String downloadLink = stock.generatePartiallyDownloadLine();
		URL url = new URL(downloadLink);
		String stockNewContent = CharStreams.toString(new InputStreamReader(url
				.openStream()));
		boolean newDays = stock.addDaysFromString(stockNewContent);
		if ( newDays )
			printOutStock(stock);
	}

	private void printOutStock(Stock s) throws FileNotFoundException,
			IOException {
		ObjectOutputStream outFile = null;
		outFile = new ObjectOutputStream(new FileOutputStream(
				generateBinaryFilePath(s.name)));
		outFile.writeObject(s);
		outFile.close();
	}

	private final Stock getStockFromFileSystem(String stockName) {
		Stock s = null;
		InputStream is;
		try {
			is = new BufferedInputStream(new FileInputStream(
					generateBinaryFilePath(stockName)));
			ObjectInput oi = new ObjectInputStream(is);
			s = (Stock) oi.readObject();
			oi.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		}
		return s;
	}

	private String generateFilePath(String stockName) {
		String dataFolder = marketDataContext.getDataFolder();
		return dataFolder + stockName + ".csv";
	}

	private String generateBinaryFilePath(String stockName) {
		String dataFolder = marketDataContext.getDataFolder();
		return dataFolder + stockName + ".bin";
	}
}
