package stsc.general.statistic;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.joda.time.LocalDate;

import stsc.common.Settings;
import stsc.common.Side;
import stsc.common.stocks.Stock;
import stsc.common.storage.StockStorage;
import stsc.general.statistic.Statistics;
import stsc.general.statistic.StatisticsCalculationException;
import stsc.general.statistic.StatisticsProcessor;
import stsc.general.testhelper.TestStockStorageHelper;
import stsc.general.trading.BrokerImpl;
import stsc.general.trading.TradingLog;
import junit.framework.TestCase;

public class StatisticsProcessorTest extends TestCase {

	public void testStatistics() throws Exception {
		final StockStorage stockStorage = TestStockStorageHelper.getStockStorage();
		final Stock aapl = stockStorage.getStock("aapl");
		final Stock adm = stockStorage.getStock("adm");

		int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2013, 9, 4).toDate());

		final TradingLog tradingLog = new BrokerImpl(stockStorage).getTradingLog();

		final StatisticsProcessor statistics = new StatisticsProcessor(tradingLog);

		statistics.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
		statistics.setStockDay("adm", adm.getDays().get(admIndex++));

		tradingLog.addBuyRecord(new Date(), "aapl", Side.LONG, 100);
		tradingLog.addBuyRecord(new Date(), "adm", Side.SHORT, 200);

		statistics.processEod();

		statistics.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
		statistics.setStockDay("adm", adm.getDays().get(admIndex++));

		tradingLog.addSellRecord(new Date(), "aapl", Side.LONG, 100);
		tradingLog.addSellRecord(new Date(), "adm", Side.SHORT, 200);

		statistics.processEod();

		final Statistics statisticsData = statistics.calculate();

		assertEquals(2.0, statisticsData.getPeriod());
		assertEquals(0.246987, statisticsData.getAvGain(), Settings.doubleEpsilon);
	}

	public void testReverseStatistics() throws Exception {
		final StockStorage stockStorage = TestStockStorageHelper.getStockStorage();
		final Stock aapl = stockStorage.getStock("aapl");
		final Stock adm = stockStorage.getStock("adm");

		int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2013, 9, 4).toDate());

		TradingLog tradingLog = new BrokerImpl(stockStorage).getTradingLog();

		StatisticsProcessor statistics = new StatisticsProcessor(tradingLog);

		statistics.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
		statistics.setStockDay("adm", adm.getDays().get(admIndex++));

		tradingLog.addBuyRecord(new Date(), "aapl", Side.SHORT, 100);
		tradingLog.addBuyRecord(new Date(), "adm", Side.LONG, 200);

		statistics.processEod();

		statistics.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
		statistics.setStockDay("adm", adm.getDays().get(admIndex++));

		tradingLog.addSellRecord(new Date(), "aapl", Side.SHORT, 100);
		tradingLog.addSellRecord(new Date(), "adm", Side.LONG, 200);

		statistics.processEod();

		Statistics statisticsData = statistics.calculate();

		assertEquals(2.0, statisticsData.getPeriod());
		assertEquals(0.246987, statisticsData.getAvGain(), Settings.doubleEpsilon);
	}

	public void testProbabilityStatistics() throws IOException, StatisticsCalculationException {
		final StockStorage stockStorage = TestStockStorageHelper.getStockStorage();
		final Stock aapl = stockStorage.getStock("aapl");
		final Stock adm = stockStorage.getStock("adm");
		final Stock spy = stockStorage.getStock("spy");

		int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		int spyIndex = spy.findDayIndex(new LocalDate(2013, 9, 4).toDate());

		TradingLog tradingLog = new BrokerImpl(stockStorage).getTradingLog();

		StatisticsProcessor statistics = new StatisticsProcessor(tradingLog);

		statistics.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
		statistics.setStockDay("adm", adm.getDays().get(admIndex++));
		statistics.setStockDay("spy", spy.getDays().get(spyIndex++));

		tradingLog.addBuyRecord(new Date(), "aapl", Side.SHORT, 100);
		tradingLog.addBuyRecord(new Date(), "adm", Side.LONG, 200);
		tradingLog.addBuyRecord(new Date(), "spy", Side.SHORT, 30);

		statistics.processEod();

		statistics.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
		statistics.setStockDay("adm", adm.getDays().get(admIndex++));
		spyIndex++;

		tradingLog.addBuyRecord(new Date(), "aapl", Side.SHORT, 100);
		tradingLog.addBuyRecord(new Date(), "adm", Side.LONG, 500);

		statistics.processEod();

		statistics.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
		statistics.setStockDay("adm", adm.getDays().get(admIndex++));
		statistics.setStockDay("spy", spy.getDays().get(spyIndex++));

		statistics.processEod();

		tradingLog.addSellRecord(new Date(), "aapl", Side.SHORT, 200);
		tradingLog.addSellRecord(new Date(), "adm", Side.LONG, 700);
		tradingLog.addSellRecord(new Date(), "spy", Side.SHORT, 30);

		statistics.processEod();

		final Statistics statisticsData = statistics.calculate();

		assertEquals(4.0, statisticsData.getPeriod());
		assertEquals(-0.008919, statisticsData.getAvGain(), Settings.doubleEpsilon);

		assertEquals(0.75, statisticsData.getFreq());
		assertEquals(0.666666, statisticsData.getWinProb(), Settings.doubleEpsilon);

		assertEquals(256.0, statisticsData.getAvWin(), 0.1);
		assertEquals(62.4, statisticsData.getAvLoss(), 0.1);

		assertEquals(293.0, statisticsData.getMaxWin(), 0.1);
		assertEquals(62.4, statisticsData.getMaxLoss(), 0.1);

		assertEquals(4.102564, statisticsData.getAvWinAvLoss(), Settings.doubleEpsilon);
		assertEquals(0.585417, statisticsData.getKelly(), Settings.doubleEpsilon);
	}

	public void testEquityCurveOn518DaysStatistics() throws IOException, StatisticsCalculationException {
		final Statistics stats = testTradingHelper(518, true);

		assertEquals(18.698462, stats.getAvGain(), Settings.doubleEpsilon);
		assertEquals(0.301158, stats.getFreq(), Settings.doubleEpsilon);

		assertEquals(358.816901, stats.getAvWin(), Settings.doubleEpsilon);
		assertEquals(-0.121142, stats.getKelly(), Settings.doubleEpsilon);

		assertEquals(0.272495, stats.getSharpeRatio(), Settings.doubleEpsilon);

		assertEquals(0.747938, stats.getStartMonthAvGain(), Settings.doubleEpsilon);
		assertEquals(3.977552, stats.getStartMonthStdDevGain(), Settings.doubleEpsilon);
		assertEquals(9.810158, stats.getStartMonthMax(), Settings.doubleEpsilon);
		assertEquals(-8.199444, stats.getStartMonthMin(), Settings.doubleEpsilon);

		assertEquals(13.662557, stats.getMonth12AvGain(), Settings.doubleEpsilon);
		assertEquals(8.357435, stats.getMonth12StdDevGain(), Settings.doubleEpsilon);
		assertEquals(23.939866, stats.getMonth12Max(), Settings.doubleEpsilon);
		assertEquals(-9.024049, stats.getMonth12Min(), Settings.doubleEpsilon);

		assertEquals(90.0, stats.getDdDurationAvGain(), Settings.doubleEpsilon);
		assertEquals(604.0, stats.getDdDurationMax(), Settings.doubleEpsilon);
		assertEquals(6.701683, stats.getDdValueAvGain(), Settings.doubleEpsilon);
		assertEquals(28.383005, stats.getDdValueMax(), Settings.doubleEpsilon);
	}

	public void testEquityCurveOn251DaysStatistics() throws IOException, StatisticsCalculationException {
		Statistics stats = testTradingHelper(251, true);

		assertEquals(-13.030631, stats.getAvGain(), Settings.doubleEpsilon);
		assertEquals(0.310756, stats.getFreq(), Settings.doubleEpsilon);

		assertEquals(413.166666, stats.getAvWin(), Settings.doubleEpsilon);
		assertEquals(0.133738, stats.getKelly(), Settings.doubleEpsilon);

		assertEquals(-1.036656, stats.getSharpeRatio(), Settings.doubleEpsilon);

		assertEquals(-1.085885, stats.getStartMonthAvGain(), Settings.doubleEpsilon);
		assertEquals(4.944017, stats.getStartMonthStdDevGain(), Settings.doubleEpsilon);
		assertEquals(5.149059, stats.getStartMonthMax(), Settings.doubleEpsilon);
		assertEquals(-11.839911, stats.getStartMonthMin(), Settings.doubleEpsilon);

		assertEquals(-13.030631, stats.getMonth12AvGain(), Settings.doubleEpsilon);
		assertEquals(0.0, stats.getMonth12StdDevGain(), Settings.doubleEpsilon);
		assertEquals(0.0, stats.getMonth12Max(), Settings.doubleEpsilon);
		assertEquals(-13.030631, stats.getMonth12Min(), Settings.doubleEpsilon);

		assertEquals(180.5, stats.getDdDurationAvGain(), Settings.doubleEpsilon);
		assertEquals(356.0, stats.getDdDurationMax(), Settings.doubleEpsilon);
		assertEquals(21.789883, stats.getDdValueAvGain(), Settings.doubleEpsilon);
		assertEquals(40.984757, stats.getDdValueMax(), Settings.doubleEpsilon);
	}

	public void testStatisticsOnLastClose() throws IOException, StatisticsCalculationException, IllegalArgumentException, IllegalAccessException {
		final Statistics stats = testTradingHelper(3, false);
		stats.print("./test/out.csv");

		assertEquals(2.595008, stats.getDdValueMax(), Settings.doubleEpsilon);
		final File file = new File("./test/out.csv");
		assertTrue(file.exists());
		assertEquals(461, file.length(), 0.1);
		file.delete();
	}

	private Statistics testTradingHelper(int daysCount, boolean closeOnExit) throws IOException, StatisticsCalculationException {
		final StockStorage stockStorage = TestStockStorageHelper.getStockStorage();
		final Stock aapl = stockStorage.getStock("aapl");
		final Stock adm = stockStorage.getStock("adm");
		final Stock spy = stockStorage.getStock("spy");

		int aaplIndex = aapl.findDayIndex(new LocalDate(2008, 9, 4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2008, 9, 4).toDate());
		int spyIndex = spy.findDayIndex(new LocalDate(2008, 9, 4).toDate());

		TradingLog tradingLog = new BrokerImpl(stockStorage).getTradingLog();

		StatisticsProcessor statisticsProcessor = new StatisticsProcessor(tradingLog);

		final int buySellEach = 5;
		boolean opened = false;

		for (int i = 0; i < daysCount; ++i) {

			statisticsProcessor.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
			statisticsProcessor.setStockDay("adm", adm.getDays().get(admIndex++));
			statisticsProcessor.setStockDay("spy", spy.getDays().get(spyIndex++));

			if (i % buySellEach == 0 && i % (buySellEach * 2) == 0) {
				tradingLog.addBuyRecord(new Date(), "aapl", Side.SHORT, 100);
				tradingLog.addBuyRecord(new Date(), "adm", Side.LONG, 200);
				tradingLog.addBuyRecord(new Date(), "spy", Side.SHORT, 100);
				opened = true;
			}
			if (i % buySellEach == 0 && i % (buySellEach * 2) != 0) {
				tradingLog.addSellRecord(new Date(), "aapl", Side.SHORT, 100);
				tradingLog.addSellRecord(new Date(), "adm", Side.LONG, 200);
				tradingLog.addSellRecord(new Date(), "spy", Side.SHORT, 100);
				opened = false;
			}

			if ((i == (daysCount - 1)) && opened && closeOnExit) {
				tradingLog.addSellRecord(new Date(), "aapl", Side.SHORT, 100);
				tradingLog.addSellRecord(new Date(), "adm", Side.LONG, 200);
				tradingLog.addSellRecord(new Date(), "spy", Side.SHORT, 100);
				opened = false;
			}

			statisticsProcessor.processEod();
		}

		Statistics stats = statisticsProcessor.calculate();
		assertEquals(new Double(daysCount), stats.getPeriod());

		return stats;
	}
}
