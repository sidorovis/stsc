package stsc.general.simulator.multistarter.grid;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import stsc.common.FromToPeriod;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodAlgorithm;
import stsc.common.algorithms.StockAlgorithm;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.simulator.multistarter.AlgorithmSettingsIteratorFactory;
import stsc.general.simulator.multistarter.BadParameterException;
import stsc.general.simulator.multistarter.MpDouble;
import stsc.general.simulator.multistarter.MpInteger;
import stsc.general.simulator.multistarter.MpString;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridFactory;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.general.testhelper.TestHelper;
import stsc.general.testhelper.TestGridSimulatorSettings;
import stsc.general.trading.BrokerImpl;
import stsc.storage.ExecutionStarter;
import stsc.storage.ExecutionsStorage;
import stsc.storage.mocks.StockStorageMock;
import junit.framework.TestCase;

public class SimulatorSettingsGridIteratorTest extends TestCase {

	public void testEmptySimulatorSettingsGridIterator() throws BadAlgorithmException, BadParameterException {
		final StockStorage stockStorage = new StockStorageMock();
		final FromToPeriod period = TestHelper.getPeriod();

		final SimulatorSettingsGridFactory ssFactory = new SimulatorSettingsGridFactory(stockStorage, period);

		int count = 0;
		for (SimulatorSettings simulatorSettings : ssFactory.getList()) {
			count += 1;
			assertNotNull(simulatorSettings);
		}
		assertEquals(0, count);
	}

	public void testSimulatorSettingsGridIterator() throws BadAlgorithmException, BadParameterException {
		final StockStorage stockStorage = new StockStorageMock();

		final SimulatorSettingsGridList settings = TestGridSimulatorSettings.getGridList();

		int count = 0;
		for (SimulatorSettings simulatorSettings : settings) {
			count += 1;
			final ExecutionsStorage executionsStorage = simulatorSettings.getInit().getExecutionsStorage();
			final ExecutionStarter executionStarter = executionsStorage.initialize(new BrokerImpl(stockStorage));
			final StockAlgorithm sain = executionStarter.getStockAlgorithm("in", "aapl");
			final StockAlgorithm saema = executionStarter.getStockAlgorithm("ema", "aapl");
			final StockAlgorithm salevel = executionStarter.getStockAlgorithm("level", "aapl");
			final EodAlgorithm saone = executionStarter.getEodAlgorithm("os");
			assertNotNull(sain);
			assertNotNull(saema);
			assertNotNull(salevel);
			assertNotNull(saone);
		}
		assertEquals(30720, count);
	}

	public void testSimulatorSettingsGridIteratorHashCode() throws BadParameterException, BadAlgorithmException {
		final StockStorage stockStorage = new StockStorageMock();
		final SimulatorSettingsGridFactory ssFactory = new SimulatorSettingsGridFactory(stockStorage, TestHelper.getPeriod());
		AlgorithmSettingsIteratorFactory f1 = new AlgorithmSettingsIteratorFactory(TestHelper.getPeriod());
		f1.add(new MpInteger("a", 1, 3, 1));
		f1.add(new MpDouble("b", 0.1, 0.3, 0.1));
		f1.add(new MpDouble("c", 0.1, 0.2, 0.1));
		f1.add(new MpString("side2", Arrays.asList(new String[] { "long", "long" })));

		ssFactory.addStock("a1", TestGridSimulatorSettings.algoStockName("In"), f1.getGridIterator());
		ssFactory.addStock("a2", TestGridSimulatorSettings.algoStockName("In"), f1.getGridIterator());
		ssFactory.addEod("a3", TestGridSimulatorSettings.algoEodName("OneSideOpenAlgorithm"), f1.getGridIterator());
		ssFactory.addEod("a4", TestGridSimulatorSettings.algoEodName("OneSideOpenAlgorithm"), f1.getGridIterator());

		final Set<String> hashes = new HashSet<>();
		int allSize = 0;
		for (SimulatorSettings simulatorSettings : ssFactory.getList()) {
			hashes.add(simulatorSettings.stringHashCode());
			allSize += 1;
		}

		assertEquals(4096, allSize);
		assertEquals(256, hashes.size());

	}
}
