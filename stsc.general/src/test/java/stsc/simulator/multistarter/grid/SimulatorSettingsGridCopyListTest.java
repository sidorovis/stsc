package stsc.simulator.multistarter.grid;

import java.util.Arrays;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.StockAlgorithm;
import stsc.simulator.SimulatorSettings;
import stsc.simulator.multistarter.BadParameterException;
import stsc.storage.ExecutionStarter;
import stsc.storage.ExecutionsStorage;
import stsc.storage.StockStorage;
import stsc.testhelper.TestSimulatorSettings;
import stsc.testhelper.TestStockStorageHelper;
import stsc.trading.Broker;
import junit.framework.TestCase;

public class SimulatorSettingsGridCopyListTest extends TestCase {

	public void testSimulatorSettingsGridCopyList() throws BadAlgorithmException, BadParameterException {
		final StockStorage stockStorage = new TestStockStorageHelper();

		final SimulatorSettingsGridFactory factory = TestSimulatorSettings.getGridFactory(stockStorage, Arrays.asList(new String[] { "open" }), "31-01-2000");
		final SimulatorSettingsGridCopyList list = factory.getCopyList();
		int count = 0;
		for (SimulatorSettings simulatorSettings : list) {
			final ExecutionsStorage executionsStorage = simulatorSettings.getInit().getExecutionsStorage();
			final ExecutionStarter executionStarter = executionsStorage.initialize(new Broker(stockStorage));
			final StockAlgorithm sain = executionStarter.getStockAlgorithm("in", "aapl");
			final EodAlgorithm saone = executionStarter.getEodAlgorithm("os");
			assertNotNull(sain);
			assertNotNull(saone);
		}
		// TODO fix test for copy
		assertEquals(0, count);
	}
}
