package stsc.signals;

import junit.framework.TestCase;

public class BooleanSignalTest extends TestCase {
	public void testBooleanSignal() {
		BooleanSignal bsTrue = new BooleanSignal(true);
		assertEquals(true, bsTrue.value.booleanValue());
		BooleanSignal bsFalse = new BooleanSignal(false);
		assertEquals(false, bsFalse.value.booleanValue());
	}
}
