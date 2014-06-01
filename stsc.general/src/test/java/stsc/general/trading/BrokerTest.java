package stsc.general.trading;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;

import stsc.common.Side;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.StockStorage;
import stsc.common.trading.Broker;
import stsc.general.trading.BrokerImpl;
import stsc.storage.ThreadSafeStockStorage;
import junit.framework.TestCase;

public class BrokerTest extends TestCase {
	private void csvReaderHelper(StockStorage ss, String stockName) throws IOException, ParseException {
		ss.updateStock(UnitedFormatStock.readFromCsvFile(stockName, "./test_data/trade_processor_tests/" + stockName
				+ ".csv"));
	}

	public void testBroker() throws IOException, ParseException {
		StockStorage stockStorage = new ThreadSafeStockStorage();

		csvReaderHelper(stockStorage, "aapl");
		csvReaderHelper(stockStorage, "gfi");
		csvReaderHelper(stockStorage, "oldstock");
		csvReaderHelper(stockStorage, "no30");

		final BrokerImpl broker = new BrokerImpl(stockStorage);
		broker.setToday(Date.valueOf("2013-10-30"));
		assertEquals(1000, broker.buy("aapl", Side.LONG, 1000));
		assertEquals(0, broker.sell("aapl", Side.SHORT, 2000));
		assertEquals(500, broker.sell("aapl", Side.LONG, 500));
		assertEquals(500, broker.sell("aapl", Side.LONG, 1000));
		assertEquals(0, broker.buy("no30", Side.LONG, 1000));
		assertEquals(0, broker.sell("no30", Side.SHORT, 2000));

		assertEquals(1000, broker.buy("aapl", Side.SHORT, 1000));
		assertEquals(500, broker.buy("aapl", Side.LONG, 500));
		
		assertEquals(500, broker.sell("aapl", Side.LONG, 1000));
		assertEquals(1000, broker.sell("aapl", Side.SHORT, 1000));

		try (FileWriter fw = new FileWriter("./test/out_file.txt")) {
			broker.getTradingLog().printOut(fw);
		}

		File out = new File("./test/out_file.txt");
		assertEquals(194, out.length());
		out.delete();
	}

	public void testBrokerTradingCalculating() throws IOException, ParseException {
		StockStorage stockStorage = new ThreadSafeStockStorage();

		csvReaderHelper(stockStorage, "aapl");
		csvReaderHelper(stockStorage, "gfi");
		csvReaderHelper(stockStorage, "oldstock");
		csvReaderHelper(stockStorage, "no30");

		Broker broker = new BrokerImpl(stockStorage);
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
