package stsc.signals;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Side;

public class SideSignalTest {

	@Test
	public void testSideSignalLong() {
		SideSignal sideSignal = new SideSignal(Side.LONG, 14.56);
		Assert.assertEquals(Side.LONG, sideSignal.getSide());
		Assert.assertEquals(14.56, sideSignal.getValue(), 0.01);
	}

	@Test
	public void testSideSignalShort() {
		SideSignal sideSignal = new SideSignal(Side.SHORT, -414.56);
		Assert.assertEquals(Side.SHORT, sideSignal.getSide());
		Assert.assertEquals(-414.56, sideSignal.getValue(), 0.01);
	}
}
