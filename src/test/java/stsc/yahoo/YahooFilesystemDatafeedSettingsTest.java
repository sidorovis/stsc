package stsc.yahoo;

import java.io.IOException;

import stsc.yahoo.YahooFilesystemDatafeedSettings;
import junit.framework.TestCase;

public class YahooFilesystemDatafeedSettingsTest extends TestCase {
	public void testYahooFilesystemDatafeedSettings() throws IOException {
		YahooFilesystemDatafeedSettings settings = new YahooFilesystemDatafeedSettings("./test/", "./test/");
		assertEquals(settings.getTask(), null);
		settings.addTask("a");
		assertEquals(1, settings.taskQueueSize());
		assertEquals(settings.getTask(), "a");
		assertEquals(settings.getTask(), null);

		assertEquals("./test/asd.uf", settings.generateUniteFormatPath("asd"));

	}

	public void testGetStockFromFileSystem() {
		YahooFilesystemDatafeedSettings settings = new YahooFilesystemDatafeedSettings("./test_data/", "./test/");
		assertNotNull(settings.getStockFromFileSystem("aapl"));
		assertNull(settings.getStockFromFileSystem("a"));
	}
}
