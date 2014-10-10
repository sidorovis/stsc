package stsc.general.testhelper;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.LocalDate;

import stsc.common.Day;
import stsc.common.FromToPeriod;
import stsc.common.Side;
import stsc.common.stocks.Stock;
import stsc.general.statistic.Statistics;
import stsc.general.statistic.StatisticsProcessor;
import stsc.general.trading.BrokerImpl;
import stsc.general.trading.TradingLog;
import stsc.storage.mocks.StockStorageMock;

public class TestStatisticsHelper {

	static {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}

	public static FromToPeriod getPeriod() {
		try {
			return new FromToPeriod("01-01-2000", "31-12-2009");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static Statistics getStatistics() throws ParseException {
		return getStatistics(100, 200);
	}

	public static Statistics getStatistics(int applSize, int admSize) throws ParseException {
		return getStatistics(applSize, admSize, Day.createDate("04-09-2013"), false);
	}

	public static Statistics getStatistics(int applSize, int admSize, LocalDate date) {
		return getStatistics(applSize, admSize, date, false);
	}

	public static Statistics getStatistics(int applSize, int admSize, LocalDate date, boolean debug) {
		return getStatistics(applSize, admSize, date.toDate(), debug);
	}

	public static Statistics getStatistics(int applSize, int admSize, Date date) {
		return getStatistics(applSize, admSize, date, false);
	}

	public static Statistics getStatistics(int applSize, int admSize, Date date, boolean debug) {
		Statistics statisticsData = null;
		try {
			Stock aapl = StockStorageMock.getStockStorage().getStock("aapl");
			Stock adm = StockStorageMock.getStockStorage().getStock("adm");

			int aaplIndex = aapl.findDayIndex(date);
			int admIndex = adm.findDayIndex(date);

			TradingLog tradingLog = new BrokerImpl(StockStorageMock.getStockStorage()).getTradingLog();
			StatisticsProcessor statistics = new StatisticsProcessor(tradingLog);

			statistics.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
			statistics.setStockDay("adm", adm.getDays().get(admIndex++));
			tradingLog.addBuyRecord(Day.createDate(), "aapl", Side.LONG, applSize);
			tradingLog.addBuyRecord(Day.createDate(), "adm", Side.SHORT, admSize);

			statistics.processEod();

			statistics.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
			statistics.setStockDay("adm", adm.getDays().get(admIndex++));
			tradingLog.addSellRecord(Day.createDate(), "aapl", Side.LONG, applSize);
			tradingLog.addSellRecord(Day.createDate(), "adm", Side.SHORT, admSize);

			statistics.processEod();
			statisticsData = statistics.calculate();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return statisticsData;
	}

}
