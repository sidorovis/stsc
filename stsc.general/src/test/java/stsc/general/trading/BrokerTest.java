package stsc.general.trading;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import stsc.common.Day;
import stsc.common.Side;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.StockStorage;
import stsc.common.trading.Broker;
import stsc.storage.ThreadSafeStockStorage;

public class BrokerTest {

	private void csvReaderHelper(StockStorage ss, String stockName) throws IOException, ParseException {
		ss.updateStock(UnitedFormatStock.readFromCsvFile(stockName, "./test_data/trade_processor_tests/" + stockName + ".csv"));
	}

	@Test
	public void testBroker() throws IOException, ParseException {
		StockStorage stockStorage = new ThreadSafeStockStorage();

		csvReaderHelper(stockStorage, "aapl");
		csvReaderHelper(stockStorage, "gfi");
		csvReaderHelper(stockStorage, "oldstock");
		csvReaderHelper(stockStorage, "no30");

		final BrokerImpl broker = new BrokerImpl(stockStorage);
		broker.setToday(Day.createDate("30-10-2013"));
		Assert.assertEquals(1000, broker.buy("aapl", Side.LONG, 1000));
		Assert.assertEquals(0, broker.sell("aapl", Side.SHORT, 2000));
		Assert.assertEquals(500, broker.sell("aapl", Side.LONG, 500));
		Assert.assertEquals(500, broker.sell("aapl", Side.LONG, 1000));
		Assert.assertEquals(0, broker.buy("no30", Side.LONG, 1000));
		Assert.assertEquals(0, broker.sell("no30", Side.SHORT, 2000));

		Assert.assertEquals(1000, broker.buy("aapl", Side.SHORT, 1000));
		Assert.assertEquals(500, broker.buy("aapl", Side.LONG, 500));

		Assert.assertEquals(500, broker.sell("aapl", Side.LONG, 1000));
		Assert.assertEquals(1000, broker.sell("aapl", Side.SHORT, 1000));

		try (FileWriter fw = new FileWriter("./test/out_file.txt")) {
			broker.getTradingLog().printOut(fw);
		}

		File out = new File("./test/out_file.txt");
		Assert.assertEquals(194, out.length());
		out.delete();
	}

	@Test
	public void testBrokerTradingCalculating() throws IOException, ParseException {
		StockStorage stockStorage = new ThreadSafeStockStorage();

		csvReaderHelper(stockStorage, "aapl");
		csvReaderHelper(stockStorage, "gfi");
		csvReaderHelper(stockStorage, "oldstock");
		csvReaderHelper(stockStorage, "no30");

		Broker broker = new BrokerImpl(stockStorage);
		broker.setToday(Day.createDate("30-10-2013"));
		Assert.assertEquals(200, broker.buy("aapl", Side.LONG, 200));
		Assert.assertEquals(0, broker.buy("no30", Side.LONG, 200));

		broker.setToday(Day.createDate("03-11-2013"));
		Assert.assertEquals(200, broker.sell("aapl", Side.LONG, 200));
		Assert.assertEquals(400, broker.buy("no30", Side.SHORT, 400));

		broker.setToday(Day.createDate("04-11-2013"));
		Assert.assertEquals(400, broker.sell("no30", Side.SHORT, 400));
	}
}
