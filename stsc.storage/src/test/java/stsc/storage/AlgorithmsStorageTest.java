package stsc.storage;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.storage.AlgorithmsStorage;

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
			Assert.fail("For 'IN' we could assume:stsc.algorithms.stock.indices.adx.AverageDirectionalMovement or stsc.algorithms.stock.indices.adx.AdxTrueRange");
		} catch (BadAlgorithmException e) {
			Assert.assertEquals(
					"For 'IN' we could assume:stsc.algorithms.stock.indices.adx.AdxSmaDi or stsc.algorithms.stock.indices.mfi.MfiMoneyFlow",
					e.getMessage());
		}
	}
}
