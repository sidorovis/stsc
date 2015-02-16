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

public final class DatabaseSettings {

	public final static String dbChangeLog = "../config/db.changelog.xml";

	private final String jdbcDriver;
	private final String jdbcUrl;
	private final String login;
	private final String password;

	public static DatabaseSettings development() throws IOException {
		return new DatabaseSettings("../config/feedzilla_development.properties");
	}

	public static DatabaseSettings test() throws IOException {
		return new DatabaseSettings("../config/feedzilla_test.properties");
	}

	public DatabaseSettings(final String filePath) throws IOException {
		this(new FileInputStream(filePath));
	}

	private DatabaseSettings(InputStream sourceInputStream) throws IOException {
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

	public DatabaseSettings migrate() throws SQLException, LiquibaseException {
		final Connection c = DriverManager.getConnection(jdbcUrl);
		final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(c));
		final String path = dbChangeLog;
		final File parentPath = new File(path).getParentFile();
		final Liquibase liquibase = new Liquibase(path, new FileSystemResourceAccessor(parentPath.getAbsolutePath()), database);
		liquibase.update((String) null);
		liquibase.validate();
		database.commit();
		c.commit();
		c.close();
		return this;
	}

	public DatabaseSettings dropAll() throws SQLException, LiquibaseException {
		final Connection c = DriverManager.getConnection(jdbcUrl);
		final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(c));
		final String path = dbChangeLog;
		final File parentPath = new File(path).getParentFile();
		final Liquibase liquibase = new Liquibase(path, new FileSystemResourceAccessor(parentPath.getAbsolutePath()), database);
		liquibase.dropAll();
		liquibase.validate();
		database.commit();
		c.commit();
		c.close();
		return this;
	}

}
