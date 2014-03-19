package stsc.trading;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import stsc.storage.AlgorithmsStorage;
import junit.framework.TestCase;

public class ExecutionsLoaderTest extends TestCase {

	public void testAlgorithmLoader() throws FileNotFoundException, IOException, ClassNotFoundException {
		
		AlgorithmsStorage algorithmsStorage = new AlgorithmsStorage();
		
		ExecutionsLoader.configFilePath = "./test_data/executions_loader_tests/algs_t1.ini";
		final ExecutionsLoader el = new ExecutionsLoader(new ArrayList<String>(), algorithmsStorage);

		assertNotNull(el.getExecutionsStorage());

	}
}
