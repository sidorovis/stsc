package stsc.general.statistic;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.joda.time.LocalDate;

import stsc.common.Day;
import stsc.common.Settings;
import stsc.common.Side;
import stsc.common.stocks.Stock;
import stsc.common.storage.StockStorage;
import stsc.general.statistic.Statistics;
import stsc.general.statistic.StatisticsProcessor;
import stsc.general.trading.BrokerImpl;
import stsc.general.trading.TradingLog;
import stsc.storage.mocks.StockStorageMock;
import junit.framework.TestCase;

public class StatisticsProcessorTest extends TestCase {

	public void testStatistics() throws Exception {
		final StockStorage stockStorage = StockStorageMock.getStockStorage();
		final Stock aapl = stockStorage.getStock("aapl");
		final Stock adm = stockStorage.getStock("adm");

		int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2013, 9, 4).toDate());

		final TradingLog tradingLog = new BrokerImpl(stockStorage).getTradingLog();

		final StatisticsProcessor statistics = new StatisticsProcessor(tradingLog);

		statistics.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
		statistics.setStockDay("adm", adm.getDays().get(admIndex++));

		tradingLog.addBuyRecord(Day.createDate(), "aapl", Side.LONG, 100);
		tradingLog.addBuyRecord(Day.createDate(), "adm", Side.SHORT, 200);

		statistics.processEod();

		statistics.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
		statistics.setStockDay("adm", adm.getDays().get(admIndex++));

		tradingLog.addSellRecord(Day.createDate(), "aapl", Side.LONG, 100);
		tradingLog.addSellRecord(Day.createDate(), "adm", Side.SHORT, 200);

		statistics.processEod();

		final Statistics statisticsData = statistics.calculate();

		assertEquals(2.0, statisticsData.getPeriod());
		assertEquals(-0.242883, statisticsData.getAvGain(), Settings.doubleEpsilon);
	}

	public void testReverseStatistics() throws Exception {
		final StockStorage stockStorage = StockStorageMock.getStockStorage();
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
		assertEquals(-0.242883, statisticsData.getAvGain(), Settings.doubleEpsilon);
	}

	public void testProbabilityStatistics() throws IOException {
		final StockStorage stockStorage = StockStorageMock.getStockStorage();
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
		tradingLog.addBuyRecord(new Date(), "spy", Side.LONG, 30);

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
		tradingLog.addSellRecord(new Date(), "spy", Side.LONG, 30);

		statistics.processEod();

		final Statistics statisticsData = statistics.calculate();

		assertEquals(4.0, statisticsData.getPeriod());
		assertEquals(0.694511, statisticsData.getAvGain(), Settings.doubleEpsilon);

		assertEquals(0.75, statisticsData.getFreq());
		assertEquals(0.333333, statisticsData.getWinProb(), Settings.doubleEpsilon);

		assertEquals(18.0, statisticsData.getAvWin(), Settings.doubleEpsilon);
		assertEquals(688, statisticsData.getAvLoss(), Settings.doubleEpsilon);

		assertEquals(18.0, statisticsData.getMaxWin(), Settings.doubleEpsilon);
		assertEquals(1131, statisticsData.getMaxLoss(), Settings.doubleEpsilon);

		assertEquals(0.026162, statisticsData.getAvWinAvLoss(), Settings.doubleEpsilon);
		assertEquals(-25.148148, statisticsData.getKelly(), Settings.doubleEpsilon);
	}

	public void testEquityCurveOn518DaysStatistics() throws IOException {
		final Statistics stats = testTradingHelper(518, true);

		assertEquals(22.790175, stats.getAvGain(), Settings.doubleEpsilon);
		assertEquals(0.301158, stats.getFreq(), Settings.doubleEpsilon);

		assertEquals(400.236111, stats.getAvWin(), Settings.doubleEpsilon);
		assertEquals(-0.123148, stats.getKelly(), Settings.doubleEpsilon);

		assertEquals(0.304066, stats.getSharpeRatio(), Settings.doubleEpsilon);

		assertEquals(0.911607, stats.getStartMonthAvGain(), Settings.doubleEpsilon);
		assertEquals(5.798013, stats.getStartMonthStdDevGain(), Settings.doubleEpsilon);
		assertEquals(10.605582, stats.getStartMonthMax(), Settings.doubleEpsilon);
		assertEquals(-14.952294, stats.getStartMonthMin(), Settings.doubleEpsilon);

		assertEquals(21.483345, stats.getMonth12AvGain(), Settings.doubleEpsilon);
		assertEquals(14.807455, stats.getMonth12StdDevGain(), Settings.doubleEpsilon);
		assertEquals(35.501103, stats.getMonth12Max(), Settings.doubleEpsilon);
		assertEquals(-16.570730, stats.getMonth12Min(), Settings.doubleEpsilon);

		assertEquals(79.0, stats.getDdDurationAvGain(), Settings.doubleEpsilon);
		assertEquals(597.0, stats.getDdDurationMax(), Settings.doubleEpsilon);
		assertEquals(7.429383, stats.getDdValueAvGain(), Settings.doubleEpsilon);
		assertEquals(37.210219, stats.getDdValueMax(), Settings.doubleEpsilon);
	}

	public void testEquityCurveOn251DaysStatistics() throws IOException {
		Statistics stats = testTradingHelper(251, true);

		assertEquals(-25.643685, stats.getAvGain(), Settings.doubleEpsilon);
		assertEquals(0.310756, stats.getFreq(), Settings.doubleEpsilon);

		assertEquals(475.761904, stats.getAvWin(), Settings.doubleEpsilon);
		assertEquals(0.177906, stats.getKelly(), Settings.doubleEpsilon);

		assertEquals(-1.003661, stats.getSharpeRatio(), Settings.doubleEpsilon);

		assertEquals(-2.136973, stats.getStartMonthAvGain(), Settings.doubleEpsilon);
		assertEquals(8.727526, stats.getStartMonthStdDevGain(), Settings.doubleEpsilon);
		assertEquals(7.641854, stats.getStartMonthMax(), Settings.doubleEpsilon);
		assertEquals(-23.139109, stats.getStartMonthMin(), Settings.doubleEpsilon);

		assertEquals(-25.643685, stats.getMonth12AvGain(), Settings.doubleEpsilon);
		assertEquals(0.0, stats.getMonth12StdDevGain(), Settings.doubleEpsilon);
		assertEquals(0.0, stats.getMonth12Max(), Settings.doubleEpsilon);
		assertEquals(-25.643685, stats.getMonth12Min(), Settings.doubleEpsilon);

		assertEquals(120.0, stats.getDdDurationAvGain(), Settings.doubleEpsilon);
		assertEquals(343.0, stats.getDdDurationMax(), Settings.doubleEpsilon);
		assertEquals(21.857840, stats.getDdValueAvGain(), Settings.doubleEpsilon);
		assertEquals(57.583892, stats.getDdValueMax(), Settings.doubleEpsilon);
	}

	public void testStatisticsOnLastClose() throws IOException, IllegalArgumentException, IllegalAccessException {
		final Statistics stats = testTradingHelper(3, false);
		stats.print("./test/out.csv");

		assertEquals(2.705918, stats.getDdValueMax(), Settings.doubleEpsilon);
		final File file = new File("./test/out.csv");
		assertTrue(file.exists());
		assertEquals(407 + 28 * 2, file.length(), 0.1);
		file.delete();
	}

	private Statistics testTradingHelper(int daysCount, boolean closeOnExit) throws IOException {
		final StockStorage stockStorage = StockStorageMock.getStockStorage();
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
