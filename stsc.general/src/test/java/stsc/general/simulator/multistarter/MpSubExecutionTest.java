package stsc.general.simulator.multistarter;

import java.util.Arrays;

import junit.framework.TestCase;

public class MpSubExecutionTest extends TestCase {
	public void testMpSubExecution() throws BadParameterException {
		final MpSubExecution check = new MpSubExecution("a", Arrays.asList(new String[] { "a", "r" }));
		assertTrue(check instanceof MpString);
	}
}
