package stsc.database.migrations;

import java.io.IOException;
import java.sql.SQLException;

import liquibase.exception.LiquibaseException;

import org.junit.Assert;
import org.junit.Test;

public class DatabaseSettingsTest {

	@Test
	public void testDatabaseSettings() throws IOException {
		final YahooDownloaderDatabaseSettings ds = YahooDownloaderDatabaseSettings.test();
		Assert.assertEquals("org.h2.Driver", ds.getJdbcDriver());
		Assert.assertEquals("jdbc:h2:mem:test_base;DB_CLOSE_DELAY=-1", ds.getJdbcUrl());
	}

	@Test
	public void testCreateConnectionToLiquibase() throws SQLException, IOException, LiquibaseException {
		final YahooDownloaderDatabaseSettings ds = YahooDownloaderDatabaseSettings.test();
		ds.migrate();
	}
}
