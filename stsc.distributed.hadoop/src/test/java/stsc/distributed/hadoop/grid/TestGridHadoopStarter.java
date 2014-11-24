package stsc.distributed.hadoop.grid;

import org.junit.Assert;
import org.junit.Test;

public class TestGridHadoopStarter {

	@Test
	public void testGridHadoopStarter() throws Exception {
		final HadoopStarter hs = new GridHadoopStarter();
		Assert.assertEquals(8, hs.searchOnHadoop().size());

		// TODO un-comment those tests and fix them
		
		// Assert.assertTrue(new File("./output_data").delete());
		// Assert.assertTrue(new File("./yahoo_datafeed").delete());
		// Assert.assertTrue(new File("./output.txt").delete());
	}
}
