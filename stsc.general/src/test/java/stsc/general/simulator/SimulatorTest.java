package stsc.general.simulator;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

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

public class SimulatorTest {

	private void deleteFileIfExists(String fileName) {
		File file = new File(fileName);
		if (file.exists())
			file.delete();
	}

	@Test
	public void testOneSideSimulator() throws Exception {
		deleteFileIfExists("./test/statistics.csv");
		Simulator.fromFile(new File("./test_data/simulator_tests/one_side.ini")).getStatistics().print("./test/statistics.csv");
		Assert.assertEquals(543, new File("./test/statistics.csv").length());
		deleteFileIfExists("./test/statistics.csv");
	}

	@Test
	public void testLongSideOnAppl() throws Exception {
		final StockStorage stockStorage = StockStorageFactory.createStockStorage("aapl", "./test_data/");
		final ExecutionsStorage executionsStorage = new ExecutionsStorage();
		final FromToPeriod period = new FromToPeriod("01-09-2002", "27-09-2002");
		final EodExecution execution = new EodExecution("eName", OneSideOpenAlgorithm.class, new AlgorithmSettingsImpl(period));
		executionsStorage.addEodExecution(execution);

		final TradeProcessorInit tpi = new TradeProcessorInit(stockStorage, period, executionsStorage);
		Simulator simulator = new Simulator(new SimulatorSettings(0, tpi));
		final Statistics statistics = simulator.getStatistics();
		Assert.assertEquals(19.0, statistics.getPeriod(), Settings.doubleEpsilon);
		Assert.assertEquals(1.761237, statistics.getAvGain(), Settings.doubleEpsilon);
	}

	@Test
	public void testLongSideOnApplForTwoMonths() throws Exception {
		final StockStorage stockStorage = StockStorageFactory.createStockStorage("aapl", "./test_data/");
		final ExecutionsStorage executionsStorage = new ExecutionsStorage();
		final FromToPeriod period = new FromToPeriod("01-09-2002", "27-10-2002");
		final EodExecution execution = new EodExecution("eName", OneSideOpenAlgorithm.class, new AlgorithmSettingsImpl(period));
		executionsStorage.addEodExecution(execution);

		final TradeProcessorInit tpi = new TradeProcessorInit(stockStorage, period, executionsStorage);
		Simulator simulator = new Simulator(new SimulatorSettings(0, tpi));
		final Statistics statistics = simulator.getStatistics();
		Assert.assertEquals(39.0, statistics.getPeriod(), Settings.doubleEpsilon);
		Assert.assertEquals(3.218612, statistics.getAvGain(), Settings.doubleEpsilon);
	}

	@Test
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
		Assert.assertEquals(19.0, statistics.getPeriod(), Settings.doubleEpsilon);
		Assert.assertEquals(-1.761237, statistics.getAvGain(), Settings.doubleEpsilon);
	}

	@Test
	public void testSimpleSimulator() throws Exception {
		deleteFileIfExists("./test/statistics.csv");
		final Statistics statistics = Simulator.fromFile(new File("./test_data/simulator_tests/simple.ini")).getStatistics();
		statistics.print("./test/statistics.csv");
		Assert.assertEquals(2096, statistics.getEquityCurveInMoney().size());
		Assert.assertEquals(46134, new File("./test/statistics.csv").length());
		deleteFileIfExists("./test/statistics.csv");
	}

	public void testPositiveNDaysSimulator() throws Exception {
		deleteFileIfExists("./test/statistics.csv");
		Simulator.fromFile(new File("./test_data/simulator_tests/ndays.ini")).getStatistics().print("./test/statistics.csv");
		Assert.assertEquals(575 * 2 + 11165, new File("./test/statistics.csv").length());
		deleteFileIfExists("./test/statistics.csv");
	}

	@Test
	public void testOpenWhileSignalAlgorithmSimulator() throws Exception {
		deleteFileIfExists("./test/statistics.csv");
		Simulator.fromFile(new File("./test_data/simulator_tests/open_while_signal.ini")).getStatistics().print("./test/statistics.csv");
		Assert.assertEquals(524, new File("./test/statistics.csv").length());
		deleteFileIfExists("./test/statistics.csv");
	}

	@Test
	public void testFromConfigOutAlgos() throws Exception {
		deleteFileIfExists("./test/statistics.csv");
		final StockStorage stoskStorage = StockStorageFactory.createStockStorage("aapl", "./test_data/");
		final FromToPeriod period = TestStatisticsHelper.getPeriod();
		final String config = "StockExecutions = Alg1\n" + "Alg1.loadLine = Sma(n = 5, In(e=close))";

		final TradeProcessorInit init = new TradeProcessorInit(stoskStorage, period, config);
		final List<String> stockExecutions = init.generateOutForStocks();
		Assert.assertEquals(2, stockExecutions.size());
		Assert.assertEquals("Alg1", stockExecutions.get(1));
		final Simulator simulator = new Simulator(new SimulatorSettings(0, init));
		Assert.assertEquals(0.0, simulator.getStatistics().getAvGain(), Settings.doubleEpsilon);
		final SignalsStorage ss = simulator.getSignalsStorage();
		final String en = ExecutionsStorage.outNameFor("Alg1");
		Assert.assertEquals(2515, ss.getIndexSize("aapl", en));
	}

}
