package stsc.signals;

import org.junit.Assert;
import org.junit.Test;

public class IntegerSignalTest {

	@Test
	public void testIntegerSignal() {
		IntegerSignal is = new IntegerSignal(65456);
		Assert.assertEquals(65456, is.value.intValue());
	}
}
