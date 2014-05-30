package stsc.integration.tests.helper;

import java.text.ParseException;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.algorithms.StockAlgorithmInit;
import stsc.common.storage.SignalsStorage;
import stsc.general.storage.SignalsStorageImpl;

public class StockAlgoInitHelper {

	private final SignalsStorage signalsStorage;

	private final AlgorithmSettingsImpl settings;

	private final StockAlgorithmInit init;

	public StockAlgoInitHelper(String executionName, String stockName, SignalsStorage stockStorage) throws ParseException {
		this.signalsStorage = stockStorage;
		this.settings = new AlgorithmSettingsImpl(TestAlgorithmsHelper.getPeriod());
		this.init = new StockAlgorithmInit(executionName, stockStorage, stockName, settings);
	}

	public StockAlgoInitHelper(String executionName, String stockName) throws ParseException {
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
