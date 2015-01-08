package stsc.news.feedzilla;

import org.junit.Assert;
import org.junit.Test;

public class FeedzillaFileStorageTest {
	@Test
	public void testFeedzillaFileFeedStorage() {
		final FeedzillaFileStorage fffs = new FeedzillaFileStorage("./test_data");
		Assert.assertNotNull(fffs);
	}
}
