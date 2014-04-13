package stsc.simulator.multistarter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

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
		MultiStockExecution mse = new MultiStockExecution("e", "stsc.algorithms.stock.factors.primitive.Sma", period);
		mse.addParameter(new MpInteger("n", 1, 3, 1));
		mse.addParameter(new MpInteger("m", -4, -1, 2));
		mse.addParameter(new MpString("l", Arrays.asList(new String[] { "asd", "ibm" })));

		final ArrayList<AlgorithmSettings> settings = new ArrayList<>();

		for (StockExecution se : mse.getEntry()) {
			assertNotNull(se);
			assertEquals("stsc.algorithms.stock.factors.primitive.Sma", se.getAlgorithmName());
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

	public void testMultiStockExecutionALotOfParameters() throws ParseException, BadParameterException,
			BadAlgorithmException {
		final FromToPeriod period = new FromToPeriod("01-01-2000", "31-12-2009");
		MultiStockExecution mse = new MultiStockExecution("e", "stsc.algorithms.stock.factors.primitive.Sma", period);
		mse.addParameter(new MpInteger("q", 0, 5, 1));
		mse.addParameter(new MpInteger("w", -4, 1, 1));
		mse.addParameter(new MpDouble("a", 0.0, 100.0, 7.0));
		mse.addParameter(new MpDouble("s", -100.0, 101.0, 25.0));
		mse.addParameter(new MpString("z", Arrays.asList(new String[] { "asd", "ibm", "yhoo" })));
		mse.addParameter(new MpString("z", Arrays.asList(new String[] { "vokrug", "fileName" })));
		mse.addParameter(new MpSubExecution("p", Arrays.asList(new String[] { "12313-432423", "234535-23424",
				"35345-234234135", "24454-65462245" })));

		final ArrayList<AlgorithmSettings> settings = new ArrayList<>();

		Iterator<StockExecution> i = mse.getEntry().iterator();
		int sum = 0;
		while (i.hasNext()) {
			i.next();
			sum += 1;
		}
		assertEquals(5 * 5 * 15 * 9 * 3 * 2 * 4, sum);

		mse.reset();

		for (StockExecution se : mse.getEntry()) {
			assertNotNull(se);
			assertEquals("stsc.algorithms.stock.factors.primitive.Sma", se.getAlgorithmName());
			settings.add(se.getSettings());
		}
		assertEquals(5 * 5 * 15 * 9 * 3 * 2 * 4, settings.size());
	}
}
