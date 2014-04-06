package stsc.algorithms;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.StockAlgorithm;
import stsc.algorithms.StockExecution;
import stsc.algorithms.primitive.TestingStockAlgorithm;
import stsc.testhelper.TestHelper;
import junit.framework.TestCase;

public class StockExecutionTest extends TestCase {

	public void testStockAlgorithmExecutionConstructor() {
		boolean exception = false;
		try {
			new StockExecution("execution1", "algorithm1", AlgorithmSettings.create00s());
		} catch (BadAlgorithmException e) {
			exception = true;
		}
		assertTrue(exception);
	}

	public void testExecution() throws BadAlgorithmException {
		final StockExecution e3 = new StockExecution("e1", TestingStockAlgorithm.class.getName(),
				AlgorithmSettings.create00s());

		assertEquals(TestingStockAlgorithm.class.getName(), e3.getAlgorithmName());
		assertEquals("e1", e3.getName());

		try {
			final StockAlgorithm.Init init = TestHelper.getStockAlgorithmInit();
			final StockAlgorithm sai = e3.getInstance(init.stockName, init.signalsStorage);
			assertTrue(sai instanceof TestingStockAlgorithm);
		} catch (BadAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
