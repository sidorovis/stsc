package stsc.news.feedzilla;

import java.io.IOException;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;

class FeedzillaMigrationTestHelper {

	private static final FeedzillaDatafeedSettings settings;

	static {
		settings = getFeedzillaDatafeedSettings();
	}

	private FeedzillaMigrationTestHelper() {
	}

	private static FeedzillaDatafeedSettings getFeedzillaDatafeedSettings() {
		try {
			return new FeedzillaDatafeedSettings("feedzilla_test.properties");
		} catch (IOException e) {
			// do nothing this is for tests only
		}
		return null;
	}

	public static void migrate() throws IOException, LiquibaseException {
		final String filePath = FeedzillaMigrationTestHelper.class.getResource("../../../liquibase/db.changelog.xml").getPath();
		final Database database = DatabaseFactory.getInstance().openDatabase(settings.getJdbcUrl(), "", "",
				new FileSystemResourceAccessor());
		final Liquibase liquibase = new Liquibase(filePath, new FileSystemResourceAccessor(), database);
		liquibase.update("");
	}

	public static void dropAll() throws IOException, LiquibaseException {
		final String filePath = FeedzillaMigrationTestHelper.class.getResource("../../../liquibase/db.changelog.xml").getPath();
		final Database database = DatabaseFactory.getInstance().openDatabase(settings.getJdbcUrl(), "", "",
				new FileSystemResourceAccessor());
		final Liquibase liquibase = new Liquibase(filePath, new FileSystemResourceAccessor(), database);
		liquibase.rollback(3, "");
	}
}
