package stsc.testhelper;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

import org.joda.time.LocalDate;

import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.BadAlgorithmException;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.StockAlgorithm;
import stsc.common.FromToPeriod;
import stsc.common.Stock;
import stsc.common.UnitedFormatStock;
import stsc.simulator.multistarter.AlgorithmSettingsIteratorFactory;
import stsc.simulator.multistarter.BadParameterException;
import stsc.simulator.multistarter.MpDouble;
import stsc.simulator.multistarter.MpInteger;
import stsc.simulator.multistarter.MpString;
import stsc.simulator.multistarter.MpSubExecution;
import stsc.simulator.multistarter.grid.AlgorithmSettingsGridIterator;
import stsc.simulator.multistarter.grid.SimulatorSettingsGridIterator;
import stsc.statistic.Statistics;
import stsc.statistic.StatisticsProcessor;
import stsc.storage.AlgorithmsStorage;
import stsc.storage.SignalsStorage;
import stsc.storage.StockStorage;
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
		return getStatistics(100, 200);
	}

	public static Statistics getStatistics(int applSize, int admSize) {
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

			tradingLog.addBuyRecord(new Date(), "aapl", Side.LONG, applSize);
			tradingLog.addBuyRecord(new Date(), "adm", Side.SHORT, admSize);

			statistics.processEod();

			statistics.setStockDay("aapl", aapl.getDays().get(aaplIndex++));
			statistics.setStockDay("adm", adm.getDays().get(admIndex++));

			tradingLog.addSellRecord(new Date(), "aapl", Side.LONG, applSize);
			tradingLog.addSellRecord(new Date(), "adm", Side.SHORT, admSize);

			statistics.processEod();

			statisticsData = statistics.calculate();

		} catch (Exception e) {
		}
		return statisticsData;
	}

	private static String algoStockName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getStock(aname).getName();
	}

	private static String algoEodName(String aname) throws BadAlgorithmException {
		return AlgorithmsStorage.getInstance().getEod(aname).getName();
	}

	public static SimulatorSettingsGridIterator getSimulatorSettingsGridIterator() {
		final StockStorage stockStorage = new ThreadSafeStockStorage();
		try {
			stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/aapl.uf"));
			stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/adm.uf"));
			stockStorage.updateStock(UnitedFormatStock.readFromUniteFormatFile("./test_data/spy.uf"));
		} catch (IOException e) {
		}
		return getSimulatorSettingsGridIterator(stockStorage);
	}

	public static SimulatorSettingsGridIterator getSimulatorSettingsGridIterator(final StockStorage stockStorage) {
		try {
			final FromToPeriod period = new FromToPeriod("01-01-2000", "31-03-2000");

			final SimulatorSettingsGridIterator settings = new SimulatorSettingsGridIterator(stockStorage, period);

			final AlgorithmSettingsIteratorFactory factoryIn = new AlgorithmSettingsIteratorFactory(period);
			factoryIn.add(new MpString("e", Arrays.asList(new String[] { "open", "high", "low", "close", "value" })));
			final AlgorithmSettingsGridIterator in = factoryIn.getGridIterator();
			settings.addStock("in", algoStockName("In"), in);

			final AlgorithmSettingsIteratorFactory factoryEma = new AlgorithmSettingsIteratorFactory(period);
			factoryEma.add(new MpDouble("P", 0.1, 0.6, 0.4));
			factoryEma.add(new MpSubExecution("", Arrays.asList(new String[] { "in" })));
			final AlgorithmSettingsGridIterator ema = factoryEma.getGridIterator();
			settings.addStock("ema", algoStockName("Ema"), ema);

			final AlgorithmSettingsIteratorFactory factoryLevel = new AlgorithmSettingsIteratorFactory(period);
			factoryLevel.add(new MpDouble("f", 15.0, 20.0, 4.0));
			factoryLevel.add(new MpSubExecution("", Arrays.asList(new String[] { "ema", "in" })));
			final AlgorithmSettingsGridIterator level = factoryLevel.getGridIterator();
			settings.addStock("level", algoStockName("Level"), level);

			final AlgorithmSettingsIteratorFactory factoryOneSide = new AlgorithmSettingsIteratorFactory(period);
			factoryOneSide.add(new MpString("side", Arrays.asList(new String[] { "long", "short" })));
			final AlgorithmSettingsGridIterator oneSide = factoryOneSide.getGridIterator();
			settings.addEod("os", algoEodName("OneSideOpenAlgorithm"), oneSide);

			final AlgorithmSettingsIteratorFactory factoryPositionSide = new AlgorithmSettingsIteratorFactory(period);
			factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new String[] { "ema", "level", "in" })));
			factoryPositionSide.add(new MpSubExecution("", Arrays.asList(new String[] { "level", "ema" })));
			factoryPositionSide.add(new MpInteger("n", 1, 32, 10));
			factoryPositionSide.add(new MpInteger("m", 1, 32, 10));
			factoryPositionSide.add(new MpDouble("ps", 50000.0, 200001.0, 50000.0));
			final AlgorithmSettingsGridIterator positionSide = factoryPositionSide.getGridIterator();
			settings.addEod("pnm", algoEodName("PositionNDayMStocks"), positionSide);
			return settings;
		} catch (BadParameterException | BadAlgorithmException | ParseException e) {
		}
		return null;
	}
}
