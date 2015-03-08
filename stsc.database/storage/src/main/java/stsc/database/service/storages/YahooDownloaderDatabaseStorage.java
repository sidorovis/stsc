package stsc.database.service.storages;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.Validate;

import stsc.database.migrations.YahooDownloaderDatabaseSettings;
import stsc.database.service.schemas.OrmliteYahooDownloaderSettings;
import stsc.database.service.schemas.OrmliteYahooDownloaderStatistics;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class YahooDownloaderDatabaseStorage {

	private final ConnectionSource source;

	private final Dao<OrmliteYahooDownloaderSettings, Integer> settings;
	private final Dao<OrmliteYahooDownloaderStatistics, Integer> statistics;

	public YahooDownloaderDatabaseStorage(final YahooDownloaderDatabaseSettings databaseSettings) throws IOException, SQLException {
		this.source = new JdbcConnectionSource(databaseSettings.getJdbcUrl(), databaseSettings.getLogin(), databaseSettings.getPassword());
		this.settings = DaoManager.createDao(source, OrmliteYahooDownloaderSettings.class);
		Validate.isTrue(settings.isTableExists(), "OrmliteYahooDatafeedSettings table should exists");
		statistics = DaoManager.createDao(source, OrmliteYahooDownloaderStatistics.class);
		Validate.isTrue(statistics.isTableExists(), "OrmliteYahooDatafeedStatistics table should exists");
	}

	public CreateOrUpdateStatus setSettings(OrmliteYahooDownloaderSettings newCategory) throws SQLException {
		newCategory.setCreatedAt();
		newCategory.setUpdatedAt();
		return settings.createOrUpdate(newCategory);
	}

	public OrmliteYahooDownloaderSettings getSettings(String settingName) throws SQLException {
		return settings.queryForEq(OrmliteYahooDownloaderSettings.settingColumnName, settingName).get(0);
	}

	public CreateOrUpdateStatus setStatistics(OrmliteYahooDownloaderStatistics newCategory) throws SQLException {
		newCategory.setCreatedAt();
		newCategory.setUpdatedAt();
		return statistics.createOrUpdate(newCategory);
	}

	public List<OrmliteYahooDownloaderStatistics> getStatistics(String settingName) throws SQLException {
		return statistics.queryForEq(OrmliteYahooDownloaderStatistics.settingColumnName, settingName);
	}

	public List<OrmliteYahooDownloaderStatistics> getStatistics(String settingName, int processId) throws SQLException {
		return statistics.queryBuilder().where().eq(OrmliteYahooDownloaderStatistics.settingColumnName, settingName).and()
				.eq(OrmliteYahooDownloaderStatistics.processIdColumnName, processId).query();
	}
}