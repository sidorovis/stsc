package stsc.database.migrations;

import java.io.IOException;
import java.sql.SQLException;

import liquibase.exception.LiquibaseException;

import org.junit.Assert;
import org.junit.Test;

public class FeedzillaDatabaseSettingsTest {

	@Test
	public void testFeedzillaDatabaseSettings() throws IOException {
		final FeedzillaDownloaderDatabaseSettings ds = FeedzillaDownloaderDatabaseSettings.test();
		Assert.assertEquals("org.h2.Driver", ds.getJdbcDriver());
		Assert.assertEquals("jdbc:h2:mem:feedzilla_base;DB_CLOSE_DELAY=-1", ds.getJdbcUrl());
	}

	@Test
	public void testFeedzillaCreateConnectionToLiquibase() throws SQLException, IOException, LiquibaseException {
		final FeedzillaDownloaderDatabaseSettings ds = FeedzillaDownloaderDatabaseSettings.test();
		ds.migrate();
	}
}
