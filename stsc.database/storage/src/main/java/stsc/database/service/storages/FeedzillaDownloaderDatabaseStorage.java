package stsc.database.service.storages;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang3.Validate;

import stsc.database.migrations.FeedzillaDownloaderDatabaseSettings;
import stsc.database.service.schemas.OrmliteFeedzillaDownloaderSettings;
import stsc.database.service.schemas.OrmliteFeedzillaDownloaderStatistics;
import stsc.database.service.schemas.OrmliteYahooDownloaderStatistics;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class FeedzillaDownloaderDatabaseStorage {

	private final ConnectionSource source;

	private final Dao<OrmliteFeedzillaDownloaderSettings, Integer> settings;
	private final Dao<OrmliteFeedzillaDownloaderStatistics, Integer> statistics;

	public FeedzillaDownloaderDatabaseStorage(final FeedzillaDownloaderDatabaseSettings databaseSettings) throws IOException, SQLException {
		this.source = new JdbcConnectionSource(databaseSettings.getJdbcUrl(), databaseSettings.getLogin(), databaseSettings.getPassword());
		this.settings = DaoManager.createDao(source, OrmliteFeedzillaDownloaderSettings.class);
		Validate.isTrue(settings.isTableExists(), "OrmliteFeedzillaDownloaderSettings table should exists");
		statistics = DaoManager.createDao(source, OrmliteFeedzillaDownloaderStatistics.class);
		Validate.isTrue(statistics.isTableExists(), "OrmliteFeedzillaDownloaderStatistics table should exists");
	}

	public CreateOrUpdateStatus setSettings(OrmliteFeedzillaDownloaderSettings newCategory) throws SQLException {
		newCategory.setCreatedAt();
		newCategory.setUpdatedAt();
		return settings.createOrUpdate(newCategory);
	}

	public OrmliteFeedzillaDownloaderSettings getSettings(String settingName) throws SQLException {
		return settings.queryForEq(OrmliteFeedzillaDownloaderSettings.settingColumnName, settingName).get(0);
	}

	public CreateOrUpdateStatus setStatistics(OrmliteFeedzillaDownloaderStatistics newCategory) throws SQLException {
		newCategory.setCreatedAt();
		newCategory.setUpdatedAt();
		return statistics.createOrUpdate(newCategory);
	}

	public List<OrmliteFeedzillaDownloaderStatistics> getStatistics(String settingName) throws SQLException {
		return statistics.queryForEq(OrmliteFeedzillaDownloaderStatistics.settingColumnName, settingName);
	}

	public List<OrmliteFeedzillaDownloaderStatistics> getStatistics(String settingName, int processId) throws SQLException {
		return statistics.queryBuilder().where().eq(OrmliteYahooDownloaderStatistics.settingColumnName, settingName).and()
				.eq(OrmliteYahooDownloaderStatistics.processIdColumnName, processId).query();
	}
}