package stsc.database.service.settings;

import java.io.IOException;
import java.sql.SQLException;

import liquibase.exception.LiquibaseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.database.migrations.DatabaseSettings;

public class DatabaseSettingsStorageTest {

	@Test
	public void testDatabaseSettingsStorage() throws IOException, SQLException, LiquibaseException, InterruptedException {
		final DatabaseSettings settings = DatabaseSettings.test().dropAll().migrate();
		final DatabaseSettingsStorage storage = new DatabaseSettingsStorage(settings);
		Assert.assertNotNull(storage);
		{
			final OrmliteYahooDatafeedSettings oyds = new OrmliteYahooDatafeedSettings("yahoo_settings");
			oyds.setThreadAmount(6);
			Assert.assertEquals(1, storage.setYahooDatafeedSettings(oyds).getNumLinesChanged());
		}
		{
			final OrmliteYahooDatafeedSettings copy = storage.getYahooDatafeedSettings("yahoo_settings");
			Assert.assertEquals(6, copy.threadAmount());
			Assert.assertEquals(1, storage.setYahooDatafeedSettings(copy).getNumLinesChanged());
		}
		settings.dropAll();
	}

}
