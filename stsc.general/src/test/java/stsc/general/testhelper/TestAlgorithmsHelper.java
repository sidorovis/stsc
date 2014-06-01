package stsc.general.testhelper;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.algorithms.AlgorithmSettings;
import stsc.common.algorithms.EodAlgorithmInit;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.storage.SignalsStorage;
import stsc.common.trading.Broker;
import stsc.general.trading.BrokerImpl;
import stsc.storage.SignalsStorageImpl;
import stsc.storage.ThreadSafeStockStorage;

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
		return new EodAlgorithmInit(executionName, signalsStorage, settings, broker);
	}

	public static StockAlgorithmInit getStockAlgorithmInit(String executionName, String stockName, SignalsStorage storage, AlgorithmSettings settings) {
		return new StockAlgorithmInit(executionName, storage, stockName, settings);
	}

	public static StockAlgorithmInit getStockAlgorithmInit(String executionName, String stockName, AlgorithmSettings settings) {
		return getStockAlgorithmInit(executionName, stockName, new SignalsStorageImpl(), settings);
	}

	public static StockAlgorithmInit getStockAlgorithmInit(String executionName) {
		return getStockAlgorithmInit(executionName, "sName", getSettings());
	}

	public static StockAlgorithmInit getStockAlgorithmInit() {
		return getStockAlgorithmInit("eName");
	}

	public static AlgorithmSettingsImpl getSettings() {
		return new AlgorithmSettingsImpl(TestHelper.getPeriod());
	}
}
