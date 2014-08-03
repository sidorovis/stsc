package stsc.general.simulator.multistarter;

import stsc.common.Settings;
import junit.framework.TestCase;

public class MpDoubleTest extends TestCase {
	public void testMpDoubleGetIndexByValue() throws BadParameterException {
		final MpDouble md = new MpDouble("a", 0.5, 1.7, 0.1);
		assertEquals(0, md.getIndexByValue(0.5));
		assertEquals(1, md.getIndexByValue(0.6));
		assertEquals(5, md.getIndexByValue(1.0));
		assertEquals(md.size(), md.getIndexByValue(1.7));
	}

	public void testMpDoubleMutate() throws BadParameterException {
		final MpDouble md = new MpDouble("a", 0.5, 5.9, 0.1);
		for (int i = 0; i < 1000; ++i) {
			final Double mutatedResult = md.mutate(1.7, 3.3);
			assertTrue(mutatedResult >= 1.6999);
			assertTrue(mutatedResult <= 3.3001);
		}
	}

	public void testMpDoubleSize() throws BadParameterException {
		assertEquals(7, new MpDouble("b", -0.6, 0.4, 0.15).size());
		assertEquals(3, new MpDouble("b", -0.6, 0.4, 0.3).size());
		assertEquals(200, new MpDouble("b", -0.6, 1.4, 0.01).size());
	}

	public void testMpDoubleClone() throws BadParameterException {
		final MpDouble value = new MpDouble("a", -0.29, 0.63, 0.21);
		final MpIterator<Double> copy = value.clone();
		value.next();
		assertFalse(Settings.doubleEpsilon < copy.next().doubleValue() - value.next().doubleValue());
	}
}
