package stsc.integration.tests.algorithms;

import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.algorithms.StockExecution;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.integration.tests.helper.TestAlgorithmsHelper;

public final class StockExecutionTest {

	@Test
	public void testStockAlgorithmExecutionConstructor() {
		boolean exception = false;
		try {
			new StockExecution("execution1", "algorithm1", TestAlgorithmsHelper.getSettings());
		} catch (BadAlgorithmException e) {
			exception = true;
		}
		Assert.assertTrue(exception);
	}

	@Test
	public void testExecution() throws BadAlgorithmException, ParseException {
		final StockExecution e3 = new StockExecution("e1", TestingStockAlgorithm.class.getName(), TestAlgorithmsHelper.getSettings());
		Assert.assertEquals(TestingStockAlgorithm.class.getName(), e3.getAlgorithmName());
		Assert.assertEquals("e1", e3.getExecutionName());

		try {
			StockAlgoInitHelper init = new StockAlgoInitHelper("e1", "aapl");
			final StockAlgorithm sai = e3.getInstance("e1", init.getStorage());
			Assert.assertTrue(sai instanceof TestingStockAlgorithm);
		} catch (BadAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
