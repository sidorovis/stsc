package stsc.testhelper;

import junit.framework.TestCase;

public class TestHelperTest extends TestCase {
	public void testTestHelper() {
		assertNotNull(TestHelper.getEodAlgorithmInit());
	}
}
