package stsc.yahoofetcher;

import java.io.File;
import java.io.IOException;
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
		DownloadThread downloadThread = new DownloadThread(marketDataContext, false);
		{
			Thread th = new Thread(downloadThread);
			th.start();
			th.join();
		}
		int beforeDownload = Integer.MAX_VALUE;
		{
			Stock s = marketDataContext.getStockFromFileSystem("aaoi");
			beforeDownload = s.getDays().size();
			assertEquals(104, s.getDays().size());
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
			assertEquals(true, beforeDownload <= s.getDays().size());
		}
		new File("./test/a.uf").delete();
		new File("./test/aaoi.uf").delete();
	}
}
