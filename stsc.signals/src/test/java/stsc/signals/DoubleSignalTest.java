package stsc.signals;

import junit.framework.TestCase;

public class DoubleSignalTest extends TestCase {
	public void testDoubleSignal() {
		DoubleSignal ds = new DoubleSignal(13.765);
		assertEquals(13.765, ds.getValue());
	}
}
