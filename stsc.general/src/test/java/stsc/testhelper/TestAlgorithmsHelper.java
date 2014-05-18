package stsc.testhelper;

import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.StockAlgorithm;
import stsc.storage.SignalsStorage;
import stsc.storage.ThreadSafeStockStorage;
import stsc.trading.Broker;

public class TestAlgorithmsHelper {
	public static EodAlgorithm.Init getEodAlgorithmInit() {
		return getEodAlgorithmInit(new Broker(new ThreadSafeStockStorage()));
	}

	public static EodAlgorithm.Init getEodAlgorithmInit(Broker broker) {
		return getEodAlgorithmInit(broker, "eName");
	}

	public static EodAlgorithm.Init getEodAlgorithmInit(Broker broker, String executionName) {
		return getEodAlgorithmInit(broker, executionName, getSettings());
	}

	public static EodAlgorithm.Init getEodAlgorithmInit(Broker broker, String executionName, AlgorithmSettings settings) {
		return getEodAlgorithmInit(broker, executionName, getSettings(), new SignalsStorage());
	}

	public static EodAlgorithm.Init getEodAlgorithmInit(Broker broker, String executionName, AlgorithmSettings settings, SignalsStorage signalsStorage) {
		EodAlgorithm.Init init = new EodAlgorithm.Init();
		init.broker = broker;
		init.executionName = executionName;
		init.settings = settings;
		init.signalsStorage = signalsStorage;
		return init;
	}

	public static StockAlgorithm.Init getStockAlgorithmInit(String executionName, String stockName, SignalsStorage storage) {
		StockAlgorithm.Init init = new StockAlgorithm.Init();
		init.executionName = executionName;
		init.settings = getSettings();
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

	public static AlgorithmSettings getSettings() {
		return new AlgorithmSettings(TestHelper.getPeriod());
	}
}
