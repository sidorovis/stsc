package stsc.storage;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.algorithms.BadAlgorithmException;

public class AlgorithmsStorageTest {

	@Test
	public void testAlgorithmNamesStorage() throws BadAlgorithmException {

		final AlgorithmsStorage ans = AlgorithmsStorage.getInstance();
		Assert.assertNotNull(ans.getStock(".Sma"));
		Assert.assertNotNull(ans.getStock(".Ema"));
		Assert.assertNull(ans.getEod("TestingEodAlgorithm"));
		Assert.assertNull(ans.getStock("StockAlgorithmHelper"));
		Assert.assertNotNull(ans.getEod("SimpleTradingAlgorithm"));

		try {
			ans.getStock("IN");
			Assert.fail("For 'IN' we could assume to find several algorithms.");
		} catch (BadAlgorithmException e) {
			Assert.assertTrue(Pattern.matches(
					"For 'IN' we could assume:stsc.algorithms.stock.indices.(.+) or stsc.algorithms.stock.indices.(.+)", e.getMessage()));
		}
	}

	@Test
	public void testAlgorithmsEquityTest() throws BadAlgorithmException {
		final AlgorithmsStorage ans = AlgorithmsStorage.getInstance();
		Assert.assertNotNull(ans.getEod(".StockMarketCycleEquity"));
	}
}
