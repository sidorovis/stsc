package stsc.storage;

import java.io.IOException;

import junit.framework.TestCase;

public class AlgorithmsStorageTest extends TestCase {
	public void testAlgorithmNamesStorage() throws ClassNotFoundException, IOException {
		AlgorithmsStorage ans = new AlgorithmsStorage();
		assertNotNull(ans.getStock("Sma"));
		assertNotNull(ans.getStock("Ema"));
		assertNull(ans.getEod("TestingEodAlgorithm"));
		assertNull(ans.getStock("StockAlgorithmHelper"));
		assertNotNull(ans.getEod("SimpleTradingAlgorithm"));
	}
}
