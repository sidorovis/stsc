package stsc.general.simulator;

import java.io.File;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.algorithms.eod.primitive.OneSideOpenAlgorithm;
import stsc.common.FromToPeriod;
import stsc.common.Settings;
import stsc.common.algorithms.EodExecution;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.statistic.Statistics;
import stsc.general.trading.TradeProcessorInit;
import stsc.storage.ExecutionsStorage;
import stsc.storage.StockStorageFactory;
import junit.framework.TestCase;

public class SimulatorTest extends TestCase {

	private void deleteFileIfExists(String fileName) {
		File file = new File(fileName);
		if (file.exists())
			file.delete();
	}

	public void testOneSideSimulator() throws Exception {
		deleteFileIfExists("./test/statistics.csv");
		Simulator.fromFile(new File("./test_data/simulator_tests/one_side.ini")).getStatistics().print("./test/statistics.csv");
		assertEquals(544, new File("./test/statistics.csv").length());
		deleteFileIfExists("./test/statistics.csv");
	}

	public void testLongSideOnAppl() throws Exception {
		final StockStorage stockStorage = StockStorageFactory.createStockStorage("aapl", "./test_data/");
		final ExecutionsStorage executionsStorage = new ExecutionsStorage();
		final FromToPeriod period = new FromToPeriod("01-09-2002", "27-09-2002");
		final EodExecution execution = new EodExecution("eName", OneSideOpenAlgorithm.class, new AlgorithmSettingsImpl(period));
		executionsStorage.addEodExecution(execution);

		final TradeProcessorInit tpi = new TradeProcessorInit(stockStorage, period, executionsStorage);
		Simulator simulator = new Simulator(new SimulatorSettings(0, tpi));
		final Statistics statistics = simulator.getStatistics();
		assertEquals(18.0, statistics.getPeriod());
		assertEquals(4.209799, statistics.getAvGain(), Settings.doubleEpsilon);
	}

	public void testShortSideOnAppl() throws Exception {
		final StockStorage stockStorage = StockStorageFactory.createStockStorage("aapl", "./test_data/");
		final ExecutionsStorage executionsStorage = new ExecutionsStorage();
		final FromToPeriod period = new FromToPeriod("01-09-2002", "27-09-2002");
		final EodExecution execution = new EodExecution("eName", OneSideOpenAlgorithm.class, new AlgorithmSettingsImpl(period).setString(
				"side", "short"));
		executionsStorage.addEodExecution(execution);

		final TradeProcessorInit tpi = new TradeProcessorInit(stockStorage, period, executionsStorage);
		Simulator simulator = new Simulator(new SimulatorSettings(0, tpi));
		final Statistics statistics = simulator.getStatistics();
		assertEquals(18.0, statistics.getPeriod());
		assertEquals(-4.209799, statistics.getAvGain(), Settings.doubleEpsilon);
	}

	public void testSimpleSimulator() throws Exception {
		deleteFileIfExists("./test/statistics.csv");
		Simulator.fromFile(new File("./test_data/simulator_tests/simple.ini")).getStatistics().print("./test/statistics.csv");
		assertEquals(11863, new File("./test/statistics.csv").length());
		deleteFileIfExists("./test/statistics.csv");
	}

	public void testPositiveNDaysSimulator() throws Exception {
		deleteFileIfExists("./test/statistics.csv");
		Simulator.fromFile(new File("./test_data/simulator_tests/ndays.ini")).getStatistics().print("./test/statistics.csv");
		assertEquals(11767, new File("./test/statistics.csv").length());
		deleteFileIfExists("./test/statistics.csv");
	}

	public void testOpenWhileSignalAlgorithmSimulator() throws Exception {
		deleteFileIfExists("./test/statistics.csv");
		Simulator.fromFile(new File("./test_data/simulator_tests/open_while_signal.ini")).getStatistics().print("./test/statistics.csv");
		assertEquals(513, new File("./test/statistics.csv").length());
		deleteFileIfExists("./test/statistics.csv");
	}

}
