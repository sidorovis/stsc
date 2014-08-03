package stsc.general.simulator.multistarter;

import java.util.Arrays;

import junit.framework.TestCase;

public class MpStringTest extends TestCase {
	public void testMpStringGetIndexByValue() throws BadParameterException {
		final MpString md = new MpString("a", Arrays.asList(new String[] { "a", "b", "c", "d", "e" }));
		assertEquals(0, md.getIndexByValue("a"));
		assertEquals(1, md.getIndexByValue("b"));
		assertEquals(3, md.getIndexByValue("d"));
		assertEquals(4, md.getIndexByValue("e"));

		assertEquals(-1, md.getIndexByValue(""));
		assertEquals(-6, md.getIndexByValue("g"));
	}

	public void testMpStringMutate() throws BadParameterException {
		final MpString md = new MpString("a", Arrays.asList(new String[] { "a", "b", "c", "d", "e", "f", "g" }));
		assertEquals("d", md.mutate("h", "g"));
		for (int i = 0; i < 1000; ++i) {
			final String mutatedResult = md.mutate("b", "g");
			assertTrue(mutatedResult.charAt(0) >= 'b');
			assertTrue(mutatedResult.charAt(0) <= 'g');
		}
	}

	public void testMpStringSize() throws BadParameterException {
		final MpString v = new MpString("a", Arrays.asList(new String[] { "a", "b", "c" }));
		assertEquals(3l, v.size());
	}

	public void testMpStringClone() throws BadParameterException {
		final MpString v = new MpString("a", Arrays.asList(new String[] { "a", "b", "c" }));
		final MpIterator<String> copy = v.clone();
		v.next();
		assertFalse(copy.next().equals(v.next()));
	}
}
