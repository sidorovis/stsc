package stsc.trading;

import stsc.algorithms.TestAlgorithm;
import junit.framework.TestCase;

public class ExecutionTest extends TestCase {
	public void testExecution() {
		{
			new Execution("execution1", "algorithm1");
		}
		Execution e2 = new Execution("e1", "stsc.algorithms.TestAlgorithm");
		assertEquals("stsc.algorithms.TestAlgorithm", e2.algorithmName);
		
		Execution e3 = new Execution("e1", TestAlgorithm.class.getName());
		assertEquals("stsc.algorithms.TestAlgorithm", e3.algorithmName);
		
	}
}
