package stsc.simulator.multistarter;

import java.util.Arrays;

import junit.framework.TestCase;

public class MultiParameterTests extends TestCase {
	public void testMultiParameterDouble() {
		Double sum = 0.0;
		int count = 0;
		for (Parameter<Double> d : MultiParameterBuilder.Double(0.1, 1.0, 0.1)) {
			sum += d.getValue();
			count += 1;
		}
		assertEquals(5.5, sum);
		assertEquals(10, count);
	}

	public void testMultiParameterInteger() {
		Integer sum = 0;
		int count = 0;
		for (Parameter<Integer> i : MultiParameterBuilder.Integer(-4, 13, 2)) {
			sum += i.getValue();
			count += 1;
		}
		assertEquals(36, sum.intValue());
		assertEquals(9, count);
	}

	public void testMultiParameterString() {
		String sum = "";
		int count = 0;
		for (Parameter<String> s : MultiParameterBuilder.String(Arrays.asList("asd", "xcv"))) {
			sum += s.getValue();
			count += 1;
		}
		assertEquals("asdxcv", sum);
		assertEquals(2, count);
	}

	public void testMultiParameterSubExecution() {
		String sum = "";
		String names = "";
		int count = 0;
		for (Parameter<String> s : MultiParameterBuilder.SubExecution(Arrays.asList("ter", "vlo"))) {
			sum += s.getValue();
			names += s.toString();
			count += 1;
		}
		assertEquals("tervlo", sum);
		assertEquals("se = terse = vlo", names);
		assertEquals(2, count);
	}
}
