package stsc.news.feedzilla;

import java.io.IOException;

import liquibase.exception.LiquibaseException;

import org.junit.Test;

public class FeedZillaMigrationTestHelperTest {

	@Test
	public void testFeedZillaMigrationTestHelper() throws IOException, LiquibaseException {
		FeedZillaMigrationTestHelper.dropAll();
		FeedZillaMigrationTestHelper.migrate();
	}

}
