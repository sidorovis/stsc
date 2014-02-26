package stsc.trading;

import stsc.algorithms.EodAlgorithmExecution;
import stsc.algorithms.TestingEodAlgorithm;
import junit.framework.TestCase;

public class EodAlgorithmExecutionTest extends TestCase {
	public void testEodAlgorithmExecutionConstructor() {
		new EodAlgorithmExecution("execution1", "algorithm1");
	}

	public void testNameInstallingMethod() {
		EodAlgorithmExecution eae = new EodAlgorithmExecution("e1", "stsc.algorithms.TestAlgorithm");
		assertEquals("stsc.algorithms.TestAlgorithm", eae.getAlgorithmName());
	}

	public void testExecution() {

		EodAlgorithmExecution e3 = new EodAlgorithmExecution("e1", TestingEodAlgorithm.class.getName());
		assertEquals("stsc.algorithms.TestEodAlgorithm", e3.getAlgorithmName());
		assertEquals("e1", e3.getName());

	}
}
