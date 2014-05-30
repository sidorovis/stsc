package stsc.general.simulator.multistarter;

import java.util.Arrays;

import stsc.common.Settings;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.MpDouble;
import stsc.general.simulator.multistarter.MpInteger;
import stsc.general.simulator.multistarter.MpString;
import stsc.general.simulator.multistarter.MpSubExecution;
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
		assertEquals(4.5, sum, Settings.doubleEpsilon);
		assertEquals(9, count);

		assertEquals(0.4, mpDouble.parameter(3));
		assertEquals(0.9, mpDouble.parameter(8));
		assertEquals(1.1, mpDouble.parameter(10));
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

		assertEquals(2, mpInteger.parameter(3).intValue());
		assertEquals(6, mpInteger.parameter(5).intValue());
		assertEquals(14, mpInteger.parameter(9).intValue());
	}

	public void testMultiParameterString() throws BadParameterException {
		String sum = "";
		int count = 0;
		final MpString mpString = new MpString("str", Arrays.asList("asd", "xcv"));
		while (mpString.hasNext()) {
			sum += mpString.current();
			count += 1;
			sum += mpString.next();
		}
		assertEquals("asdasdxcvxcv", sum);
		assertEquals(2, count);

		assertEquals("xcv", mpString.parameter(1));
		assertEquals("asd", mpString.parameter(0));
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

		assertEquals("ter", exe.parameter(0));
		assertEquals("vlo", exe.parameter(1));
	}
}
