package stsc.algorithms;

import junit.framework.TestCase;

public class BadAlgorithmExceptionTest extends TestCase {
	public void testBadAlgorithmException() {
		final BadAlgorithmException exception = new BadAlgorithmException("my exception message");
		assertEquals("my exception message", exception.getMessage());
		boolean bae = false;
		boolean e = false;
		try {
			throw new BadAlgorithmException("the reason");
		} catch (BadAlgorithmException ex) {
			bae = true;
			assertEquals("the reason", ex.getMessage());
		} catch (Exception ex) {
			e = true;
		}
		assertTrue(bae);
		assertFalse(e);
	}
}
