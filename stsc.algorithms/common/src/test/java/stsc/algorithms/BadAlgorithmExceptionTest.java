package stsc.algorithms;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.algorithms.BadAlgorithmException;

public final class BadAlgorithmExceptionTest {

	@Test
	public void testBadAlgorithmException() {
		final BadAlgorithmException exception = new BadAlgorithmException("my exception message");
		Assert.assertEquals("my exception message", exception.getMessage());
		boolean bae = false;
		boolean e = false;
		try {
			throw new BadAlgorithmException("the reason");
		} catch (BadAlgorithmException ex) {
			bae = true;
			Assert.assertEquals("the reason", ex.getMessage());
		} catch (Exception ex) {
			e = true;
		}
		Assert.assertTrue(bae);
		Assert.assertFalse(e);
	}
}
