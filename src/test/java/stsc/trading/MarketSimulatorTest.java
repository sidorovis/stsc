package stsc.trading;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import stsc.common.UnitedFormatStock;
import stsc.storage.StockStorage;
import stsc.storage.YahooFileStockStorage;
import junit.framework.TestCase;

public class MarketSimulatorTest extends TestCase {
	private void csvReaderHelper(StockStorage ss, String stockName) throws IOException, ParseException{
		ss.updateStock(UnitedFormatStock.readFromCsvFile(stockName, "./test_data/market_simulator_tests/"+stockName+".csv"));
	}
	public void testMarketSimulator() throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			ParseException, IOException, InterruptedException {

		StockStorage ss = YahooFileStockStorage.newInMemoryStockStorage();

		csvReaderHelper(ss, "aapl");
		csvReaderHelper(ss, "gfi");
		csvReaderHelper(ss, "oldstock");
		csvReaderHelper(ss, "no30");

		MarketSimulator marketSimulator = new MarketSimulator(ss);

		try {
			marketSimulator.simulate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
