package stsc.trading;

import stsc.algorithms.TestEodAlgorithm;
import junit.framework.TestCase;

public class ExecutionTest extends TestCase {
	public void testExecution() {
		{
			new Execution("execution1", "algorithm1");
		}
		Execution e2 = new Execution("e1", "stsc.algorithms.TestAlgorithm");
		assertEquals("stsc.algorithms.TestAlgorithm", e2.algorithmName);
		
		Execution e3 = new Execution("e1", TestEodAlgorithm.class.getName());
		assertEquals("stsc.algorithms.TestEodAlgorithm", e3.algorithmName);
		
	}
}
