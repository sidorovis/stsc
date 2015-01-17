package stsc.yahoo;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class YahooFilesystemDatafeedSettingsTest {
	
	@Test
	public void testYahooFilesystemDatafeedSettings() throws IOException {
		YahooSettings settings = new YahooSettings("./test/", "./test/");
		Assert.assertEquals(settings.getTask(), null);
		settings.addTask("a");
		Assert.assertEquals(1, settings.taskQueueSize());
		Assert.assertEquals(settings.getTask(), "a");
		Assert.assertEquals(settings.getTask(), null);

		Assert.assertEquals(new File("./test/asd.uf"), new File(settings.generateUniteFormatPath("asd")));
	}

	@Test
	public void testGetStockFromFileSystem() throws IOException {
		final YahooSettings settings = new YahooSettings("./test_data/", "./test/");
		Assert.assertNotNull(settings.getStockFromFileSystem("aapl"));
		Assert.assertNull(settings.getStockFromFileSystem("a"));
	}
}
