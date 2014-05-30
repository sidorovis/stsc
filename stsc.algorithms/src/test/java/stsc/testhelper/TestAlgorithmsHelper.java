package stsc.testhelper;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.algorithms.AlgorithmSettings;
import stsc.common.algorithms.EodAlgorithmInit;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.storage.SignalsStorage;
import stsc.common.trading.Broker;

public class TestAlgorithmsHelper {
	public static EodAlgorithmInit getEodAlgorithmInit() {
		return getEodAlgorithmInit(new BrokerImpl(new ThreadSafeStockStorage()));
	}

	public static EodAlgorithmInit getEodAlgorithmInit(Broker broker) {
		return getEodAlgorithmInit(broker, "eName");
	}

	public static EodAlgorithmInit getEodAlgorithmInit(Broker broker, String executionName) {
		return getEodAlgorithmInit(broker, executionName, getSettings());
	}

	public static EodAlgorithmInit getEodAlgorithmInit(Broker broker, String executionName, AlgorithmSettings settings) {
		return getEodAlgorithmInit(broker, executionName, getSettings(), new SignalsStorageImpl());
	}

	public static EodAlgorithmInit getEodAlgorithmInit(Broker broker, String executionName, AlgorithmSettings settings, SignalsStorage signalsStorage) {
		EodAlgorithmInit init = new EodAlgorithmInit();
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

	public static AlgorithmSettingsImpl getSettings() {
		return new AlgorithmSettingsImpl(TestHelper.getPeriod());
	}
}
