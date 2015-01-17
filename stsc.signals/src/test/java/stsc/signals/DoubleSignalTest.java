package stsc.signals;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Settings;

public class DoubleSignalTest {

	@Test
	public void testDoubleSignal() {
		DoubleSignal ds = new DoubleSignal(13.765);
		Assert.assertEquals(13.765, ds.getValue(), Settings.doubleEpsilon);
	}
}
