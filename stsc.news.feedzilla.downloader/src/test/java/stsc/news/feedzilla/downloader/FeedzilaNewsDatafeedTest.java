package stsc.news.feedzilla.downloader;

import org.junit.Assert;
import org.junit.Test;

import stsc.news.feedzilla.downloader.FeedDataDownloder;

public class FeedzilaNewsDatafeedTest {

	@Test
	public void testFeedzilaNewsDatafeed() {
		FeedDataDownloder fz = new FeedDataDownloder();
		Assert.assertNotNull(fz);
	}
}

