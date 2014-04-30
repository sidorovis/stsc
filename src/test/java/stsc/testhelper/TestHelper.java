package stsc.testhelper;

import java.util.Date;

import org.joda.time.LocalDate;

import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.StockAlgorithm;
import stsc.common.FromToPeriod;
import stsc.common.Stock;
import stsc.common.UnitedFormatStock;
import stsc.statistic.Statistics;
import stsc.statistic.StatisticsProcessor;
import stsc.storage.SignalsStorage;
import stsc.storage.ThreadSafeStockStorage;
import stsc.trading.Broker;
import stsc.trading.Side;
import stsc.trading.TradingLog;

public class TestHelper {
	public static EodAlgorithm.Init getEodAlgorithmInit() {
		return getEodAlgorithmInit(new Broker(new ThreadSafeStockStorage()));
	}

	public static EodAlgorithm.Init getEodAlgorithmInit(Broker broker) {
		return getEodAlgorithmInit(broker, "eName");
	}

	public static EodAlgorithm.Init getEodAlgorithmInit(Broker broker, String executionName) {
		return getEodAlgorithmInit(broker, executionName, getAlgorithmSettings());
	}

	public static EodAlgorithm.Init getEodAlgorithmInit(Broker broker, String executionName, AlgorithmSettings settings) {
		return getEodAlgorithmInit(broker, executionName, getAlgorithmSettings(), new SignalsStorage());
	}

	public static EodAlgorithm.Init getEodAlgorithmInit(Broker broker, String executionName,
			AlgorithmSettings settings, SignalsStorage signalsStorage) {
		EodAlgorithm.Init init = new EodAlgorithm.Init();
		init.broker = broker;
		init.executionName = executionName;
		init.settings = settings;
		init.signalsStorage = signalsStorage;
		return init;
	}

	public static StockAlgorithm.Init getStockAlgorithmInit(String executionName, String stockName,
			SignalsStorage storage) {
		StockAlgorithm.Init init = new StockAlgorithm.Init();
		init.executionName = executionName;
		init.settings = getAlgorithmSettings();
		init.signalsStorage = storage;
		init.stockName = stockName;
		return init;
	}

	public static StockAlgorithm.Init getStockAlgorithmInit(String executionName, String stockName) {
		return getStockAlgorithmInit(executionName, stockName, new SignalsStorage());
	}

	public static StockAlgorithm.Init getStockAlgorithmInit(String executionName) {
		return getStockAlgorithmInit(executionName, "sName");
	}

	public static StockAlgorithm.Init getStockAlgorithmInit() {
		return getStockAlgorithmInit("eName");
	}

	public static AlgorithmSettings getAlgorithmSettings() {
		return new AlgorithmSettings(getPeriod());
	}

	public static FromToPeriod getPeriod() {
		try {
			return new FromToPeriod("01-01-2000", "31-12-2009");
		} catch (Exception e) {
		}
		return null;
	}

	public static Statistics getStatistics() {
		Statistics statisticsData = null;
		try {
			Stock aapl = UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf");
			Stock adm = UnitedFormatStock.readFromUniteFormatFile("./test_data/adm.uf");

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

			statisticsData = statistics.calculate();

		} catch (Exception e) {
		}
		return statisticsData;
	}

}
