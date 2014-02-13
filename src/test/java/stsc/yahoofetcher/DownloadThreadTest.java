package stsc.yahoofetcher;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;

import stsc.common.MarketDataContext;
import stsc.common.Stock;

import com.google.common.io.Files;

import junit.framework.TestCase;

public class DownloadThreadTest extends TestCase {
	public void testDownloadThread() throws InterruptedException, IOException, ClassNotFoundException {
		MarketDataContext marketDataContext = new MarketDataContext();
		marketDataContext.dataFolder = "./test/";
		marketDataContext.filteredDataFolder = "./test/";
		Files.copy(new File("./test_data/aaoi.uf"), new File("./test/aaoi.uf"));
		marketDataContext.addTask("a");
		DownloadThread downloadThread = new DownloadThread(marketDataContext);
		{
			Thread th = new Thread(downloadThread);
			th.start();
			th.join();
		}
		marketDataContext.addTask("aaoi");
		{
			Thread th = new Thread(downloadThread);
			th.start();
			th.join();
		}
		marketDataContext.addTask("aaoi");
		{
			Thread th = new Thread(downloadThread);
			th.start();
			th.join();
		}
		{
			Stock s = marketDataContext.getStockFromFileSystem("aaoi");
			Calendar cal = Calendar.getInstance();
			cal.set(2014, 1, 10);
			Date d93 = cal.getTime();
			Date today = new Date();
			Days days = Days.daysBetween(new DateTime(d93), new DateTime(today));
			assertEquals(93 + days.getDays(), s.getDays().size());
		}
		new File("./test/a.uf").delete();
		new File("./test/aaoi.uf").delete();
	}
}
