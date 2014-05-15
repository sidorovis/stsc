package stsc.simulator.multistarter.grid;

import java.util.Arrays;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.StockAlgorithm;
import stsc.simulator.SimulatorSettings;
import stsc.simulator.multistarter.BadParameterException;
import stsc.storage.ExecutionsStorage;
import stsc.storage.StockStorage;
import stsc.testhelper.StockStorageHelper;
import stsc.testhelper.TestHelper;
import stsc.trading.Broker;
import junit.framework.TestCase;

public class SimulatorSettingsGridCopyListTest extends TestCase {

	public void testSimulatorSettingsGridIterator() throws BadAlgorithmException, BadParameterException {
		final StockStorage stockStorage = new StockStorageHelper();

		final SimulatorSettingsGridFactory factory = TestHelper.getSimulatorSettingsGridFactory(stockStorage,
				Arrays.asList(new String[] { "open" }), "31-01-2000");
		final SimulatorSettingsGridCopyList list = factory.getCopyList();
		int count = 0;
		for (SimulatorSettings simulatorSettings : list) {
			final ExecutionsStorage executionsStorage = simulatorSettings.getInit().getExecutionsStorage();
			executionsStorage.initialize(new Broker(stockStorage));
			final StockAlgorithm sain = executionsStorage.getStockAlgorithm("in", "aapl");
			final EodAlgorithm saone = executionsStorage.getEodAlgorithm("os");
			assertNotNull(sain);
			assertNotNull(saone);
		}
		// TODO fix test for copy
		assertEquals(0, count);
	}
}
