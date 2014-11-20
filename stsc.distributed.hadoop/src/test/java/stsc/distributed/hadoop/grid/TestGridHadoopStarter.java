package stsc.distributed.hadoop.grid;

import org.junit.Assert;
import org.junit.Test;

public class TestGridHadoopStarter {

	@Test
	public void testGridHadoopStarter() throws Exception {
		final HadoopStarter hs = new GridHadoopStarter();
		Assert.assertEquals(8, hs.startSearch().size());
	}
}
