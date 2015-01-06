package stsc.news.feedzilla;

import org.junit.Assert;
import org.junit.Test;

public class FeedzillaFileFeedStorageTest {
	@Test
	public void testFeedzillaFileFeedStorage() {
		final FeedzillaFileFeedStorage fffs = new FeedzillaFileFeedStorage();
		Assert.assertNotNull(fffs);
	}
}
