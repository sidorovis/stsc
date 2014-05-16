package stsc.algorithms;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodExecution;
import stsc.algorithms.eod.primitive.TestingEodAlgorithm;
import stsc.testhelper.TestHelper;
import junit.framework.TestCase;

public class EodAlgorithmExecutionTest extends TestCase {
	public void testEodAlgorithmExecutionConstructor() {
		boolean exception = false;
		try {
			new EodExecution("execution1", "algorithm1", TestHelper.getAlgorithmSettings());
		} catch (BadAlgorithmException e) {
			exception = true;
		}
		assertTrue(exception);
	}

	public void testNameInstallingMethod() throws BadAlgorithmException {
		final EodExecution eae = new EodExecution("e1", "stsc.algorithms.eod.primitive.TestingEodAlgorithm",
				TestHelper.getAlgorithmSettings());
		assertEquals("stsc.algorithms.eod.primitive.TestingEodAlgorithm", eae.getAlgorithmName());
	}

	public void testExecution() throws BadAlgorithmException {
		EodExecution e3 = new EodExecution("e1", TestingEodAlgorithm.class.getName(), TestHelper.getAlgorithmSettings());
		assertEquals(TestingEodAlgorithm.class.getName(), e3.getAlgorithmName());
		assertEquals("e1", e3.getName());
	}
}
