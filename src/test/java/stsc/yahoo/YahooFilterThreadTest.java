package stsc.yahoo;

import java.io.File;
import java.io.IOException;

import org.joda.time.LocalDate;

import stsc.yahoo.YahooFilesystemDatafeedSettings;
import stsc.yahoo.YahooFilterThread;
import junit.framework.TestCase;

public class YahooFilterThreadTest extends TestCase {
	public void testFilterThread() throws IOException, InterruptedException {
		YahooFilesystemDatafeedSettings settings = new YahooFilesystemDatafeedSettings("./test_data/", "./test/");

		settings.addTask("aaoi").addTask("aapl").addTask("ibm").addTask("spy");

		YahooFilterThread filterThread = new YahooFilterThread(settings, new LocalDate(2014, 1, 14).toDate());
		{
			Thread th = new Thread(filterThread);
			th.start();
			th.join();
		}
		assertEquals(false, new File("./test/aaoi.uf").exists());
		assertEquals(true, new File("./test/aapl.uf").exists());
		assertEquals(false, new File("./test/ibm.uf").exists());
		assertEquals(true, new File("./test/spy.uf").exists());
		new File("./test/aapl.uf").delete();
		new File("./test/spy.uf").delete();
	}
}
