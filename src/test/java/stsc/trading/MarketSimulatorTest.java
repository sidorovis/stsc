package stsc.trading;

import java.io.IOException;
import java.text.ParseException;
import java.sql.Date;

import stsc.algorithms.EodAlgorithmExecution;
import stsc.algorithms.EodExecutionSignal;
import stsc.algorithms.TestingEodAlgorithm;
import stsc.algorithms.TestingAlgorithmSignal;
import stsc.common.UnitedFormatStock;
import stsc.storage.InMemoryStockStorage;
import stsc.storage.SignalsStorage;
import stsc.storage.StockStorage;
import junit.framework.TestCase;

public class MarketSimulatorTest extends TestCase {
	private void csvReaderHelper(StockStorage ss, String stockName) throws IOException, ParseException {
		ss.updateStock(UnitedFormatStock.readFromCsvFile(stockName, "./test_data/market_simulator_tests/" + stockName
				+ ".csv"));
	}

	public void testMarketSimulator() throws Exception {

		StockStorage ss = new InMemoryStockStorage();

		csvReaderHelper(ss, "aapl");
		csvReaderHelper(ss, "gfi");
		csvReaderHelper(ss, "oldstock");
		csvReaderHelper(ss, "no30");

		MarketSimulatorSettings settings = new MarketSimulatorSettings();
		settings.setStockStorage(ss);
		settings.setBroker(new Broker(ss));
		settings.setFrom("30-10-2013");
		settings.setTo("06-11-2013");
		settings.getExecutionsList().add(new EodAlgorithmExecution("e1", TestingEodAlgorithm.class.getName()));
		settings.getStockList().add("aapl");
		settings.getStockList().add("gfi");
		settings.getStockList().add("no30");
		settings.getStockList().add("unexisted_stock");
		settings.getStockList().add("oldstock");

		MarketSimulator marketSimulator = new MarketSimulator(settings);
		marketSimulator.simulate();
		assertEquals(1, marketSimulator.getTradeAlgorithms().size());

		TestingEodAlgorithm ta = (TestingEodAlgorithm) marketSimulator.getTradeAlgorithms().get("e1");
		assertEquals(ta.datafeeds.size(), 7);

		int[] expectedDatafeedSizes = { 1, 1, 2, 2, 3, 2, 0 };

		for (int i = 0; i < expectedDatafeedSizes.length; ++i)
			assertEquals(expectedDatafeedSizes[i], ta.datafeeds.get(i).size());

		SignalsStorage signalsStorage = marketSimulator.getSignalsStorage();
		EodExecutionSignal e1s1 = signalsStorage.getSignal("e1", Date.valueOf("2013-10-30"));
		assertEquals(true, e1s1.getClass() == TestingAlgorithmSignal.class);
		assertEquals("2013-10-30", ((TestingAlgorithmSignal) e1s1).dateRepresentation);

		EodExecutionSignal e1s2 = signalsStorage.getSignal("e1", Date.valueOf("2013-10-31"));
		assertEquals(true, e1s2.getClass() == TestingAlgorithmSignal.class);
		assertEquals("2013-10-31", ((TestingAlgorithmSignal) e1s2).dateRepresentation);

		EodExecutionSignal e1s3 = signalsStorage.getSignal("e1", Date.valueOf("2013-11-01"));
		assertEquals(true, e1s3.getClass() == TestingAlgorithmSignal.class);
		assertEquals("2013-11-01", ((TestingAlgorithmSignal) e1s3).dateRepresentation);

		EodExecutionSignal e1s6 = signalsStorage.getSignal("e1", Date.valueOf("2013-11-05"));
		assertEquals(true, e1s6.getClass() == TestingAlgorithmSignal.class);
		assertEquals("2013-11-05", ((TestingAlgorithmSignal) e1s6).dateRepresentation);

		assertNull(signalsStorage.getSignal("e1", Date.valueOf("2013-11-06")));
		assertNull(signalsStorage.getSignal("e2", Date.valueOf("2013-11-03")));
		assertNull(signalsStorage.getSignal("e1", Date.valueOf("2013-10-29")));
	}
}
