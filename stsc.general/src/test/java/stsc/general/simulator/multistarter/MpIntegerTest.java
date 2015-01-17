package stsc.general.simulator.multistarter;

import org.junit.Assert;
import org.junit.Test;

public class MpIntegerTest {
	
	@Test
	public void testMpIntegerGetIndexByValue() throws BadParameterException {
		final MpInteger md = new MpInteger("a", -10, 40, 5);
		Assert.assertEquals(0, md.getIndexByValue(-10));
		Assert.assertEquals(2, md.getIndexByValue(0));
		Assert.assertEquals(4, md.getIndexByValue(10));
		Assert.assertEquals(md.size(), md.getIndexByValue(40));
	}

	@Test
	public void testMpIntegerMutate() throws BadParameterException {
		final MpInteger md = new MpInteger("a", -15, 15, 2);
		for (int i = 0; i < 1000; ++i) {
			final Integer mutatedResult = md.merge(-11, 7);
			Assert.assertTrue(mutatedResult >= -11);
			Assert.assertTrue(mutatedResult <= 7);
		}
	}

	@Test
	public void testMpIntegerBadIndex() throws BadParameterException {
		final MpInteger md = new MpInteger("a", -15, 15, 2);
		for (int i = 0; i < 1000; ++i) {
			final Integer mutatedResult = md.merge(-11, 5);
			Assert.assertTrue(mutatedResult >= -11);
			Assert.assertTrue(mutatedResult <= 5);
		}
	}

	@Test
	public void testMpIntegerSize() throws BadParameterException {
		Assert.assertEquals(20L, new MpInteger("a", -10, 10, 1).size());
		Assert.assertEquals(8L, new MpInteger("a", 0, 30, 4).size());
		Assert.assertEquals(2L, new MpInteger("a", 4, 30, 25).size());
		Assert.assertEquals(1L, new MpInteger("a", 4, 30, 28).size());
		Assert.assertEquals(75L, new MpInteger("a", 100, 1000, 12).size());
	}

	@Test
	public void testMpIntegerClone() throws BadParameterException {
		final MpInteger from = new MpInteger("a", 0, 10, 3);
		final MpIterator<Integer> to = from.clone();
		to.next();
		Assert.assertFalse(from.next().intValue() == to.next().intValue());
	}
}
