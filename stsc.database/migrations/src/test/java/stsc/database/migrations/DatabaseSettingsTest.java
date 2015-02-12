package stsc.database.migrations;

import java.io.IOException;
import java.sql.SQLException;

import liquibase.exception.LiquibaseException;

import org.junit.Assert;
import org.junit.Test;

public class DatabaseSettingsTest {

	@Test
	public void testDatabaseSettings() throws IOException {
		final DatafeedSettings ds = new DatafeedSettings();
		Assert.assertEquals("org.h2.Driver", ds.getJdbcDriver());
		Assert.assertEquals("jdbc:h2:./../test_data/liquibaseTest;AUTO_SERVER=TRUE", ds.getJdbcUrl());
	}

	@Test
	public void testCreateConnectionToLiquibase() throws SQLException, IOException, LiquibaseException {
		final DatafeedSettings ds = new DatafeedSettings();
		ds.migrate();
	}
}
