package stsc.general.simulator.multistarter;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class MpSubExecutionTest {
	
	@Test
	public void testMpSubExecution() throws BadParameterException {
		final MpSubExecution check = new MpSubExecution("a", Arrays.asList(new String[] { "a", "r" }));
		Assert.assertTrue(check instanceof MpString);
	}
}
