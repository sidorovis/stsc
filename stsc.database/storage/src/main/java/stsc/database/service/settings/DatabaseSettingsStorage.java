package stsc.database.service.settings;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.Validate;

import stsc.database.migrations.DatabaseSettings;
import stsc.database.service.statistics.OrmliteYahooDownloaderStatistics;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class DatabaseSettingsStorage {

	private final ConnectionSource source;

	private final Dao<OrmliteYahooDownloaderSettings, Integer> yahooDatafeedSettings;
	private final Dao<OrmliteYahooDownloaderStatistics, Integer> yahooDatafeedStatistics;

	public DatabaseSettingsStorage(final DatabaseSettings databaseSettings) throws IOException, SQLException {
		this.source = new JdbcConnectionSource(databaseSettings.getJdbcUrl(), databaseSettings.getLogin(), databaseSettings.getPassword());
		this.yahooDatafeedSettings = DaoManager.createDao(source, OrmliteYahooDownloaderSettings.class);
		Validate.isTrue(yahooDatafeedSettings.isTableExists(), "OrmliteYahooDatafeedSettings table should exists");
		yahooDatafeedStatistics = DaoManager.createDao(source, OrmliteYahooDownloaderStatistics.class);
	}

	public CreateOrUpdateStatus setYahooDatafeedSettings(OrmliteYahooDownloaderSettings newCategory) throws SQLException {
		newCategory.setCreatedAt();
		newCategory.setUpdatedAt();
		return yahooDatafeedSettings.createOrUpdate(newCategory);
	}

	public OrmliteYahooDownloaderSettings getYahooDatafeedSettings(String settingName) throws SQLException {
		return yahooDatafeedSettings.queryForEq(OrmliteYahooDownloaderSettings.settingColumnName, settingName).get(0);
	}

	public CreateOrUpdateStatus setYahooDatafeedStatistics(OrmliteYahooDownloaderStatistics newCategory) throws SQLException {
		newCategory.setCreatedAt();
		newCategory.setUpdatedAt();
		return yahooDatafeedStatistics.createOrUpdate(newCategory);
	}

	public List<OrmliteYahooDownloaderStatistics> getYahooDatafeedStatistics(String settingName) throws SQLException {
		return yahooDatafeedStatistics.queryForEq(OrmliteYahooDownloaderStatistics.settingColumnName, settingName);
	}

	public List<OrmliteYahooDownloaderStatistics> getYahooDatafeedStatistics(String settingName, int processId) throws SQLException {
		return yahooDatafeedStatistics.queryBuilder().where().eq(OrmliteYahooDownloaderStatistics.settingColumnName, settingName).and()
				.eq(OrmliteYahooDownloaderStatistics.processIdColumnName, processId).query();
	}
}