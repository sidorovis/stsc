package stsc.algorithms.eod.privitive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.joda.time.LocalDate;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.algorithms.In;
import stsc.algorithms.eod.primitive.OpenWhileSignalAlgorithm;
import stsc.algorithms.stock.factors.primitive.Level;
import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodAlgorithmInit;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.common.storage.StockStorage;
import stsc.testhelper.TestAlgorithmsHelper;
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
		final BrokerImpl broker = new BrokerImpl(stockStorage);

		final AlgorithmSettingsImpl algoSettings = TestAlgorithmsHelper.getSettings();
		algoSettings.set("P", "10000.0");
		algoSettings.addSubExecutionName("level");
		final EodAlgorithmInit initOwsa = TestAlgorithmsHelper.getEodAlgorithmInit(broker, "eodOwsa", algoSettings, stockInit.signalsStorage);
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
