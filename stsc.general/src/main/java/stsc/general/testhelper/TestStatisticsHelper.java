package stsc.general.testhelper;

import java.text.ParseException;
import java.util.Date;

import org.joda.time.LocalDate;

import stsc.common.Day;
import stsc.common.FromToPeriod;
import stsc.common.Side;
import stsc.common.stocks.Stock;
import stsc.common.stocks.UnitedFormatStock;
import stsc.general.statistic.Statistics;
import stsc.general.statistic.StatisticsProcessor;
import stsc.general.trading.BrokerImpl;
import stsc.general.trading.TradingLog;
import stsc.storage.mocks.StockStorageMock;

public class TestStatisticsHelper {

	public static FromToPeriod getPeriod() {
		try {
			return new FromToPeriod("01-01-2000", "31-12-2009");
		} catch (Exception e) {
		}
		return null;
	}

	public static Statistics getStatistics() throws ParseException {
		return getStatistics(100, 200);
	}

	public static Statistics getStatistics(int applSize, int admSize) throws ParseException {
		return getStatistics(applSize, admSize, Day.createDate("04-09-2013"));
	}

	public static Statistics getStatistics(int applSize, int admSize, LocalDate date) {
		return getStatistics(applSize, admSize, date.toDate());
	}

	public static Statistics getStatistics(int applSize, int admSize, Date date) {
		Statistics statisticsData = null;
		try {
			Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
			Stock adm = UnitedFormatStock.readFromUniteFormatFile("./test_data/adm.uf");

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
		}
		return statisticsData;
	}

}
