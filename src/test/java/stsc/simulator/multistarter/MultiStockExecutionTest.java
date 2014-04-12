package stsc.simulator.multistarter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.StockExecution;
import stsc.common.FromToPeriod;
import junit.framework.TestCase;

public class MultiStockExecutionTest extends TestCase {

	private void testHelperNlmParameters(String n, String l, String m, AlgorithmSettings s) {
		assertEquals(n, s.get("n"));
		assertEquals(l, s.get("l"));
		assertEquals(m, s.get("m"));
	}

	public void testMultiStockExecution() throws ParseException, BadParameterException, BadAlgorithmException {
		final FromToPeriod period = new FromToPeriod("01-01-2000", "31-12-2009");
		MultiStockExecution mse = new MultiStockExecution("e", "stsc.algorithms.factors.primitive.Sma", period);
		mse.addIntegerParameter(new MpInteger("n", 1, 3, 1));
		mse.addIntegerParameter(new MpInteger("m", -4, -1, 2));
		mse.addStringParameter(new MpString("l", Arrays.asList(new String[] { "asd", "ibm" })));

		final ArrayList<AlgorithmSettings> settings = new ArrayList<>();

		for (StockExecution se : mse.getEntry()) {
			assertNotNull(se);
			assertEquals("stsc.algorithms.factors.primitive.Sma", se.getAlgorithmName());
			settings.add(se.getSettings());
		}
		assertEquals(8, settings.size());
		testHelperNlmParameters("1", "asd", "-4", settings.get(0));
		testHelperNlmParameters("2", "asd", "-4", settings.get(1));
		testHelperNlmParameters("1", "asd", "-2", settings.get(2));
		testHelperNlmParameters("2", "asd", "-2", settings.get(3));
		testHelperNlmParameters("1", "ibm", "-4", settings.get(4));
		testHelperNlmParameters("2", "ibm", "-4", settings.get(5));
		testHelperNlmParameters("1", "ibm", "-2", settings.get(6));
		testHelperNlmParameters("2", "ibm", "-2", settings.get(7));
	}
}
