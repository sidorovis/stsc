package stsc.statistic;

import java.util.Date;

import org.joda.time.LocalDate;

import stsc.common.Stock;
import stsc.common.UnitedFormatStock;
import stsc.trading.Side;
import stsc.trading.TradingLog;
import junit.framework.TestCase;

public class StatisticsTest extends TestCase {
	public void testStatistics() throws Exception {
		
		Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
		Stock adm = UnitedFormatStock.readFromUniteFormatFile("./test_data/adm.uf");

		int aaplIndex = aapl.findDayIndex(new LocalDate(2013,9,4).toDate());
		int admIndex = adm.findDayIndex(new LocalDate(2013,9,4).toDate());
		
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

	}
}
