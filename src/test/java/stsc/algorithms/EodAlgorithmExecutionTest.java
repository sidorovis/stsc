package stsc.algorithms;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodExecution;
import stsc.algorithms.primitive.TestingEodAlgorithm;
import junit.framework.TestCase;

public class EodAlgorithmExecutionTest extends TestCase {
	public void testEodAlgorithmExecutionConstructor() {
		boolean exception = false;
		try {
			new EodExecution("execution1", "algorithm1", AlgorithmSettings.create00s());
		} catch (BadAlgorithmException e) {
			exception = true;
		}
		assertTrue(exception);
	}

	public void testNameInstallingMethod() throws BadAlgorithmException {
		final EodExecution eae = new EodExecution("e1", "stsc.algorithms.primitive.TestingEodAlgorithm",
				AlgorithmSettings.create00s());
		assertEquals("stsc.algorithms.primitive.TestingEodAlgorithm", eae.getAlgorithmName());
	}

	public void testExecution() throws BadAlgorithmException {
		EodExecution e3 = new EodExecution("e1", TestingEodAlgorithm.class.getName(), AlgorithmSettings.create00s());
		assertEquals(TestingEodAlgorithm.class.getName(), e3.getAlgorithmName());
		assertEquals("e1", e3.getName());
	}
}
