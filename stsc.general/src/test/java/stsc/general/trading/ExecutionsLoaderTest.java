package stsc.general.trading;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.general.testhelper.TestStatisticsHelper;
import stsc.general.trading.BrokerImpl;
import stsc.general.trading.ExecutionsLoader;
import stsc.storage.ExecutionStarter;
import stsc.storage.ExecutionsStorage;
import stsc.storage.mocks.StockStorageMock;

public class ExecutionsLoaderTest {

	private ExecutionsStorage helperForSuccessLoadTests(File filename) throws Exception {
		final StockStorage ss = new StockStorageMock();
		final ExecutionsLoader el = new ExecutionsLoader(filename, TestStatisticsHelper.getPeriod());
		Assert.assertNotNull(el.getExecutionsStorage());
		final ExecutionsStorage executions = el.getExecutionsStorage();
		executions.initialize(new BrokerImpl(ss));
		return executions;
	}

	@Test
	public void testAlgorithmLoader() throws Exception {
		final ExecutionsStorage executions = helperForSuccessLoadTests(new File("./test_data/executions_loader_tests/algs_t1.ini"));
		final ExecutionStarter starter = executions.initialize(new BrokerImpl(StockStorageMock.getStockStorage()));
		Assert.assertEquals(3, starter.getStockAlgorithmsSize());
		Assert.assertEquals(0, starter.getEodAlgorithmsSize());
	}

	@Test
	public void testSeveralAlgorithmLoader() throws Exception {
		final ExecutionsStorage executions = helperForSuccessLoadTests(new File("./test_data/executions_loader_tests/algs_t2.ini"));
		final ExecutionStarter starter = executions.initialize(new BrokerImpl(StockStorageMock.getStockStorage()));
		Assert.assertEquals(5, starter.getStockAlgorithmsSize());
		Assert.assertEquals(0, starter.getEodAlgorithmsSize());
	}

	private void throwTesthelper(File file, String message) throws Exception {
		boolean throwed = false;
		try {
			ExecutionsLoader loader = new ExecutionsLoader(file, TestStatisticsHelper.getPeriod());
			loader.getExecutionsStorage().initialize(new BrokerImpl(new StockStorageMock()));
		} catch (BadAlgorithmException e) {
			Assert.assertEquals(message, e.getMessage());
			throwed = true;
		}
		Assert.assertEquals(true, throwed);
	}

	@Test
	public void testBadAlgoFiles() throws Exception {
		throwTesthelper(new File("./test_data/executions_loader_tests/algs_bad_repeat.ini"), "algorithm AlgDefines already registered");
		throwTesthelper(new File("./test_data/executions_loader_tests/algs_no_load_line.ini"),
				"bad stock execution registration, no AlgDefine.loadLine property");
		throwTesthelper(new File("./test_data/executions_loader_tests/algs_bad_load_line1.ini"), "bad algorithm load line: INPUT( e = close");
		throwTesthelper(new File("./test_data/executions_loader_tests/algs_bad_load_line2.ini"), "bad algorithm load line: INPUT)");
		throwTesthelper(
				new File("./test_data/executions_loader_tests/algs_bad_load_line3.ini"),
				"Exception while loading algo: stsc.algorithms.stock.indices.primitive.Sma( AlgDefine ) , exception: stsc.common.algorithms.BadAlgorithmException: Sma algorithm should receive at least one sub algorithm");
	}

	@Test
	public void testAlgorithmLoaderWithEod() throws Exception {
		final ExecutionsStorage executions = helperForSuccessLoadTests(new File("./test_data/executions_loader_tests/trade_algs.ini"));
		final ExecutionStarter starter = executions.initialize(new BrokerImpl(StockStorageMock.getStockStorage()));
		Assert.assertEquals(4, starter.getStockAlgorithmsSize());
		Assert.assertNotNull(starter.getEodAlgorithm("a1"));
	}

}
