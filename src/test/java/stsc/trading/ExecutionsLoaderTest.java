package stsc.trading;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class ExecutionsLoaderTest extends TestCase {
	public void testAlgorithmLoader() throws FileNotFoundException, IOException {
		final List<String> stockNames = Arrays.asList(new String[] {});
		final ExecutionsLoader el = new ExecutionsLoader(stockNames);

		assertNotNull(el.getExecutionsStorage());
	}
}
