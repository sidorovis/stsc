package stsc.algorithms.eod.privitive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.LocalDate;

import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.In;
import stsc.algorithms.StockAlgorithmInit;
import stsc.algorithms.eod.primitive.OpenWhileSignalAlgorithm;
import stsc.algorithms.stock.factors.primitive.Level;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.Stock;
import stsc.common.StockStorage;
import stsc.common.UnitedFormatStock;
import stsc.storage.ThreadSafeStockStorage;
import stsc.testhelper.TestAlgorithmsHelper;
import stsc.trading.Broker;
import stsc.trading.TradingLog;
import stsc.trading.TradingRecord.TradingType;
import junit.framework.TestCase;

public class OpenWhileSignalAlgorithmTest extends TestCase {
	public void testOpenWhileSignalAlgorithm() throws BadAlgorithmException, IOException, BadSignalException {

		final StockAlgorithmInit stockInit = TestAlgorithmsHelper.getStockAlgorithmInit("in", "aapl");
		stockInit.settings.set("e", "open");
		final In in = new In(stockInit);

		StockAlgorithmInit levelInit = TestAlgorithmsHelper.getStockAlgorithmInit("level", "aapl", stockInit.signalsStorage);
		levelInit.settings.addSubExecutionName("in");
		levelInit.settings.set("f", "699.0");
		final Level level = new Level(levelInit);

		final Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		final StockStorage stockStorage = new ThreadSafeStockStorage();
		stockStorage.updateStock(aapl);
		final Broker broker = new Broker(stockStorage);

		final AlgorithmSettings algoSettings = TestAlgorithmsHelper.getSettings();
		algoSettings.set("P", "10000.0");
		algoSettings.addSubExecutionName("level");
		final EodAlgorithm.Init initOwsa = TestAlgorithmsHelper.getEodAlgorithmInit(broker, "eodOwsa", algoSettings, stockInit.signalsStorage);
		final OpenWhileSignalAlgorithm eodOwsa = new OpenWhileSignalAlgorithm(initOwsa);

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
		assertEquals(2, tl.getRecords().size());
		assertEquals(tl.getRecords().get(0).getAmount(), tl.getRecords().get(1).getAmount());
		assertEquals(tl.getRecords().get(0).getStockName(), tl.getRecords().get(1).getStockName());
		assertEquals(TradingType.BUY, tl.getRecords().get(0).getType());
		assertEquals(TradingType.SELL, tl.getRecords().get(1).getType());
	}
}
