package stsc.database.service.settings;

import java.io.IOException;
import java.sql.SQLException;

import liquibase.exception.LiquibaseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.database.migrations.FeedzillaDownloaderDatabaseSettings;
import stsc.database.service.schemas.OrmliteFeedzillaDownloaderSettings;
import stsc.database.service.storages.FeedzillaDownloaderDatabaseStorage;

public class FeedzillaDatabaseSettingsStorageTest {

	@Test
	public void testFeedzillaDatabaseSettingsStorage() throws IOException, SQLException, LiquibaseException, InterruptedException {
		final FeedzillaDownloaderDatabaseSettings settings = FeedzillaDownloaderDatabaseSettings.test().dropAll().migrate();
		final FeedzillaDownloaderDatabaseStorage storage = new FeedzillaDownloaderDatabaseStorage(settings);
		Assert.assertNotNull(storage);
		{
			final OrmliteFeedzillaDownloaderSettings oyds = new OrmliteFeedzillaDownloaderSettings("feedzilla_downloader_test");
			oyds.setFeedFolder("the test feed folder");
			Assert.assertEquals(1, storage.setSettings(oyds).getNumLinesChanged());
		}
		{
			final OrmliteFeedzillaDownloaderSettings copy = storage.getSettings("feedzilla_downloader_test");
			Assert.assertEquals("the test feed folder", copy.feedFolder());
			Assert.assertEquals(1, storage.setSettings(copy).getNumLinesChanged());
		}
		settings.dropAll();
	}

}
