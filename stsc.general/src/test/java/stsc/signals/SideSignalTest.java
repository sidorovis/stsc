package stsc.signals;

import stsc.trading.Side;
import junit.framework.TestCase;

public class SideSignalTest extends TestCase {
	public void testSideSignalLong() {
		SideSignal sideSignal = new SideSignal(Side.LONG, 14.56);
		assertEquals(Side.LONG, sideSignal.getSide());
		assertEquals(14.56, sideSignal.getValue(), 0.01);
	}

	public void testSideSignalShort() {
		SideSignal sideSignal = new SideSignal(Side.SHORT, -414.56);
		assertEquals(Side.SHORT, sideSignal.getSide());
		assertEquals(-414.56, sideSignal.getValue(), 0.01);
	}
}
