package stsc.algorithms;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.StockAlgorithm;
import stsc.algorithms.StockExecution;
import stsc.testhelper.TestAlgorithmsHelper;
import junit.framework.TestCase;

public class StockExecutionTest extends TestCase {

	public void testStockAlgorithmExecutionConstructor() {
		boolean exception = false;
		try {
			new StockExecution("execution1", "algorithm1", TestAlgorithmsHelper.getSettings());
		} catch (BadAlgorithmException e) {
			exception = true;
		}
		assertTrue(exception);
	}

	public void testExecution() throws BadAlgorithmException {
		final StockExecution e3 = new StockExecution("e1", TestingStockAlgorithm.class.getName(), TestAlgorithmsHelper.getSettings());

		assertEquals(TestingStockAlgorithm.class.getName(), e3.getAlgorithmName());
		assertEquals("e1", e3.getName());

		try {
			final StockAlgorithm.Init init = TestAlgorithmsHelper.getStockAlgorithmInit();
			final StockAlgorithm sai = e3.getInstance(init.stockName, init.signalsStorage);
			assertTrue(sai instanceof TestingStockAlgorithm);
		} catch (BadAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
