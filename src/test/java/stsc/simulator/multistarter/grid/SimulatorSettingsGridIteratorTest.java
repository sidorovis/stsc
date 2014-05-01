package stsc.simulator.multistarter.grid;

import java.util.Arrays;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.StockAlgorithm;
import stsc.common.FromToPeriod;
import stsc.simulator.SimulatorSettings;
import stsc.simulator.multistarter.AlgorithmSettingsIteratorFactory;
import stsc.simulator.multistarter.BadParameterException;
import stsc.simulator.multistarter.MpDouble;
import stsc.simulator.multistarter.MpInteger;
import stsc.simulator.multistarter.MpString;
import stsc.simulator.multistarter.MpSubExecution;
import stsc.simulator.multistarter.grid.AlgorithmSettingsGridIterator;
import stsc.simulator.multistarter.grid.SimulatorSettingsGridIterator;
import stsc.storage.AlgorithmsStorage;
import stsc.storage.ExecutionsStorage;
import stsc.storage.StockStorage;
import stsc.testhelper.StockStorageHelper;
import stsc.testhelper.TestHelper;
import stsc.trading.Broker;
import junit.framework.TestCase;

public class SimulatorSettingsGridIteratorTest extends TestCase {

	private String algoStockName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getStock(aname).getName();
	}

	private String algoEodName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getEod(aname).getName();
	}

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
		final FromToPeriod period = TestHelper.getPeriod();

		final SimulatorSettingsGridIterator settings = new SimulatorSettingsGridIterator(stockStorage, period);

		final AlgorithmSettingsIteratorFactory factoryIn = new AlgorithmSettingsIteratorFactory(period);
		factoryIn.add(new MpString("e", Arrays.asList(new String[] { "open", "high", "low", "close", "value" })));
		final AlgorithmSettingsGridIterator in = factoryIn.getGridIterator();
		settings.addStock("in", algoStockName("In"), in);

		final AlgorithmSettingsIteratorFactory factoryEma = new AlgorithmSettingsIteratorFactory(period);
		factoryEma.add(new MpDouble("P", 0.1, 0.6, 0.1));
		factoryEma.add(new MpSubExecution("", Arrays.asList(new String[] { "e" })));
		final AlgorithmSettingsGridIterator ema = factoryEma.getGridIterator();
		settings.addStock("ema", algoStockName("Ema"), ema);

		final AlgorithmSettingsIteratorFactory factoryLevel = new AlgorithmSettingsIteratorFactory(period);
		factoryLevel.add(new MpDouble("f", 15.0, 20.0, 1.0));
		factoryLevel.add(new MpSubExecution("", Arrays.asList(new String[] { "in", "ema" })));
		final AlgorithmSettingsGridIterator level = factoryLevel.getGridIterator();
		settings.addStock("level", algoStockName("Level"), level);

		final AlgorithmSettingsIteratorFactory factoryOneSide = new AlgorithmSettingsIteratorFactory(period);
		factoryOneSide.add(new MpString("side", Arrays.asList(new String[] { "long", "short" })));
		final AlgorithmSettingsGridIterator oneSide = factoryOneSide.getGridIterator();
		settings.addEod("os", algoEodName("OneSideOpenAlgorithm"), oneSide);

		final AlgorithmSettingsIteratorFactory factoryPositionSide = new AlgorithmSettingsIteratorFactory(period);
		factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new String[] { "in", "level", "ema" })));
		factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new String[] { "level", "ema" })));
		factoryPositionSide.add(new MpInteger("n", 1, 32, 10));
		factoryPositionSide.add(new MpInteger("m", 1, 32, 10));
		factoryPositionSide.add(new MpDouble("ps", 50000.0, 200001.0, 50000.0));
		final AlgorithmSettingsGridIterator positionSide = factoryPositionSide.getGridIterator();
		settings.addEod("pnm", algoEodName("PositionNDayMStocks"), positionSide);

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
		assertEquals(5 * 5 * 5 * 2 * 2 * 3 * 2 * 4 * 4 * 4, count);
	}
}
