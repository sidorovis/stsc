package stsc.testhelper;

import stsc.algorithms.AlgorithmSettings;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.StockAlgorithmInit;
import stsc.common.SignalsStorage;
import stsc.storage.SignalsStorageImpl;
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
		return getEodAlgorithmInit(broker, executionName, getSettings(), new SignalsStorageImpl());
	}

	public static EodAlgorithm.Init getEodAlgorithmInit(Broker broker, String executionName, AlgorithmSettings settings, SignalsStorage signalsStorage) {
		EodAlgorithm.Init init = new EodAlgorithm.Init();
		init.broker = broker;
		init.executionName = executionName;
		init.settings = settings;
		init.signalsStorage = signalsStorage;
		return init;
	}

	public static StockAlgorithmInit getStockAlgorithmInit(String executionName, String stockName, SignalsStorage storage) {
		StockAlgorithmInit init = new StockAlgorithmInit();
		init.executionName = executionName;
		init.settings = getSettings();
		init.signalsStorage = storage;
		init.stockName = stockName;
		return init;
	}

	public static StockAlgorithmInit getStockAlgorithmInit(String executionName, String stockName) {
		return getStockAlgorithmInit(executionName, stockName, new SignalsStorageImpl());
	}

	public static StockAlgorithmInit getStockAlgorithmInit(String executionName) {
		return getStockAlgorithmInit(executionName, "sName");
	}

	public static StockAlgorithmInit getStockAlgorithmInit() {
		return getStockAlgorithmInit("eName");
	}

	public static AlgorithmSettings getSettings() {
		return new AlgorithmSettings(TestHelper.getPeriod());
	}
}
