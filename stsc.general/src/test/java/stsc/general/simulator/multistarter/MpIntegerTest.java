package stsc.general.simulator.multistarter;

import junit.framework.TestCase;

public class MpIntegerTest extends TestCase {
	public void testMpIntegerGetIndexByValue() throws BadParameterException {
		final MpInteger md = new MpInteger("a", -10, 40, 5);
		assertEquals(0, md.getIndexByValue(-10));
		assertEquals(2, md.getIndexByValue(0));
		assertEquals(4, md.getIndexByValue(10));
		assertEquals(md.size(), md.getIndexByValue(40));
	}

	public void testMpIntegerMutate() throws BadParameterException {
		final MpInteger md = new MpInteger("a", -15, 15, 2);
		for (int i = 0; i < 1000; ++i) {
			final Integer mutatedResult = md.mutate(-11, 7);
			assertTrue(mutatedResult >= -11);
			assertTrue(mutatedResult <= 7);
		}
	}

	public void testMpIntegerBadIndex() throws BadParameterException {
		final MpInteger md = new MpInteger("a", -15, 15, 2);
		final Integer mutatedResult = md.mutate(-10, 4);
		assertTrue(mutatedResult >= -10);
		assertTrue(mutatedResult <= 4);
	}
}
