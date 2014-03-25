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

		AlgorithmsStorage algorithmsStorage = new AlgorithmsStorage();
		Broker broker = new Broker(new ThreadSafeStockStorage());
		SignalsStorage signalsStorage = new SignalsStorage();

		ExecutionsLoader.configFilePath = "./test_data/executions_loader_tests/algs_t1.ini";
		final ExecutionsLoader el = new ExecutionsLoader(Arrays.asList("aapl,goog,spy".split(",")), algorithmsStorage,
				broker, signalsStorage);

		assertNotNull(el.getExecutionsStorage());
		final ExecutionsStorage executions = el.getExecutionsStorage();

		assertEquals(0, executions.getEodAlgorithmsSize());
		assertEquals(3, executions.getStockAlgorithmsSize());
	}
}
