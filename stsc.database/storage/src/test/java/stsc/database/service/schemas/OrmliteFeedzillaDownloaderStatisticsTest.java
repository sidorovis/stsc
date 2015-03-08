package stsc.database.service.schemas;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import liquibase.exception.LiquibaseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.service.statistics.StatisticType;
import stsc.database.migrations.FeedzillaDownloaderDatabaseSettings;
import stsc.database.service.storages.FeedzillaDownloaderDatabaseStorage;

public class OrmliteFeedzillaDownloaderStatisticsTest {

	private Integer getId() {
		final String name = ManagementFactory.getRuntimeMXBean().getName();
		final String id = name.substring(0, name.indexOf('@'));
		return Integer.valueOf(id);
	}

	private Date create(FeedzillaDownloaderDatabaseSettings settings) throws IOException, SQLException {
		final FeedzillaDownloaderDatabaseStorage storage = new FeedzillaDownloaderDatabaseStorage(settings);
		Assert.assertNotNull(storage);
		final Date d = new Date();
		{
			final OrmliteFeedzillaDownloaderStatistics oyds = new OrmliteFeedzillaDownloaderStatistics("feedzilla_settings");
			oyds.setStartDate(d);
			oyds.setStatisticType(StatisticType.FATAL);
			oyds.setProcessId(getId());
			oyds.setMessage("this is a test message");
			Assert.assertEquals(1, storage.setStatistics(oyds).getNumLinesChanged());
			Assert.assertEquals(1, storage.setStatistics(oyds).getNumLinesChanged());
		}
		{
			final OrmliteFeedzillaDownloaderStatistics oyds = new OrmliteFeedzillaDownloaderStatistics("feedzilla_settings");
			oyds.setStartDate(d);
			oyds.setStatisticType(StatisticType.ALL);
			oyds.setProcessId(getId());
			oyds.setMessage("another test message");
			Assert.assertEquals(1, storage.setStatistics(oyds).getNumLinesChanged());
			Assert.assertEquals(2, oyds.getId().intValue());
		}
		return d;
	}

	@Test
	public void testOrmliteFeedzillaDatafeedSettings() throws SQLException, LiquibaseException, IOException {
		final FeedzillaDownloaderDatabaseSettings settings = FeedzillaDownloaderDatabaseSettings.test().dropAll().migrate();
		final Date d = create(settings);
		final FeedzillaDownloaderDatabaseStorage storage = new FeedzillaDownloaderDatabaseStorage(settings);
		final List<OrmliteFeedzillaDownloaderStatistics> list = storage.getStatistics("feedzilla_settings");
		checkList(d, list);
		settings.dropAll();
	}

	@Test
	public void testOrmliteFeedzillaDatafeedSettingsGetStatisticsByPid() throws SQLException, LiquibaseException, IOException {
		final FeedzillaDownloaderDatabaseSettings settings = FeedzillaDownloaderDatabaseSettings.test().dropAll().migrate();
		final Date d = create(settings);
		final FeedzillaDownloaderDatabaseStorage storage = new FeedzillaDownloaderDatabaseStorage(settings);
		final List<OrmliteFeedzillaDownloaderStatistics> list = storage.getStatistics("feedzilla_settings", getId());
		checkList(d, list);
		settings.dropAll();
	}

	private void checkList(final Date d, final List<OrmliteFeedzillaDownloaderStatistics> list) {
		Assert.assertEquals(2, list.size());
		final OrmliteFeedzillaDownloaderStatistics copy = list.get(0);
		Assert.assertTrue(copy.getStartDate().equals(d));
		Assert.assertEquals(getId().intValue(), copy.getProcessId());
		Assert.assertEquals(StatisticType.FATAL, copy.getStatisticType());
		Assert.assertEquals("this is a test message", copy.getMessage());

		final OrmliteFeedzillaDownloaderStatistics second = list.get(1);
		Assert.assertTrue(second.getStartDate().equals(d));
		Assert.assertEquals(getId().intValue(), second.getProcessId());
		Assert.assertEquals(StatisticType.ALL, second.getStatisticType());
		Assert.assertEquals("another test message", second.getMessage());
	}
}
