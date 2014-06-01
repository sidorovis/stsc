package stsc.storage;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.storage.AlgorithmsStorage;
import junit.framework.TestCase;

public class AlgorithmsStorageTest extends TestCase {
	public void testAlgorithmNamesStorage() throws BadAlgorithmException {
		AlgorithmsStorage ans = AlgorithmsStorage.getInstance();
		assertNotNull(ans.getStock("Sma"));
		assertNotNull(ans.getStock("Ema"));
		assertNull(ans.getEod("TestingEodAlgorithm"));
		assertNull(ans.getStock("StockAlgorithmHelper"));
		assertNotNull(ans.getEod("SimpleTradingAlgorithm"));
	}
}
