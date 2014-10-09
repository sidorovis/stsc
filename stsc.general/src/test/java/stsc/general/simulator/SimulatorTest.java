package stsc.general.simulator;

import java.io.File;
import java.util.List;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.algorithms.eod.primitive.OneSideOpenAlgorithm;
import stsc.common.FromToPeriod;
import stsc.common.Settings;
import stsc.common.algorithms.EodExecution;
import stsc.common.storage.SignalsStorage;
import stsc.common.storage.StockStorage;
import stsc.general.simulator.Simulator;
import stsc.general.simulator.SimulatorSettings;
import stsc.general.statistic.Statistics;
import stsc.general.testhelper.TestStatisticsHelper;
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
		assertEquals(480 + 33 * 2, new File("./test/statistics.csv").length());
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
		assertEquals(19.0, statistics.getPeriod());
		assertEquals(0.0, statistics.getAvGain(), Settings.doubleEpsilon);
	}

	public void testLongSideOnApplForTwoMonths() throws Exception {
		final StockStorage stockStorage = StockStorageFactory.createStockStorage("aapl", "./test_data/");
		final ExecutionsStorage executionsStorage = new ExecutionsStorage();
		final FromToPeriod period = new FromToPeriod("01-09-2002", "27-10-2002");
		final EodExecution execution = new EodExecution("eName", OneSideOpenAlgorithm.class, new AlgorithmSettingsImpl(period));
		executionsStorage.addEodExecution(execution);

		final TradeProcessorInit tpi = new TradeProcessorInit(stockStorage, period, executionsStorage);
		Simulator simulator = new Simulator(new SimulatorSettings(0, tpi));
		final Statistics statistics = simulator.getStatistics();
		assertEquals(39.0, statistics.getPeriod());
		assertEquals(1.380262, statistics.getAvGain(), Settings.doubleEpsilon);
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
		assertEquals(19.0, statistics.getPeriod());
		assertEquals(-0.0, statistics.getAvGain(), Settings.doubleEpsilon);
	}

	public void testSimpleSimulator() throws Exception {
		deleteFileIfExists("./test/statistics.csv");
		final Statistics statistics = Simulator.fromFile(new File("./test_data/simulator_tests/simple.ini")).getStatistics();
		statistics.print("./test/statistics.csv");
		assertEquals(2096, statistics.getEquityCurveInMoney().size());
		assertEquals(2121 * 2 + 40815, new File("./test/statistics.csv").length());
		deleteFileIfExists("./test/statistics.csv");
	}

	public void testPositiveNDaysSimulator() throws Exception {
		deleteFileIfExists("./test/statistics.csv");
		Simulator.fromFile(new File("./test_data/simulator_tests/ndays.ini")).getStatistics().print("./test/statistics.csv");
		assertEquals(575 * 2 + 10618, new File("./test/statistics.csv").length());
		deleteFileIfExists("./test/statistics.csv");
	}

	public void testOpenWhileSignalAlgorithmSimulator() throws Exception {
		deleteFileIfExists("./test/statistics.csv");
		Simulator.fromFile(new File("./test_data/simulator_tests/open_while_signal.ini")).getStatistics().print("./test/statistics.csv");
		assertEquals(32 * 2 + 468, new File("./test/statistics.csv").length());
		deleteFileIfExists("./test/statistics.csv");
	}

	public void testFromConfigOutAlgos() throws Exception {
		deleteFileIfExists("./test/statistics.csv");
		final StockStorage stoskStorage = StockStorageFactory.createStockStorage("aapl", "./test_data/");
		final FromToPeriod period = TestStatisticsHelper.getPeriod();
		final String config = "StockExecutions = Alg1\n" + "Alg1.loadLine = Sma(n = 5, In(e=close))";

		final TradeProcessorInit init = new TradeProcessorInit(stoskStorage, period, config);
		final List<String> stockExecutions = init.generateOutForStocks();
		assertEquals(2, stockExecutions.size());
		assertEquals("Alg1", stockExecutions.get(1));
		final Simulator simulator = new Simulator(new SimulatorSettings(0, init));
		assertEquals(0.0, simulator.getStatistics().getAvGain(), Settings.doubleEpsilon);
		final SignalsStorage ss = simulator.getSignalsStorage();
		final String en = ExecutionsStorage.outNameFor("Alg1");
		final int size = ss.getIndexSize("aapl", en);
		assertEquals(2515, size);
	}

}
