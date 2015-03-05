package stsc.database.service.statistics;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import liquibase.exception.LiquibaseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.service.statistics.StatisticType;
import stsc.database.migrations.YahooDownloaderDatabaseSettings;
import stsc.database.service.settings.YahooDownloaderDatabaseStorage;

public class OrmliteYahooDatafeedStatisticsTest {

	private Integer getId() {
		final String name = ManagementFactory.getRuntimeMXBean().getName();
		final String id = name.substring(0, name.indexOf('@'));
		return Integer.valueOf(id);
	}

	private Date create(YahooDownloaderDatabaseSettings settings) throws IOException, SQLException {
		final YahooDownloaderDatabaseStorage storage = new YahooDownloaderDatabaseStorage(settings);
		Assert.assertNotNull(storage);
		final Date d = new Date();
		{
			final OrmliteYahooDownloaderStatistics oyds = new OrmliteYahooDownloaderStatistics("yahoo_settings");
			oyds.setStartDate(d);
			oyds.setStatisticType(StatisticType.FATAL);
			oyds.setProcessId(getId());
			oyds.setMessage("this is a test message");
			Assert.assertEquals(1, storage.setYahooDatafeedStatistics(oyds).getNumLinesChanged());
			Assert.assertEquals(1, storage.setYahooDatafeedStatistics(oyds).getNumLinesChanged());
		}
		{
			final OrmliteYahooDownloaderStatistics oyds = new OrmliteYahooDownloaderStatistics("yahoo_settings");
			oyds.setStartDate(d);
			oyds.setStatisticType(StatisticType.ALL);
			oyds.setProcessId(getId());
			oyds.setMessage("another test message");
			Assert.assertEquals(1, storage.setYahooDatafeedStatistics(oyds).getNumLinesChanged());
			Assert.assertEquals(2, oyds.getId().intValue());
		}
		return d;
	}

	@Test
	public void testOrmliteYahooDatafeedSettings() throws SQLException, LiquibaseException, IOException {
		final YahooDownloaderDatabaseSettings settings = YahooDownloaderDatabaseSettings.test().dropAll().migrate();
		final Date d = create(settings);
		final YahooDownloaderDatabaseStorage storage = new YahooDownloaderDatabaseStorage(settings);
		final List<OrmliteYahooDownloaderStatistics> list = storage.getYahooDatafeedStatistics("yahoo_settings");
		checkList(d, list);
		settings.dropAll();
	}

	@Test
	public void testOrmliteYahooDatafeedSettingsGetStatisticsByPid() throws SQLException, LiquibaseException, IOException {
		final YahooDownloaderDatabaseSettings settings = YahooDownloaderDatabaseSettings.test().dropAll().migrate();
		final Date d = create(settings);
		final YahooDownloaderDatabaseStorage storage = new YahooDownloaderDatabaseStorage(settings);
		final List<OrmliteYahooDownloaderStatistics> list = storage.getYahooDatafeedStatistics("yahoo_settings", getId());
		checkList(d, list);
		settings.dropAll();
	}

	private void checkList(final Date d, final List<OrmliteYahooDownloaderStatistics> list) {
		Assert.assertEquals(2, list.size());
		final OrmliteYahooDownloaderStatistics copy = list.get(0);
		Assert.assertTrue(copy.getStartDate().equals(d));
		Assert.assertEquals(getId().intValue(), copy.getProcessId());
		Assert.assertEquals(StatisticType.FATAL, copy.getStatisticType());
		Assert.assertEquals("this is a test message", copy.getMessage());

		final OrmliteYahooDownloaderStatistics second = list.get(1);
		Assert.assertTrue(second.getStartDate().equals(d));
		Assert.assertEquals(getId().intValue(), second.getProcessId());
		Assert.assertEquals(StatisticType.ALL, second.getStatisticType());
		Assert.assertEquals("another test message", second.getMessage());
	}
}
