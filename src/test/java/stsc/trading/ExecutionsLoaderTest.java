package stsc.trading;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import stsc.algorithms.BadAlgorithmException;
import stsc.storage.AlgorithmsStorage;
import stsc.storage.ExecutionsStorage;
import stsc.storage.SignalsStorage;
import stsc.storage.ThreadSafeStockStorage;
import junit.framework.TestCase;

public class ExecutionsLoaderTest extends TestCase {

	private ExecutionsStorage helperForSuccessLoadTests(String filename) throws ClassNotFoundException, IOException,
			BadAlgorithmException {
		final AlgorithmsStorage algorithmsStorage = new AlgorithmsStorage();
		final Broker broker = new Broker(new ThreadSafeStockStorage());
		final SignalsStorage signalsStorage = new SignalsStorage();

		ExecutionsLoader.configFilePath = filename;
		final ExecutionsLoader el = new ExecutionsLoader(Arrays.asList("aapl,goog,spy".split(",")), algorithmsStorage,
				broker, signalsStorage);

		assertNotNull(el.getExecutionsStorage());
		final ExecutionsStorage executions = el.getExecutionsStorage();
		return executions;
	}

	public void testAlgorithmLoader() throws FileNotFoundException, IOException, BadAlgorithmException,
			ClassNotFoundException {
		final ExecutionsStorage executions = helperForSuccessLoadTests("./test_data/executions_loader_tests/algs_t1.ini");
		assertEquals(3, executions.getStockAlgorithmsSize());
		assertEquals(0, executions.getEodAlgorithmsSize());
	}

	public void testSeveralAlgorithmLoader() throws ClassNotFoundException, IOException, BadAlgorithmException {
		final ExecutionsStorage executions = helperForSuccessLoadTests("./test_data/executions_loader_tests/algs_t2.ini");
		assertEquals(5, executions.getStockAlgorithmsSize());
		assertEquals(0, executions.getEodAlgorithmsSize());
	}

	private void throwTesthelper(String file, String message) throws FileNotFoundException, IOException,
			ClassNotFoundException {
		final AlgorithmsStorage algorithmsStorage = new AlgorithmsStorage();
		final Broker broker = new Broker(new ThreadSafeStockStorage());
		final SignalsStorage signalsStorage = new SignalsStorage();

		ExecutionsLoader.configFilePath = file;
		boolean throwed = false;
		try {
			new ExecutionsLoader(Arrays.asList("aapl,goog,spy".split(",")), algorithmsStorage, broker, signalsStorage);
		} catch (BadAlgorithmException e) {
			assertEquals(message, e.getMessage());
			throwed = true;
		}
		assertEquals(true, throwed);
	}

	public void testBadAlgoFiles() throws Exception {
		throwTesthelper("./test_data/executions_loader_tests/algs_bad_repeat.ini",
				"algorithm AlgDefines already registered");
		throwTesthelper("./test_data/executions_loader_tests/algs_no_load_line.ini",
				"bad stock execution registration, no AlgDefine.loadLine property");
		throwTesthelper("./test_data/executions_loader_tests/algs_bad_load_line1.ini",
				"bad algorithm load line: IN( e = close");
		throwTesthelper("./test_data/executions_loader_tests/algs_bad_load_line2.ini", "bad algorithm load line: IN)");
		throwTesthelper(
				"./test_data/executions_loader_tests/algs_bad_load_line3.ini",
				"Exception while loading algo: stsc.algorithms.factors.primitive.Sma( 3533721117350624 ) , exception: stsc.algorithms.BadAlgorithmException: Sma algorithm should receive at least one sub algorithm");
	}

	public void testAlgorithmLoaderWithEod() throws ClassNotFoundException, IOException, BadAlgorithmException {
		final ExecutionsStorage executions = helperForSuccessLoadTests("./test_data/executions_loader_tests/trade_algs.ini");
		assertEquals(2, executions.getStockAlgorithmsSize());
		assertNotNull(executions.getEodAlgorithm("a1"));
	}

}
