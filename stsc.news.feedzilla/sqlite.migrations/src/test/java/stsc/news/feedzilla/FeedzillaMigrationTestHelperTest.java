package stsc.news.feedzilla;

import java.io.IOException;

import liquibase.exception.LiquibaseException;

import org.junit.Test;

public class FeedzillaMigrationTestHelperTest {

	@Test
	public void testFeedzillaMigrationTestHelper() throws IOException, LiquibaseException {
		FeedzillaMigrationTestHelper.dropAll();
		FeedzillaMigrationTestHelper.migrate();
	}

}
