package stsc.statistic;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.joda.time.LocalDate;

import stsc.common.Stock;
import stsc.common.UnitedFormatStock;
import stsc.trading.Side;
import stsc.trading.TradingLog;
import junit.framework.TestCase;

public class StatisticsProcessorTest extends TestCase {

	private static boolean stocksLoaded = false;
	private static Stock aapl;
	private static Stock adm;
	private static Stock spy;

	private void loadStocksForTest() throws IOException {
		if (stocksLoaded)
			return;
		aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		adm = UnitedFormatStock.readFromUniteFormatFile("./test_data/adm.uf");
		spy = UnitedFormatStock.readFromUniteFormatFile("./test_data/spy.uf");
		stocksLoaded = true;
	}

	public void testStatistics() throws Exception {

		loadStocksForTest();

		int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2013, 9, 4).toDate());

		TradingLog tradingLog = new TradingLog();

		StatisticsProcessor statistics = new StatisticsProcessor(tradingLog);

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

		Statistics statisticsData = statistics.calculate();

		assertEquals(2, statisticsData.getPeriod());
		assertTrue(StatisticsProcessor.isDoubleEqual(0.246987, statisticsData.getAvGain()));
	}

	// TODO fix tests
//	public void testReverseStatistics() throws Exception {
//		loadStocksForTest();
//
//		int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
//		int admIndex = adm.findDayIndex(new LocalDate(2013, 9, 4).toDate());
//
//		TradingLog tradingLog = new TradingLog();
//
//		StatisticsProcessor statistics = new StatisticsProcessor(tradingLog);
//
//		statistics.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
//		statistics.setStockDay("adm", adm.getDays().get(admIndex++));
//
//		tradingLog.addBuyRecord(new Date(), "aapl", Side.SHORT, 100);
//		tradingLog.addBuyRecord(new Date(), "adm", Side.LONG, 200);
//
//		statistics.processEod();
//
//		statistics.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
//		statistics.setStockDay("adm", adm.getDays().get(admIndex++));
//
//		tradingLog.addSellRecord(new Date(), "aapl", Side.SHORT, 100);
//		tradingLog.addSellRecord(new Date(), "adm", Side.LONG, 200);
//
//		statistics.processEod();
//
//		Statistics statisticsData = statistics.calculate();
//
//		assertEquals(2, statisticsData.getPeriod());
//		assertTrue(StatisticsProcessor.isDoubleEqual(0.005255, statisticsData.getAvGain()));
//	}
// TODO
//	public void testProbabilityStatistics() throws IOException, StatisticsCalculationException {
//		loadStocksForTest();
//
//		int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
//		int admIndex = adm.findDayIndex(new LocalDate(2013, 9, 4).toDate());
//		int spyIndex = spy.findDayIndex(new LocalDate(2013, 9, 4).toDate());
//
//		TradingLog tradingLog = new TradingLog();
//
//		StatisticsProcessor statistics = new StatisticsProcessor(tradingLog);
//
//		statistics.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
//		statistics.setStockDay("adm", adm.getDays().get(admIndex++));
//		statistics.setStockDay("spy", spy.getDays().get(spyIndex++));
//
//		tradingLog.addBuyRecord(new Date(), "aapl", Side.SHORT, 100);
//		tradingLog.addBuyRecord(new Date(), "adm", Side.LONG, 200);
//		tradingLog.addBuyRecord(new Date(), "spy", Side.SHORT, 30);
//
//		statistics.processEod();
//
//		statistics.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
//		statistics.setStockDay("adm", adm.getDays().get(admIndex++));
//		spyIndex++;
//
//		tradingLog.addBuyRecord(new Date(), "aapl", Side.SHORT, 100);
//		tradingLog.addBuyRecord(new Date(), "adm", Side.LONG, 500);
//
//		statistics.processEod();
//
//		statistics.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
//		statistics.setStockDay("adm", adm.getDays().get(admIndex++));
//		statistics.setStockDay("spy", spy.getDays().get(spyIndex++));
//
//		statistics.processEod();
//
//		tradingLog.addSellRecord(new Date(), "aapl", Side.SHORT, 200);
//		tradingLog.addSellRecord(new Date(), "adm", Side.LONG, 700);
//		tradingLog.addSellRecord(new Date(), "spy", Side.SHORT, 30);
//
//		statistics.processEod();
//
//		Statistics statisticsData = statistics.calculate();
//
//		assertEquals(4, statisticsData.getPeriod());
//		assertTrue(StatisticsProcessor.isDoubleEqual(.345697, statisticsData.getAvGain()));
//
//		assertTrue(StatisticsProcessor.isDoubleEqual(.75, statisticsData.getFreq()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(0.666666, statisticsData.getWinProb()));
//
//		assertTrue(StatisticsProcessor.isDoubleEqual(256.0, statisticsData.getAvWin()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(62.4, statisticsData.getAvLoss()));
//
//		assertTrue(StatisticsProcessor.isDoubleEqual(293.0, statisticsData.getMaxWin()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(62.4, statisticsData.getMaxLoss()));
//
//		assertTrue(StatisticsProcessor.isDoubleEqual(4.102564, statisticsData.getAvWinAvLoss()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(0.585417, statisticsData.getKelly()));
//	}
// TODO
//	private void testEquityCurveOn518DaysStatistics() throws IOException, StatisticsCalculationException {
//		Statistics stats = testTradingHelper(518, true);
//
//		assertTrue(StatisticsProcessor.isDoubleEqual(-13.738679, stats.getAvGain()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(0.301158, stats.getFreq()));
//
//		assertTrue(StatisticsProcessor.isDoubleEqual(358.816901, stats.getAvWin()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(-0.121142, stats.getKelly()));
//
//		assertTrue(StatisticsProcessor.isDoubleEqual(-0.861926, stats.getSharpeRatio()));
//
//		assertTrue(StatisticsProcessor.isDoubleEqual(-0.549547, stats.getStartMonthAvGain()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(3.590038, stats.getStartMonthStdDevGain()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(5.136049, stats.getStartMonthMax()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(-8.821443, stats.getStartMonthMin()));
//
//		assertTrue(StatisticsProcessor.isDoubleEqual(-9.059939, stats.getMonth12AvGain()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(7.121803, stats.getMonth12StdDevGain()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(8.732297, stats.getMonth12Max()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(-18.664019, stats.getMonth12Min()));
//
//		assertTrue(StatisticsProcessor.isDoubleEqual(71.1, stats.getDdDurationAvGain()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(554.0, stats.getDdDurationMax()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(6.470206, stats.getDdValueAvGain()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(32.700528, stats.getDdValueMax()));
//	}
// TODO
//	private void testEquityCurveOn251DaysStatistics() throws IOException, StatisticsCalculationException {
//		Statistics stats = testTradingHelper(251, true);
//
//		assertTrue(StatisticsProcessor.isDoubleEqual(12.609344, stats.getAvGain()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(0.310756, stats.getFreq()));
//
//		assertTrue(StatisticsProcessor.isDoubleEqual(413.166666, stats.getAvWin()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(0.133738, stats.getKelly()));
//
//		assertTrue(StatisticsProcessor.isDoubleEqual(0.553627, stats.getSharpeRatio()));
//
//		assertTrue(StatisticsProcessor.isDoubleEqual(1.050778, stats.getStartMonthAvGain()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(3.729215, stats.getStartMonthStdDevGain()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(7.416400, stats.getStartMonthMax()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(-4.973523, stats.getStartMonthMin()));
//
//		assertTrue(StatisticsProcessor.isDoubleEqual(12.609344, stats.getMonth12AvGain()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(0.0, stats.getMonth12StdDevGain()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(12.609344, stats.getMonth12Max()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(0.0, stats.getMonth12Min()));
//
//		assertTrue(StatisticsProcessor.isDoubleEqual(32.4, stats.getDdDurationAvGain()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(167.0, stats.getDdDurationMax()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(6.098124, stats.getDdValueAvGain()));
//		assertTrue(StatisticsProcessor.isDoubleEqual(19.762441, stats.getDdValueMax()));
//	}

	public void testStatisticsOnLastClose() throws IOException, StatisticsCalculationException,
			IllegalArgumentException, IllegalAccessException {
		final Statistics stats = testTradingHelper(3, false);
		stats.print("./test/out.csv");

		assertTrue(StatisticsProcessor.isDoubleEqual(2.595008, stats.getDdValueMax()));
		final File file = new File("./test/out.csv");
		assertTrue(file.exists());
		assertEquals(461, file.length());
		file.delete();
	}

	private Statistics testTradingHelper(int daysCount, boolean closeOnExit) throws IOException,
			StatisticsCalculationException {

		loadStocksForTest();

		int aaplIndex = aapl.findDayIndex(new LocalDate(2008, 9, 4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2008, 9, 4).toDate());
		int spyIndex = spy.findDayIndex(new LocalDate(2008, 9, 4).toDate());

		TradingLog tradingLog = new TradingLog();

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
		assertEquals(daysCount, stats.getPeriod());

		return stats;
	}
}
