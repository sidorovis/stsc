package stsc.integration.tests.algorithms.eod.privitive;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;

import stsc.algorithms.Input;
import stsc.algorithms.eod.primitive.OpenWhileSignalAlgorithm;
import stsc.algorithms.stock.indices.primitive.Level;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.StockStorage;
import stsc.general.trading.BrokerImpl;
import stsc.general.trading.TradingLog;
import stsc.general.trading.TradingRecord.TradingType;
import stsc.integration.tests.helper.EodAlgoInitHelper;
import stsc.integration.tests.helper.StockAlgoInitHelper;
import stsc.storage.ThreadSafeStockStorage;

public class OpenWhileSignalAlgorithmTest {

	@Test
	public void testOpenWhileSignalAlgorithm() throws BadAlgorithmException, IOException, BadSignalException, ParseException {
		final StockAlgoInitHelper inInit = new StockAlgoInitHelper("in", "aapl");
		inInit.getSettings().setString("e", "open");
		final Input in = new Input(inInit.getInit());

		final StockAlgoInitHelper levelInit = new StockAlgoInitHelper("level", "aapl", inInit.getStorage());
		levelInit.getSettings().addSubExecutionName("in");
		levelInit.getSettings().setDouble("f", 667.0);
		final Level level = new Level(levelInit.getInit());

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final StockStorage stockStorage = new ThreadSafeStockStorage();
		stockStorage.updateStock(aapl);
		final BrokerImpl broker = new BrokerImpl(stockStorage);

		final EodAlgoInitHelper initOwsa = new EodAlgoInitHelper("eodOwsa", inInit.getStorage(), broker);
		initOwsa.getSettings().setDouble("P", 10000.0);
		initOwsa.getSettings().addSubExecutionName("level");
		final OpenWhileSignalAlgorithm eodOwsa = new OpenWhileSignalAlgorithm(initOwsa.getInit());

		final int aaplIndex = aapl.findDayIndex(new LocalDate(2011, 9, 4).toDate());
		final ArrayList<Day> days = aapl.getDays();

		for (int i = aaplIndex; i < days.size(); ++i) {
			final Day day = days.get(i);
			in.process(day);
			level.process(day);
			broker.setToday(day.getDate());
			HashMap<String, Day> datafeed = new HashMap<>();
			datafeed.put("aapl", day);
			eodOwsa.process(day.getDate(), datafeed);
		}

		final TradingLog tl = broker.getTradingLog();
		Assert.assertEquals(2, tl.getRecords().size());
		Assert.assertEquals(tl.getRecords().get(0).getAmount(), tl.getRecords().get(1).getAmount());
		Assert.assertEquals(tl.getRecords().get(0).getStockName(), tl.getRecords().get(1).getStockName());
		Assert.assertEquals(TradingType.BUY, tl.getRecords().get(0).getType());
		Assert.assertEquals(TradingType.SELL, tl.getRecords().get(1).getType());
	}
}
