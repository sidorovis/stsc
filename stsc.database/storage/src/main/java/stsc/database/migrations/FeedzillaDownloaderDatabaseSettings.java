package stsc.database.migrations;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;

public final class FeedzillaDownloaderDatabaseSettings {

	public final static String configFolder = "../config/feedzilla_downloader/";
	public final static String dbChangeLog = "../migrations/target/classes/feedzilla_downloader/";
	public final static String dbChangeLogFile = "db.changelog.xml";

	private final String jdbcDriver;
	private final String jdbcUrl;
	private final String login;
	private final String password;

	public static FeedzillaDownloaderDatabaseSettings development() throws IOException {
		return new FeedzillaDownloaderDatabaseSettings(configFolder + "development.properties");
	}

	public static FeedzillaDownloaderDatabaseSettings test() throws IOException {
		return new FeedzillaDownloaderDatabaseSettings(configFolder + "test.properties");
	}

	public FeedzillaDownloaderDatabaseSettings(final String filePath) throws IOException {
		this(new FileInputStream(filePath));
	}

	private FeedzillaDownloaderDatabaseSettings(InputStream sourceInputStream) throws IOException {
		try (DataInputStream inputStream = new DataInputStream(sourceInputStream)) {
			final Properties properties = new Properties();
			properties.load(inputStream);
			jdbcDriver = properties.getProperty("jdbc.driver");
			jdbcUrl = properties.getProperty("jdbc.url");
			login = properties.getProperty("jdbc.login");
			password = properties.getProperty("jdbc.password");
		}

	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public String getJdbcDriver() {
		return jdbcDriver;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public FeedzillaDownloaderDatabaseSettings migrate() throws SQLException, LiquibaseException {
		final Connection c = DriverManager.getConnection(jdbcUrl);
		final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(c));
		final File parentPath = new File(dbChangeLog);
		final Liquibase liquibase = new Liquibase(dbChangeLogFile, new FileSystemResourceAccessor(parentPath.getAbsolutePath()), database);
		liquibase.update((String) null);
		liquibase.validate();
		database.commit();
		c.commit();
		c.close();
		return this;
	}

	public FeedzillaDownloaderDatabaseSettings dropAll() throws SQLException, LiquibaseException {
		final Connection c = DriverManager.getConnection(jdbcUrl);
		final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(c));
		final File parentPath = new File(dbChangeLog);
		final Liquibase liquibase = new Liquibase(dbChangeLogFile, new FileSystemResourceAccessor(parentPath.getAbsolutePath()), database);
		liquibase.dropAll();
		liquibase.validate();
		database.commit();
		c.commit();
		c.close();
		return this;
	}

}
