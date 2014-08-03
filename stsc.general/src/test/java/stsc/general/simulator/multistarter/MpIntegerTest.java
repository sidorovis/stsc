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
		for (int i = 0; i < 1000; ++i) {
			final Integer mutatedResult = md.mutate(-11, 5);
			assertTrue(mutatedResult >= -11);
			assertTrue(mutatedResult <= 5);
		}
	}

	public void testMpIntegerSize() throws BadParameterException {
		assertEquals(20L, new MpInteger("a", -10, 10, 1).size());
		assertEquals(7L, new MpInteger("a", 0, 30, 4).size());
		assertEquals(75L, new MpInteger("a", 100, 1000, 12).size());
	}
}
