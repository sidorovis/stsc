package stsc.statistic;

import java.io.IOException;
import java.util.Date;

import org.joda.time.LocalDate;

import stsc.common.Stock;
import stsc.common.UnitedFormatStock;
import stsc.trading.Side;
import stsc.trading.TradingLog;
import junit.framework.TestCase;

public class StatisticsTest extends TestCase {

	private static boolean stocksLoaded = false;
	private static Stock aapl;
	private static Stock adm;

	private void loadStocksForTest() throws IOException{
		if (stocksLoaded)
			return;
		aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		adm = UnitedFormatStock.readFromUniteFormatFile("./test_data/adm.uf");
		stocksLoaded = true;
	}
	
	public void testStatistics() throws Exception {

		loadStocksForTest();
		
		int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2013, 9, 4).toDate());

		TradingLog tradingLog = new TradingLog();

		Statistics statistics = new Statistics(tradingLog);

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

		assertEquals(2, statistics.getEquityCurve().size());
		assertEquals(true, Math.abs(3.0 - statistics.getEquityCurve().get(1)) < 0.000001);
	}

	public void testReverseStatistics() throws Exception {

		loadStocksForTest();

		int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2013, 9, 4).toDate());

		TradingLog tradingLog = new TradingLog();

		Statistics statistics = new Statistics(tradingLog);

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

		assertEquals(2, statistics.getEquityCurve().size());
		assertEquals(true, Math.abs(-3.0 - statistics.getEquityCurve().get(1)) < 0.000001);
	}

	public void testSeveralDaysTrading() throws IOException {

		loadStocksForTest();

		int aaplIndex = aapl.findDayIndex(new LocalDate(2013, 9, 4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2013, 9, 4).toDate());

		TradingLog tradingLog = new TradingLog();

		Statistics statistics = new Statistics(tradingLog);

		statistics.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
		statistics.setStockDay("adm", adm.getDays().get(admIndex++));

		tradingLog.addBuyRecord(new Date(), "aapl", Side.SHORT, 100);
		tradingLog.addBuyRecord(new Date(), "adm", Side.LONG, 200);

		statistics.processEod();

		statistics.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
		statistics.setStockDay("adm", adm.getDays().get(admIndex++));

		tradingLog.addBuyRecord(new Date(), "aapl", Side.SHORT, 100);
		tradingLog.addBuyRecord(new Date(), "adm", Side.LONG, 500);

		statistics.processEod();

		statistics.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
		statistics.setStockDay("adm", adm.getDays().get(admIndex++));

		statistics.processEod();

		tradingLog.addSellRecord(new Date(), "aapl", Side.SHORT, 200);
		tradingLog.addSellRecord(new Date(), "adm", Side.LONG, 700);

		statistics.processEod();

		assertEquals(4, statistics.getEquityCurve().size());
		assertEquals(true, Math.abs(-512.0 - statistics.getEquityCurve().get(3)) < 0.000001);
	}
}
