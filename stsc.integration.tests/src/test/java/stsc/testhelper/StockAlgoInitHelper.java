package stsc.testhelper;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.storage.SignalsStorage;
import stsc.storage.SignalsStorageImpl;

public class StockAlgoInitHelper {

	private final SignalsStorage signalsStorage;

	private final AlgorithmSettingsImpl settings;

	private final StockAlgorithmInit init;

	public StockAlgoInitHelper(String executionName, String stockName, SignalsStorage stockStorage) {
		this.signalsStorage = stockStorage;
		this.settings = new AlgorithmSettingsImpl(TestHelper.getPeriod());
		this.init = new StockAlgorithmInit(executionName, stockStorage, stockName, settings);
	}

	public StockAlgoInitHelper(String executionName, String stockName) {
		this(executionName, stockName, new SignalsStorageImpl());
	}

	public SignalsStorage getStorage() {
		return signalsStorage;
	}

	public AlgorithmSettingsImpl getSettings() {
		return settings;
	}

	public StockAlgorithmInit getInit() {
		return init;
	}
}
