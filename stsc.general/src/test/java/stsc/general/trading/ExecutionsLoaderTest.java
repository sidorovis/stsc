package stsc.general.trading;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.storage.StockStorage;
import stsc.general.testhelper.TestStatisticsHelper;
import stsc.general.trading.BrokerImpl;
import stsc.general.trading.ExecutionsLoader;
import stsc.storage.ExecutionStarter;
import stsc.storage.ExecutionsStorage;
import stsc.storage.mocks.StockStorageMock;
import junit.framework.TestCase;

public class ExecutionsLoaderTest extends TestCase {

	private ExecutionsStorage helperForSuccessLoadTests(String filename) throws Exception {
		final StockStorage ss = new StockStorageMock();
		final ExecutionsLoader el = new ExecutionsLoader(filename, TestStatisticsHelper.getPeriod());
		assertNotNull(el.getExecutionsStorage());
		final ExecutionsStorage executions = el.getExecutionsStorage();
		executions.initialize(new BrokerImpl(ss));
		return executions;
	}

	public void testAlgorithmLoader() throws Exception {
		final ExecutionsStorage executions = helperForSuccessLoadTests("./test_data/executions_loader_tests/algs_t1.ini");
		final ExecutionStarter starter = executions.initialize(new BrokerImpl(StockStorageMock.getStockStorage()));
		assertEquals(3, starter.getStockAlgorithmsSize());
		assertEquals(0, starter.getEodAlgorithmsSize());
	}

	public void testSeveralAlgorithmLoader() throws Exception {
		final ExecutionsStorage executions = helperForSuccessLoadTests("./test_data/executions_loader_tests/algs_t2.ini");
		final ExecutionStarter starter = executions.initialize(new BrokerImpl(StockStorageMock.getStockStorage()));
		assertEquals(5, starter.getStockAlgorithmsSize());
		assertEquals(0, starter.getEodAlgorithmsSize());
	}

	private void throwTesthelper(String file, String message) throws Exception {
		boolean throwed = false;
		try {
			ExecutionsLoader loader = new ExecutionsLoader(file, TestStatisticsHelper.getPeriod());
			loader.getExecutionsStorage().initialize(new BrokerImpl(new StockStorageMock()));
		} catch (BadAlgorithmException e) {
			assertEquals(message, e.getMessage());
			throwed = true;
		}
		assertEquals(true, throwed);
	}

	public void testBadAlgoFiles() throws Exception {
		throwTesthelper("./test_data/executions_loader_tests/algs_bad_repeat.ini", "algorithm AlgDefines already registered");
		throwTesthelper("./test_data/executions_loader_tests/algs_no_load_line.ini", "bad stock execution registration, no AlgDefine.loadLine property");
		throwTesthelper("./test_data/executions_loader_tests/algs_bad_load_line1.ini", "bad algorithm load line: IN( e = close");
		throwTesthelper("./test_data/executions_loader_tests/algs_bad_load_line2.ini", "bad algorithm load line: IN)");
		throwTesthelper(
				"./test_data/executions_loader_tests/algs_bad_load_line3.ini",
				"Exception while loading algo: stsc.algorithms.stock.factors.primitive.Sma( 35337210 ) , exception: stsc.common.algorithms.BadAlgorithmException: Sma algorithm should receive at least one sub algorithm");
	}

	public void testAlgorithmLoaderWithEod() throws Exception {
		final ExecutionsStorage executions = helperForSuccessLoadTests("./test_data/executions_loader_tests/trade_algs.ini");
		final ExecutionStarter starter = executions.initialize(new BrokerImpl(StockStorageMock.getStockStorage()));
		assertEquals(2, starter.getStockAlgorithmsSize());
		assertNotNull(starter.getEodAlgorithm("a1"));
	}

}
