package stsc.news.feedzilla;

import org.junit.Assert;
import org.junit.Test;

public class FeedzilaNewsDatafeedTest {

	@Test
	public void testFeedzilaNewsDatafeed() {
		FeedzilaNewsDatafeed fz = new FeedzilaNewsDatafeed();
		Assert.assertNotNull(fz);
	}
}

