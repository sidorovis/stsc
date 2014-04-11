package stsc.simulator.multistarter;

import java.text.ParseException;

import stsc.algorithms.StockExecution;
import stsc.common.FromToPeriod;
import junit.framework.TestCase;

public class MultiStockExecutionTest extends TestCase {
	public void testMultiStockExecution() throws ParseException, BadParameterException {
		final FromToPeriod period = new FromToPeriod("01-01-2000", "31-12-2009");
		MultiStockExecution mse = new MultiStockExecution("e", "Sma", period);
		mse.addIntegerParameter(new MpInteger("n", 1, 5, 1));
		mse.addIntegerParameter(new MpInteger("m", -4, -2, 2));
		mse.addIntegerParameter(new MpInteger("l", 10, 16, 4));

	//	for (StockExecution se : mse.getEntry()) {
			// assertNotNull(se);
	//	}

		// MultiStockExecution mse = new
		// MultiStockExecution(StockAlgorithm.class)
	}
}
