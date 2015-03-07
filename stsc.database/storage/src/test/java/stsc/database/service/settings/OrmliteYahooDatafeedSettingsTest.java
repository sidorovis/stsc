package stsc.database.service.settings;

import java.io.IOException;
import java.sql.SQLException;

import liquibase.exception.LiquibaseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.database.migrations.YahooDownloaderDatabaseSettings;

public class OrmliteYahooDatafeedSettingsTest {

	@Test
	public void testOrmliteYahooDatafeedSettings() throws SQLException, LiquibaseException, IOException {
		final YahooDownloaderDatabaseSettings settings = YahooDownloaderDatabaseSettings.test().dropAll().migrate();
		final YahooDownloaderDatabaseStorage storage = new YahooDownloaderDatabaseStorage(settings);
		Assert.assertNotNull(storage);
		{
			final OrmliteYahooDownloaderSettings oyds = new OrmliteYahooDownloaderSettings("yahoo_downloader_test");
			oyds.setThreadAmount(6);
			oyds.setDownloadByPattern(false);
			oyds.setDownloadOnlyExisted(false);
			oyds.setPatternNameFrom("ASD");
			oyds.setPatternNameTo("GFDS");
			oyds.setStockNameFrom(4);
			oyds.setStockNameTo(19);
			Assert.assertEquals(1, storage.setYahooDatafeedSettings(oyds).getNumLinesChanged());
		}
		{
			final OrmliteYahooDownloaderSettings copy = storage.getYahooDatafeedSettings("yahoo_downloader_test");
			Assert.assertEquals(6, copy.threadAmount());
			Assert.assertEquals(1, storage.setYahooDatafeedSettings(copy).getNumLinesChanged());
			Assert.assertEquals(false, copy.downloadByPattern());
			Assert.assertEquals(false, copy.downloadOnlyExisted());
			Assert.assertEquals("ASD", copy.patternNameFrom());
			Assert.assertEquals("GFDS", copy.patternNameTo());
			Assert.assertEquals(4, copy.stockNameFrom());
			Assert.assertEquals(19, copy.stockNameTo());
		}
		settings.dropAll();
	}

}
