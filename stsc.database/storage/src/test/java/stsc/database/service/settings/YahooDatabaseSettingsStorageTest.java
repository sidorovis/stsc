package stsc.database.service.settings;

import java.io.IOException;
import java.sql.SQLException;

import liquibase.exception.LiquibaseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.database.migrations.YahooDownloaderDatabaseSettings;

public class YahooDatabaseSettingsStorageTest {

	@Test
	public void testYahooDatabaseSettingsStorage() throws IOException, SQLException, LiquibaseException, InterruptedException {
		final YahooDownloaderDatabaseSettings settings = YahooDownloaderDatabaseSettings.test().dropAll().migrate();
		final YahooDownloaderDatabaseStorage storage = new YahooDownloaderDatabaseStorage(settings);
		Assert.assertNotNull(storage);
		{
			final OrmliteYahooDownloaderSettings oyds = new OrmliteYahooDownloaderSettings("yahoo_downloader_test");
			oyds.setThreadAmount(6);
			Assert.assertEquals(1, storage.setYahooDatafeedSettings(oyds).getNumLinesChanged());
		}
		{
			final OrmliteYahooDownloaderSettings copy = storage.getYahooDatafeedSettings("yahoo_downloader_test");
			Assert.assertEquals(6, copy.threadAmount());
			Assert.assertEquals(1, storage.setYahooDatafeedSettings(copy).getNumLinesChanged());
		}
		settings.dropAll();
	}

}
