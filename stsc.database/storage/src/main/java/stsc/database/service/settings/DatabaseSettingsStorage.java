package stsc.database.service.settings;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.lang3.Validate;

import stsc.database.migrations.DatabaseSettings;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.Dao.CreateOrUpdateStatus;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class DatabaseSettingsStorage {

	private final ConnectionSource source;

	private final Dao<OrmliteYahooDatafeedSettings, Integer> yahooDatafeedSettings;

	public DatabaseSettingsStorage(final DatabaseSettings databaseSettings) throws IOException, SQLException {
		this.source = getConnectionSource(databaseSettings);
		this.yahooDatafeedSettings = DaoManager.createDao(source, OrmliteYahooDatafeedSettings.class);
		Validate.isTrue(yahooDatafeedSettings.isTableExists(), "OrmliteYahooDatafeedSettings table should exists");
	}

	public ConnectionSource getConnectionSource(final DatabaseSettings databaseSettings) throws IOException, SQLException {
		return new JdbcConnectionSource(databaseSettings.getJdbcUrl());
	}

	public CreateOrUpdateStatus setYahooDatafeedSettings(OrmliteYahooDatafeedSettings newCategory) throws SQLException {
		newCategory.setCreatedAt();
		newCategory.setUpdatedAt();
		return yahooDatafeedSettings.createOrUpdate(newCategory);
	}

	public OrmliteYahooDatafeedSettings getYahooDatafeedSettings(String settingName) throws SQLException {
		return yahooDatafeedSettings.queryForEq(OrmliteYahooDatafeedSettings.settingColumnName, settingName).get(0);
	}

}
