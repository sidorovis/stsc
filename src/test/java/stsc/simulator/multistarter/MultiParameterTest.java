package stsc.simulator.multistarter;

import java.util.Arrays;

import junit.framework.TestCase;

public class MultiParameterTest extends TestCase {
	public void testMultiParameterDouble() throws BadParameterException {
		Double sum = 0.0;
		int count = 0;
		final MpDouble mpDouble = new MpDouble("double", 0.1, 1.0, 0.1);
		while (mpDouble.hasNext()) {
			sum += mpDouble.currentParameter().getValue();
			count += 1;
			mpDouble.increment();
		}
		assertEquals(4.5, sum, 0.000001);
		assertEquals(9, count);
	}

	public void testMultiParameterInteger() throws BadParameterException {
		Integer sum = 0;
		int count = 0;
		final MpInteger mpInteger = new MpInteger("int", -4, 13, 2);
		while (mpInteger.hasNext()) {
			sum += mpInteger.current();
			count += 1;
			mpInteger.increment();
		}
		assertEquals(36, sum.intValue());
		assertEquals(9, count);
	}

	public void testMultiParameterString() throws BadParameterException {
		String sum = "";
		int count = 0;
		final MpString mpString = new MpString("str", Arrays.asList("asd", "xcv"));
		while (mpString.hasNext()) {
			sum += mpString.current();
			count += 1;
			mpString.next();
		}
		assertEquals("asdxcv", sum);
		assertEquals(2, count);
	}

	public void testMultiParameterSubExecution() throws BadParameterException {
		String sum = "";
		String names = "";
		int count = 0;
		final MpSubExecution exe = new MpSubExecution("se", Arrays.asList("ter", "vlo"));
		while (exe.hasNext()) {
			sum += exe.current();
			names += exe.currentParameter().toString();
			count += 1;
			exe.increment();
		}
		assertEquals("tervlo", sum);
		assertEquals("se = terse = vlo", names);
		assertEquals(2, count);
	}
}
