package stsc.general.simulator.multistarter.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodAlgorithm;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.testhelper.TestGridSimulatorSettings;
import stsc.general.trading.BrokerImpl;
import stsc.storage.ExecutionStarter;
import stsc.storage.ExecutionsStorage;
import stsc.storage.mocks.StockStorageMock;

public class SimulatorSettingsGridCopyListTest {

	@Test
	public void testSimulatorSettingsGridCopyList() throws BadAlgorithmException, BadParameterException {
		final StockStorage stockStorage = new StockStorageMock();

		final SimulatorSettingsGridFactory factory = TestGridSimulatorSettings.getSmallGridFactory(stockStorage,
				Arrays.asList(new String[] { "open" }), "31-01-2000");
		final SimulatorSettingsGridList list = factory.getList();

		final List<String> simulatorSettingsHashCodes = new ArrayList<>();
		int firstCount = 0;

		for (SimulatorSettings simulatorSettings : list) {
			final ExecutionsStorage executionsStorage = simulatorSettings.getInit().getExecutionsStorage();
			final ExecutionStarter executionStarter = executionsStorage.initialize(new BrokerImpl(stockStorage));
			final StockAlgorithm sain = executionStarter.getStockAlgorithm("in", "aapl");
			final EodAlgorithm saone = executionStarter.getEodAlgorithm("os");
			Assert.assertNotNull(sain);
			Assert.assertNotNull(saone);
			firstCount += 1;
			simulatorSettingsHashCodes.add(simulatorSettings.stringHashCode());
		}

		int secondCount = 0;
		for (SimulatorSettings simulatorSettings : list) {
			final ExecutionsStorage executionsStorage = simulatorSettings.getInit().getExecutionsStorage();
			final ExecutionStarter executionStarter = executionsStorage.initialize(new BrokerImpl(stockStorage));
			final StockAlgorithm sain = executionStarter.getStockAlgorithm("in", "aapl");
			final EodAlgorithm saone = executionStarter.getEodAlgorithm("os");
			Assert.assertNotNull(sain);
			Assert.assertNotNull(saone);
			Assert.assertEquals(simulatorSettingsHashCodes.get(secondCount), simulatorSettings.stringHashCode());
			secondCount += 1;
		}

		Assert.assertEquals(firstCount, secondCount);
	}
}
