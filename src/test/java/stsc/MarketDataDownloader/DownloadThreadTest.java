package stsc.MarketDataDownloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.google.common.io.Files;

import junit.framework.TestCase;

public class DownloadThreadTest extends TestCase {
	public void testDownloadThread() throws InterruptedException, IOException,
			ClassNotFoundException {
		MarketDataContext marketDataContext = new MarketDataContext();
		marketDataContext.dataFolder = "./test/";
		Files.copy(new File("./test_data/aaoi.bin"),
				new File("./test/aaoi.bin"));
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
			InputStream is = new BufferedInputStream(new FileInputStream(
					"./test/aaoi.bin"));
			ObjectInput oi = new ObjectInputStream(is);
			Stock s = null;
			s = (Stock) oi.readObject();
			oi.close();
			Calendar cal = Calendar.getInstance();
			cal.set(2014, 1, 10);
			Date d93 = cal.getTime();
			Date today = new Date();
			Days days = Days.daysBetween(new DateTime(d93), new DateTime(today));
			assertEquals(93 + days.getDays(), s.getDays().size());
		}
		new File("./test/a.bin").delete();
		new File("./test/aaoi.bin").delete();
	}
}
