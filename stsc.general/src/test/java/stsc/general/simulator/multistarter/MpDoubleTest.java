package stsc.general.simulator.multistarter;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;

public class MpDoubleTest {

	@Test
	public void testMpDoubleGetIndexByValue() throws BadParameterException {
		final MpDouble md = new MpDouble("a", 0.5, 1.7, 0.1);
		Assert.assertEquals(0, md.getIndexByValue(0.5));
		Assert.assertEquals(1, md.getIndexByValue(0.6));
		Assert.assertEquals(5, md.getIndexByValue(1.0));
		Assert.assertEquals(md.size(), md.getIndexByValue(1.7));
	}

	@Test
	public void testMpDoubleMutate() throws BadParameterException {
		final MpDouble md = new MpDouble("a", 0.5, 5.9, 0.1);
		for (int i = 0; i < 1000; ++i) {
			final Double mutatedResult = md.merge(1.7, 3.3);
			Assert.assertTrue(mutatedResult >= 1.6999);
			Assert.assertTrue(mutatedResult <= 3.3001);
		}
	}

	@Test
	public void testMpDoubleSize() throws BadParameterException {
		Assert.assertEquals(7, new MpDouble("b", -0.6, 0.4, 0.15).size());
		Assert.assertEquals(4, new MpDouble("b", -0.6, 0.4, 0.3).size());
		Assert.assertEquals(200, new MpDouble("b", -0.6, 1.4, 0.01).size());
		Assert.assertEquals(2, new MpDouble("b", 0.1, 0.6, 0.4).size());
		Assert.assertEquals(1, new MpDouble("b", 0.1, 0.6, 0.8).size());
	}

	@Test
	public void testMpDoubleClone() throws BadParameterException {
		final MpDouble value = new MpDouble("a", -0.29, 0.63, 0.21);
		final MpIterator<Double> copy = value.clone();
		value.next();
		Assert.assertFalse(Settings.doubleEpsilon < copy.next().doubleValue() - value.next().doubleValue());
	}
}
