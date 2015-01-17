package stsc.general.simulator.multistarter;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;

public class MultiParameterTest {

	@Test
	public void testMultiParameterDouble() throws BadParameterException {
		Double sum = 0.0;
		int count = 0;
		final MpDouble mpDouble = new MpDouble("double", 0.1, 1.0, 0.1);
		while (mpDouble.hasNext()) {
			sum += mpDouble.currentParameter().getValue();
			count += 1;
			mpDouble.increment();
		}
		Assert.assertEquals(4.5, sum, Settings.doubleEpsilon);
		Assert.assertEquals(9, count);

		Assert.assertEquals(0.4, mpDouble.parameter(3), Settings.doubleEpsilon);
		Assert.assertEquals(0.9, mpDouble.parameter(8), Settings.doubleEpsilon);
		Assert.assertEquals(1.1, mpDouble.parameter(10), Settings.doubleEpsilon);
	}

	@Test
	public void testMultiParameterInteger() throws BadParameterException {
		Integer sum = 0;
		int count = 0;
		final MpInteger mpInteger = new MpInteger("int", -4, 13, 2);
		while (mpInteger.hasNext()) {
			sum += mpInteger.current();
			count += 1;
			mpInteger.increment();
		}
		Assert.assertEquals(36, sum.intValue());
		Assert.assertEquals(9, count);

		Assert.assertEquals(2, mpInteger.parameter(3).intValue());
		Assert.assertEquals(6, mpInteger.parameter(5).intValue());
		Assert.assertEquals(14, mpInteger.parameter(9).intValue());
	}

	@Test
	public void testMultiParameterString() throws BadParameterException {
		String sum = "";
		int count = 0;
		final MpString mpString = new MpString("str", Arrays.asList("asd", "xcv"));
		while (mpString.hasNext()) {
			sum += mpString.current();
			count += 1;
			sum += mpString.next();
		}
		Assert.assertEquals("asdasdxcvxcv", sum);
		Assert.assertEquals(2, count);

		Assert.assertEquals("xcv", mpString.parameter(1));
		Assert.assertEquals("asd", mpString.parameter(0));
	}

	@Test
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
		Assert.assertEquals("tervlo", sum);
		Assert.assertEquals("se = terse = vlo", names);
		Assert.assertEquals(2, count);

		Assert.assertEquals("ter", exe.parameter(0));
		Assert.assertEquals("vlo", exe.parameter(1));
	}
}
