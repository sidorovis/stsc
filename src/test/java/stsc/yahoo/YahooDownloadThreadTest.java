package stsc.yahoo;

import java.io.File;
import java.io.IOException;

import stsc.common.Stock;
import stsc.yahoo.DownloadThread;
import stsc.yahoo.DownloadThreadSettings;

import com.google.common.io.Files;

import junit.framework.TestCase;

public class YahooDownloadThreadTest extends TestCase {
	public void testDownloadThread() throws InterruptedException, IOException, ClassNotFoundException {
		DownloadThreadSettings settings = new DownloadThreadSettings("./test/", "./test/");
		Files.copy(new File("./test_data/aaoi.uf"), new File("./test/aaoi.uf"));
		settings.addTask("a");
		DownloadThread downloadThread = new DownloadThread(settings, false);
		{
			Thread th = new Thread(downloadThread);
			th.start();
			th.join();
		}
		int beforeDownload = Integer.MAX_VALUE;
		{
			Stock s = settings.getStockFromFileSystem("aaoi");
			beforeDownload = s.getDays().size();
			assertEquals(104, s.getDays().size());
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
			assertEquals(true, beforeDownload < s.getDays().size());
		}
		new File("./test/a.uf").delete();
		new File("./test/aaoi.uf").delete();
	}
}
