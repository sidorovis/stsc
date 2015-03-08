package stsc.database.service.schemas;

import java.io.IOException;
import java.sql.SQLException;

import liquibase.exception.LiquibaseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.database.migrations.FeedzillaDownloaderDatabaseSettings;
import stsc.database.service.storages.FeedzillaDownloaderDatabaseStorage;

public class OrmliteFeedzillaDownloaderSettingsTest {

	@Test
	public void testOrmliteFeedzillaDatafeedSettings() throws SQLException, LiquibaseException, IOException {
		final FeedzillaDownloaderDatabaseSettings settings = FeedzillaDownloaderDatabaseSettings.test().dropAll().migrate();
		final FeedzillaDownloaderDatabaseStorage storage = new FeedzillaDownloaderDatabaseStorage(settings);
		Assert.assertNotNull(storage);
		{
			final OrmliteFeedzillaDownloaderSettings oyds = new OrmliteFeedzillaDownloaderSettings("feedzilla_downloader_test");
			Assert.assertEquals(1, storage.setSettings(oyds).getNumLinesChanged());
		}
		{
			final OrmliteFeedzillaDownloaderSettings copy = storage.getSettings("feedzilla_downloader_test");
			Assert.assertEquals(2, copy.daysBackDownloadFrom());
			Assert.assertEquals(1, storage.setSettings(copy).getNumLinesChanged());
			Assert.assertEquals(true, copy.endlessCycle());
			Assert.assertEquals(20, copy.articlesWaitTime());
			Assert.assertEquals("./feed_data", copy.feedFolder());
			Assert.assertEquals(36000, copy.intervalBetweenExecutions());
		}
		settings.dropAll();
	}

}
