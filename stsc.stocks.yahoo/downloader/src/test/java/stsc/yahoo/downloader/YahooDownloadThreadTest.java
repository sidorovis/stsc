package stsc.yahoo.downloader;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.stocks.Stock;
import stsc.yahoo.YahooSettings;
import stsc.yahoo.YahooUtils;

import com.google.common.io.Files;

public class YahooDownloadThreadTest {

	@Test
	public void testDownloadThread() throws InterruptedException, IOException, ClassNotFoundException {
		final YahooSettings settings = YahooUtils.createSettings("./test/", "./test/");
		Files.copy(new File("./test_data/aaoi.uf"), new File("./test/aaoi.uf"));
		settings.addTask("a");
		DownloadYahooStockThread downloadThread = new DownloadYahooStockThread(settings, false);
		{
			Thread th = new Thread(downloadThread);
			th.start();
			th.join();
		}
		int beforeDownload = Integer.MAX_VALUE;
		{
			Stock s = settings.getStockFromFileSystem("aaoi");
			beforeDownload = s.getDays().size();
			Assert.assertEquals(104, s.getDays().size());
		}
		settings.addTask("aaoi");
		{
			Thread th = new Thread(downloadThread);
			th.start();
			th.join();
		}
		settings.addTask("aaoi");
		{
			Thread th = new Thread(downloadThread);
			th.start();
			th.join();
		}
		{
			Stock s = settings.getStockFromFileSystem("aaoi");
			Assert.assertEquals(true, beforeDownload < s.getDays().size());
		}
		new File("./test/a.uf").delete();
		new File("./test/aaoi.uf").delete();
	}
}
