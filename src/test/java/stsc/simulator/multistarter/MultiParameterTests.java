package stsc.simulator.multistarter;

import java.util.Arrays;

import junit.framework.TestCase;

public class MultiParameterTests extends TestCase {
	public void testMultiParameterDouble() {
		Double sum = 0.0;
		int count = 0;
		for (Double d : MultiParameters.Double(0.1, 1.0, 0.1)) {
			sum += d;
			count += 1;
		}
		assertEquals(5.5, sum);
		assertEquals(10, count);
	}

	public void testMultiParameterInteger() {
		Integer sum = 0;
		int count = 0;
		for (Integer i : MultiParameters.Integer(-4, 13, 2)) {
			sum += i;
			count += 1;
		}
		assertEquals(36, sum.intValue());
		assertEquals(9, count);
	}

	public void testMultiParameterString() {
		String sum = "";
		int count = 0;
		for (String s : MultiParameters.String(Arrays.asList("asd", "xcv"))) {
			sum += s;
			count += 1;
		}
		assertEquals("asdxcv", sum);
		assertEquals(2, count);
	}

	public void testMultiParameterSubExecution() {
		String sum = "";
		int count = 0;
		for (String s : MultiParameters.SubExecution(Arrays.asList("ter", "vlo"))) {
			sum += s;
			count += 1;
		}
		assertEquals("tervlo", sum);
		assertEquals(2, count);
	}
}
