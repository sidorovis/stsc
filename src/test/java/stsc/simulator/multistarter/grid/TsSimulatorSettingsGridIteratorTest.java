package stsc.simulator.multistarter.grid;

import java.text.ParseException;
import java.util.Arrays;

import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.StockAlgorithm;
import stsc.common.FromToPeriod;
import stsc.simulator.SimulatorSettings;
import stsc.simulator.multistarter.BadParameterException;
import stsc.storage.ExecutionsStorage;
import stsc.storage.StockStorage;
import stsc.testhelper.StockStorageHelper;
import stsc.testhelper.TestHelper;
import stsc.trading.Broker;
import junit.framework.TestCase;

public class TsSimulatorSettingsGridIteratorTest extends TestCase {
	public void testTsSimulatorSettingsGridSearcher() throws BadAlgorithmException, BadParameterException,
			ParseException {
		final StockStorage stockStorage = new StockStorageHelper();
		final FromToPeriod period = new FromToPeriod("01-01-2000", "31-01-2000");

		final TsSimulatorSettingsGridIterator settings = new TsSimulatorSettingsGridIterator(stockStorage, period);
		TestHelper.fillIterator(settings, period,
				Arrays.asList(new String[] { "open", "high", "low", "close", "value" }));

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
