package stsc.statistic;

import org.joda.time.LocalDate;

import stsc.trading.TradingLog;
import junit.framework.TestCase;

public class StatisticsTest extends TestCase {
	public void testStatistics() throws Exception {
		TradingLog tradingLog = new TradingLog();
		Statistics statistics = new Statistics(tradingLog);
		statistics.setToday(new LocalDate(2013, 9, 2).toDate());
	}
}
