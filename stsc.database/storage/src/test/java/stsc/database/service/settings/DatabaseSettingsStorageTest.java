package stsc.database.service.settings;

import java.io.IOException;
import java.sql.SQLException;

import liquibase.exception.LiquibaseException;

import org.junit.Assert;
import org.junit.Test;

import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;

import stsc.database.migrations.DatabaseSettings;

public class DatabaseSettingsStorageTest {

	@Test
	public void testDatabaseSettingsStorage() throws IOException, SQLException, LiquibaseException {
		final DatabaseSettings settings = DatabaseSettings.test().migrate();
		final DatabaseSettingsStorage storage = new DatabaseSettingsStorage(settings);
		Assert.assertNotNull(storage);
		OrmliteYahooDatafeedSettings oyds = new OrmliteYahooDatafeedSettings("yahoo settings");
		final CreateOrUpdateStatus status = storage.createOrUpdateCategory(oyds);
		Assert.assertEquals(1, status.getNumLinesChanged());
	}

}
