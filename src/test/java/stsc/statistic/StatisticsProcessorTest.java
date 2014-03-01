package stsc.statistic;

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
		assertEquals(true, StatisticsProcessor.isDoubleEqual(-0.005255, statisticsData.getAvGain()));
	}

	public void testReverseStatistics() throws Exception {

		loadStocksForTest();

		int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2013, 9, 4).toDate());

		TradingLog tradingLog = new TradingLog();

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

		assertEquals(2, statisticsData.getPeriod());
		assertEquals(true, StatisticsProcessor.isDoubleEqual(0.005255, statisticsData.getAvGain()));
	}

	public void testProbabilityStatistics() throws IOException, StatisticsCalculationException {

		loadStocksForTest();

		int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		int spyIndex = spy.findDayIndex(new LocalDate(2013, 9, 4).toDate());

		TradingLog tradingLog = new TradingLog();

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

		Statistics statisticsData = statistics.calculate();

		assertEquals(4, statisticsData.getPeriod());
		assertEquals(true, StatisticsProcessor.isDoubleEqual(.345697, statisticsData.getAvGain()));

		assertEquals(true, StatisticsProcessor.isDoubleEqual(.75, statisticsData.getFreq()));
		assertEquals(true, StatisticsProcessor.isDoubleEqual(0.666666, statisticsData.getWinProb()));

		assertEquals(true, StatisticsProcessor.isDoubleEqual(256.0, statisticsData.getAvWin()));
		assertEquals(true, StatisticsProcessor.isDoubleEqual(62.4, statisticsData.getAvLoss()));

		assertEquals(true, StatisticsProcessor.isDoubleEqual(4.102564, statisticsData.getAvWinAvLoss()));
		assertEquals(true, StatisticsProcessor.isDoubleEqual(0.585417, statisticsData.getKelly()));
	}

	public void testEquityCurveStatistics() throws IOException, StatisticsCalculationException {

		loadStocksForTest();

		int aaplIndex = aapl.findDayIndex(new LocalDate(2008, 9, 4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2008, 9, 4).toDate());
		int spyIndex = spy.findDayIndex(new LocalDate(2008, 9, 4).toDate());

		TradingLog tradingLog = new TradingLog();

		StatisticsProcessor statistics = new StatisticsProcessor(tradingLog);

		final int buy_sell_each = 5;
		
		for (int i = 0; i < 504; ++i) {

			statistics.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
			statistics.setStockDay("adm", adm.getDays().get(admIndex++));
			statistics.setStockDay("spy", spy.getDays().get(spyIndex++));

			if (i % buy_sell_each == 0 && i % (buy_sell_each * 2) == 0) {
				tradingLog.addBuyRecord(new Date(), "aapl", Side.SHORT, 100);
				tradingLog.addBuyRecord(new Date(), "adm", Side.LONG, 200);
				tradingLog.addBuyRecord(new Date(), "spy", Side.SHORT, 100);
			}
			if (i % buy_sell_each == 0 && i % (buy_sell_each * 2) != 0) {
				tradingLog.addSellRecord(new Date(), "aapl", Side.SHORT, 100);
				tradingLog.addSellRecord(new Date(), "adm", Side.LONG, 200);
				tradingLog.addSellRecord(new Date(), "spy", Side.SHORT, 100);
			}

			statistics.processEod();
		}

		Statistics statisticsData = statistics.calculate();

		assertEquals(504, statisticsData.getPeriod());
		assertEquals(true, StatisticsProcessor.isDoubleEqual(-3.976890, statisticsData.getAvGain()));
		assertEquals(true, StatisticsProcessor.isDoubleEqual(0.297619, statisticsData.getFreq()));
		
		assertEquals(true, StatisticsProcessor.isDoubleEqual(363.449275, statisticsData.getAvWin()));
		assertEquals(true, StatisticsProcessor.isDoubleEqual(-0.059925, statisticsData.getKelly()));
		
		
	}
}
