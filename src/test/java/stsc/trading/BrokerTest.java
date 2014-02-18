package stsc.trading;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;

import stsc.common.UnitedFormatStock;
import stsc.storage.InMemoryStockStorage;
import stsc.storage.StockStorage;
import junit.framework.TestCase;

public class BrokerTest extends TestCase {
	private void csvReaderHelper(StockStorage ss, String stockName) throws IOException, ParseException {
		ss.updateStock(UnitedFormatStock.readFromCsvFile(stockName, "./test_data/market_simulator_tests/" + stockName
				+ ".csv"));
	}

	public void testBroker() throws IOException, ParseException {
		StockStorage stockStorage = new InMemoryStockStorage();

		csvReaderHelper(stockStorage, "aapl");
		csvReaderHelper(stockStorage, "gfi");
		csvReaderHelper(stockStorage, "oldstock");
		csvReaderHelper(stockStorage, "no30");

		Broker broker = new Broker(stockStorage);
		broker.setToday(Date.valueOf("2013-10-30"));
		assertEquals(1000, broker.buy("aapl", Side.LONG, 1000));
		assertEquals(2000, broker.sell("aapl", Side.SHORT, 2000));
		assertEquals(0, broker.buy("no30", Side.LONG, 1000));
		assertEquals(0, broker.sell("no30", Side.SHORT, 2000));

		try (FileWriter fw = new FileWriter("./test/out_file.txt")) {
			broker.getTradingLog().printOut(fw);
		}

		File out = new File("./test/out_file.txt");
		assertEquals(57, out.length());
		out.delete();
	}

	public void testBrokerTradingCalculating() throws IOException, ParseException {
		StockStorage stockStorage = new InMemoryStockStorage();

		csvReaderHelper(stockStorage, "aapl");
		csvReaderHelper(stockStorage, "gfi");
		csvReaderHelper(stockStorage, "oldstock");
		csvReaderHelper(stockStorage, "no30");

		Broker broker = new Broker(stockStorage);
		broker.setToday(Date.valueOf("2013-10-30"));
		assertEquals(200, broker.buy("aapl", Side.LONG, 200));
		assertEquals(0, broker.buy("no30", Side.LONG, 200));

		broker.setToday(Date.valueOf("2013-11-03"));
		assertEquals(200, broker.sell("aapl", Side.LONG, 200));
		assertEquals(400, broker.buy("no30", Side.SHORT, 400));
	
		broker.setToday(Date.valueOf("2013-11-04"));
		assertEquals(400, broker.sell("no30", Side.SHORT, 400));
	}
}
