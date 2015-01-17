package stsc.general.simulator.multistarter;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class MpStringTest {
	
	@Test
	public void testMpStringGetIndexByValue() throws BadParameterException {
		final MpString md = new MpString("a", Arrays.asList(new String[] { "a", "b", "c", "d", "e" }));
		Assert.assertEquals(0, md.getIndexByValue("a"));
		Assert.assertEquals(1, md.getIndexByValue("b"));
		Assert.assertEquals(3, md.getIndexByValue("d"));
		Assert.assertEquals(4, md.getIndexByValue("e"));

		Assert.assertEquals(-1, md.getIndexByValue(""));
		Assert.assertEquals(-6, md.getIndexByValue("g"));
	}

	@Test
	public void testMpStringMutate() throws BadParameterException {
		final MpString md = new MpString("a", Arrays.asList(new String[] { "a", "b", "c", "d", "e", "f", "g" }));
		Assert.assertEquals("d", md.merge("h", "g"));
		for (int i = 0; i < 1000; ++i) {
			final String mutatedResult = md.merge("b", "g");
			Assert.assertTrue(mutatedResult.charAt(0) >= 'b');
			Assert.assertTrue(mutatedResult.charAt(0) <= 'g');
		}
	}

	@Test
	public void testMpStringSize() throws BadParameterException {
		final MpString v = new MpString("a", Arrays.asList(new String[] { "a", "b", "c" }));
		Assert.assertEquals(3l, v.size());
	}

	@Test
	public void testMpStringClone() throws BadParameterException {
		final MpString v = new MpString("a", Arrays.asList(new String[] { "a", "b", "c" }));
		final MpIterator<String> copy = v.clone();
		v.next();
		Assert.assertFalse(copy.next().equals(v.next()));
	}
}
