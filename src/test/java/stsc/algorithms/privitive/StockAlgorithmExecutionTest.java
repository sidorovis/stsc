package stsc.algorithms.privitive;

import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.StockAlgorithm;
import stsc.algorithms.StockAlgorithmExecution;
import stsc.algorithms.primitive.TestingStockAlgorithm;
import stsc.storage.SignalsStorage;
import junit.framework.TestCase;

public class StockAlgorithmExecutionTest extends TestCase {

	public void testStockAlgorithmExecutionConstructor() {
		boolean exception = false;
		try{
			new StockAlgorithmExecution("execution1", "algorithm1");
		} catch( BadAlgorithmException e ){
			exception = true;
		}
		assertTrue(exception);
	}

	public void testExecution() throws BadAlgorithmException {
		final StockAlgorithmExecution e3 = new StockAlgorithmExecution("e1", TestingStockAlgorithm.class.getName());

		assertEquals(TestingStockAlgorithm.class.getName(), e3.getAlgorithmName());
		assertEquals("e1", e3.getName());

		final SignalsStorage storage = new SignalsStorage();
		final AlgorithmSettings settings = new AlgorithmSettings();

		try {
			final StockAlgorithm sai = e3.getInstance(storage, settings);
			assertTrue(sai instanceof TestingStockAlgorithm);
		} catch (BadAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
