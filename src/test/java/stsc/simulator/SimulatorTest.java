package stsc.simulator;

import java.io.File;

import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.EodExecution;
import stsc.algorithms.eod.primitive.OneSideOpenAlgorithm;
import stsc.common.FromToPeriod;
import stsc.statistic.Statistics;
import stsc.storage.ExecutionsStorage;
import stsc.storage.StockStorage;
import stsc.storage.StockStorageFactory;
import stsc.trading.TradeProcessorInit;
import junit.framework.TestCase;

public class SimulatorTest extends TestCase {

	private void deleteFileIfExists(String fileName) {
		File file = new File(fileName);
		if (file.exists())
			file.delete();
	}

	public void testOneSideSimulator() throws Exception {
		deleteFileIfExists("./test/statistics.csv");
		Simulator.fromFile("./test_data/simulator_tests/one_side.ini").getStatistics().print("./test/statistics.csv");
		assertEquals(544, new File("./test/statistics.csv").length());
		deleteFileIfExists("./test/statistics.csv");
	}

	public void testLongSideOnAppl() throws Exception {
		final StockStorage stockStorage = StockStorageFactory.createStockStorage("aapl");
		final ExecutionsStorage executionsStorage = new ExecutionsStorage();
		final FromToPeriod period = new FromToPeriod("01-09-2002", "27-09-2002");
		final EodExecution execution = new EodExecution("eName", OneSideOpenAlgorithm.class, new AlgorithmSettings(
				period));
		executionsStorage.addEodExecution(execution);

		final TradeProcessorInit tpi = new TradeProcessorInit(stockStorage, period, executionsStorage);
		Simulator simulator = new Simulator(new SimulatorSettings(tpi));
		final Statistics statistics = simulator.getStatistics();
		assertEquals(18.0, statistics.getPeriod());
		assertEquals(4.209799, statistics.getAvGain(), 0.000001);
	}

	public void testShortSideOnAppl() throws Exception {
		final StockStorage stockStorage = StockStorageFactory.createStockStorage("aapl");
		final ExecutionsStorage executionsStorage = new ExecutionsStorage();
		final FromToPeriod period = new FromToPeriod("01-09-2002", "27-09-2002");
		final EodExecution execution = new EodExecution("eName", OneSideOpenAlgorithm.class, new AlgorithmSettings(
				period).set("side", "short"));
		executionsStorage.addEodExecution(execution);

		final TradeProcessorInit tpi = new TradeProcessorInit(stockStorage, period, executionsStorage);
		Simulator simulator = new Simulator(new SimulatorSettings(tpi));
		final Statistics statistics = simulator.getStatistics();
		assertEquals(18.0, statistics.getPeriod());
		assertEquals(-4.209799, statistics.getAvGain(), 0.000001);
	}

	public void testSimpleSimulator() throws Exception {
		deleteFileIfExists("./test/statistics.csv");
		Simulator.fromFile("./test_data/simulator_tests/simple.ini").getStatistics().print("./test/statistics.csv");
		assertEquals(11863, new File("./test/statistics.csv").length());
		deleteFileIfExists("./test/statistics.csv");
	}

	public void testPositiveNDaysSimulator() throws Exception {
		deleteFileIfExists("./test/statistics.csv");
		Simulator.fromFile("./test_data/simulator_tests/ndays.ini").getStatistics().print("./test/statistics.csv");
		assertEquals(11685, new File("./test/statistics.csv").length());
		deleteFileIfExists("./test/statistics.csv");
	}

	public void testOpenWhileSignalAlgorithmSimulator() throws Exception {
		deleteFileIfExists("./test/statistics.csv");
		Simulator.fromFile("./test_data/simulator_tests/open_while_signal.ini").getStatistics()
				.print("./test/statistics.csv");
		assertEquals(513, new File("./test/statistics.csv").length());
		deleteFileIfExists("./test/statistics.csv");
	}

}
