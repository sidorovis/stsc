package stsc.distributed.hadoop.grid;

import java.io.File;

import org.apache.hadoop.fs.FileUtil;
import org.junit.Assert;
import org.junit.Test;

public class TestGridHadoopStarter {

	@Test
	public void testGridHadoopStarter() throws Exception {
		{
			final HadoopStarter hs = new GridHadoopStarter();
			Assert.assertEquals(8, hs.searchOnHadoop().size());
		}
		System.gc(); // dirty hack could be not free, but workaround for test purpose
		Assert.assertTrue(FileUtil.fullyDelete(new File("./yahoo_datafeed")));
		Assert.assertTrue(new File("./output_data").exists());
		Assert.assertTrue(FileUtil.fullyDelete(new File("./output_data")));
		Assert.assertTrue(new File("./output.txt").delete());
	}
}
