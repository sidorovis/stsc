package stsc.simulator.multistarter.grid;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.StockAlgorithm;
import stsc.common.FromToPeriod;
import stsc.simulator.SimulatorSettings;
import stsc.simulator.multistarter.BadParameterException;
import stsc.simulator.multistarter.grid.SimulatorSettingsGridIterator;
import stsc.storage.ExecutionsStorage;
import stsc.storage.StockStorage;
import stsc.testhelper.StockStorageHelper;
import stsc.testhelper.TestHelper;
import stsc.trading.Broker;
import junit.framework.TestCase;

public class SimulatorSettingsGridIteratorTest extends TestCase {

	public void testEmptySimulatorSettingsGridSearcher() throws BadAlgorithmException, BadParameterException {
		final StockStorage stockStorage = new StockStorageHelper();
		final FromToPeriod period = TestHelper.getPeriod();

		final SimulatorSettingsGridIterator settings = new SimulatorSettingsGridIterator(stockStorage, period);
		int count = 0;
		for (SimulatorSettings simulatorSettings : settings) {
			count += 1;
			assertNotNull(simulatorSettings);
		}
		assertEquals(0, count);
	}

	public void testSimulatorSettingsGridSearcher() throws BadAlgorithmException, BadParameterException {
		final StockStorage stockStorage = new StockStorageHelper();

		final SimulatorSettingsGridIterator settings = TestHelper.getSimulatorSettingsGridIterator();

		int count = 0;
		for (SimulatorSettings simulatorSettings : settings) {
			count += 1;
			final ExecutionsStorage executionsStorage = simulatorSettings.getInit().getExecutionsStorage();
			executionsStorage.initialize(new Broker(stockStorage));
			final StockAlgorithm sain = executionsStorage.getStockAlgorithm("in", "aapl");
			final StockAlgorithm saema = executionsStorage.getStockAlgorithm("ema", "aapl");
			final StockAlgorithm salevel = executionsStorage.getStockAlgorithm("level", "aapl");
			final EodAlgorithm saone = executionsStorage.getEodAlgorithm("os");
			assertNotNull(sain);
			assertNotNull(saema);
			assertNotNull(salevel);
			assertNotNull(saone);
		}
		assertEquals(30720, count);
	}
}
