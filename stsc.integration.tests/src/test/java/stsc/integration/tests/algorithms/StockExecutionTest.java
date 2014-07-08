package stsc.integration.tests.algorithms;

import java.text.ParseException;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockExecution;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.integration.tests.helper.TestAlgorithmsHelper;
import junit.framework.TestCase;

public final class StockExecutionTest extends TestCase {

	public void testStockAlgorithmExecutionConstructor() {
		boolean exception = false;
		try {
			new StockExecution("execution1", "algorithm1", TestAlgorithmsHelper.getSettings());
		} catch (BadAlgorithmException e) {
			exception = true;
		}
		assertTrue(exception);
	}

	public void testExecution() throws BadAlgorithmException, ParseException {
		final StockExecution e3 = new StockExecution("e1", TestingStockAlgorithm.class.getName(), TestAlgorithmsHelper.getSettings());

		assertEquals(TestingStockAlgorithm.class.getName(), e3.getAlgorithmName());
		assertEquals("e1", e3.getExecutionName());

		try {
			StockAlgoInitHelper init = new StockAlgoInitHelper("e1", "aapl");
			final StockAlgorithm sai = e3.getInstance("e1", init.getStorage());
			assertTrue(sai instanceof TestingStockAlgorithm);
		} catch (BadAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
