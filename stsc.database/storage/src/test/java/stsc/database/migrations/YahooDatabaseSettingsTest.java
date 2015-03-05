package stsc.database.migrations;

import java.io.IOException;
import java.sql.SQLException;

import liquibase.exception.LiquibaseException;

import org.junit.Assert;
import org.junit.Test;

public class YahooDatabaseSettingsTest {

	@Test
	public void testYahooDatabaseSettings() throws IOException {
		final YahooDownloaderDatabaseSettings ds = YahooDownloaderDatabaseSettings.test();
		Assert.assertEquals("org.h2.Driver", ds.getJdbcDriver());
		Assert.assertEquals("jdbc:h2:mem:yahoo_base;DB_CLOSE_DELAY=-1", ds.getJdbcUrl());
	}

	@Test
	public void testYahooCreateConnectionToLiquibase() throws SQLException, IOException, LiquibaseException {
		final YahooDownloaderDatabaseSettings ds = YahooDownloaderDatabaseSettings.test();
		ds.migrate();
	}
}
