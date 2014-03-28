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

	public void testAlgorithmLoader() throws FileNotFoundException, IOException, BadAlgorithmException,
			ClassNotFoundException {
		final AlgorithmsStorage algorithmsStorage = new AlgorithmsStorage();
		final Broker broker = new Broker(new ThreadSafeStockStorage());
		final SignalsStorage signalsStorage = new SignalsStorage();

		ExecutionsLoader.configFilePath = "./test_data/executions_loader_tests/algs_t1.ini";
		final ExecutionsLoader el = new ExecutionsLoader(Arrays.asList("aapl,goog,spy".split(",")), algorithmsStorage,
				broker, signalsStorage);

		assertNotNull(el.getExecutionsStorage());
		final ExecutionsStorage executions = el.getExecutionsStorage();

		assertEquals(3, executions.getStockAlgorithmsSize());
		assertEquals(0, executions.getEodAlgorithmsSize());
	}

	public void testSeveralAlgorithmLoader() throws ClassNotFoundException, IOException, BadAlgorithmException {
		final AlgorithmsStorage algorithmsStorage = new AlgorithmsStorage();
		final Broker broker = new Broker(new ThreadSafeStockStorage());
		final SignalsStorage signalsStorage = new SignalsStorage();

		ExecutionsLoader.configFilePath = "./test_data/executions_loader_tests/algs_t2.ini";
		final ExecutionsLoader el = new ExecutionsLoader(Arrays.asList("aapl,goog,spy".split(",")), algorithmsStorage,
				broker, signalsStorage);

		assertNotNull(el.getExecutionsStorage());
		final ExecutionsStorage executions = el.getExecutionsStorage();

		assertEquals(5, executions.getStockAlgorithmsSize());
		assertEquals(0, executions.getEodAlgorithmsSize());
	}

}
