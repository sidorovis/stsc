package stsc.storage;

import java.io.IOException;

import junit.framework.TestCase;

public class AlgorithmNamesStorageTest extends TestCase {
	public void testAlgorithmNamesStorage() throws ClassNotFoundException, IOException {
		AlgorithmNamesStorage ans = new AlgorithmNamesStorage();
		assertNotNull(ans.getStock("Sma"));
		assertNotNull(ans.getStock("Ema"));
		assertNull(ans.getEod("TestingEodAlgorithm"));
		assertNull(ans.getStock("StockAlgorithmHelper"));
		assertNotNull(ans.getEod("SimpleTradingAlgorithm"));
	}
}
