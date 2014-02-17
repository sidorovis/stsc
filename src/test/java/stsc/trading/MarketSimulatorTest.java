package stsc.trading;

import java.io.IOException;
import java.text.ParseException;

import stsc.algorithms.TestAlgorithm;
import stsc.common.UnitedFormatStock;
import stsc.storage.InMemoryStockStorage;
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
		settings.setBroker(new Broker());
		settings.setFrom("30-10-2013");
		settings.setTo("06-11-2013");
		settings.getAlgorithmList().add(TestAlgorithm.class.getName());
		settings.getStockList().add("aapl");
		settings.getStockList().add("gfi");
		settings.getStockList().add("no30");
		settings.getStockList().add("unexisted_stock");
		settings.getStockList().add("oldstock");

		MarketSimulator marketSimulator = new MarketSimulator(settings);
		marketSimulator.simulate();
		assertEquals(1, marketSimulator.getTradeAlgorithms().size());
		
		TestAlgorithm ta = (TestAlgorithm) marketSimulator.getTradeAlgorithms().get(0);
		assertEquals(ta.datafeeds.size(), 7);
		
		int[] expectedDatafeedSizes = {1,1,2,2,3,2,0};
		
		for(int i = 0; i < expectedDatafeedSizes.length ; ++i)
			assertEquals(expectedDatafeedSizes[i], ta.datafeeds.get(i).size());
	}
}
