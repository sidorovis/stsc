package stsc.signals;

import org.junit.Assert;
import org.junit.Test;

public class BooleanSignalTest {

	@Test
	public void testBooleanSignal() {
		BooleanSignal bsTrue = new BooleanSignal(true);
		Assert.assertEquals(true, bsTrue.value.booleanValue());
		BooleanSignal bsFalse = new BooleanSignal(false);
		Assert.assertEquals(false, bsFalse.value.booleanValue());
	}
}
