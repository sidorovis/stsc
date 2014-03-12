package stsc.algorithms.privitive;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.StockAlgorithmExecution;
import stsc.algorithms.StockAlgorithmInterface;
import stsc.algorithms.primitive.TestingStockAlgorithm;
import stsc.storage.SignalsStorage;
import junit.framework.TestCase;

public class StockAlgorithmExecutionTest extends TestCase {

	public void testStockAlgorithmExecutionConstructor() {
		new StockAlgorithmExecution("execution1", "algorithm1");
	}

	public void testExecution() throws BadAlgorithmException {
		StockAlgorithmExecution e3 = new StockAlgorithmExecution("e1", TestingStockAlgorithm.class.getName());
		assertEquals(TestingStockAlgorithm.class.getName(), e3.getAlgorithmName());
		assertEquals("e1", e3.getName());
		
		SignalsStorage signalsStorage = new SignalsStorage();
		
		StockAlgorithmInterface sai = e3.getInstance(signalsStorage);
		assertTrue( sai instanceof TestingStockAlgorithm );
	}
}
