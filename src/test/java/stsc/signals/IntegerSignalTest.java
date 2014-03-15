package stsc.signals;

import junit.framework.TestCase;

public class IntegerSignalTest extends TestCase {
	public void testIntegerSignal() {
		IntegerSignal is = new IntegerSignal(65456);
		assertEquals(65456, is.value.intValue());
	}
}
