package stsc.testhelper;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.algorithms.EodAlgorithmInit;
import stsc.common.storage.SignalsStorage;
import stsc.storage.SignalsStorageImpl;
import stsc.storage.ThreadSafeStockStorage;
import stsc.trading.BrokerImpl;

public class EodAlgoInitHelper {

	final private BrokerImpl broker;
	final private SignalsStorage storage;
	final private AlgorithmSettingsImpl settings;
	final private EodAlgorithmInit init;

	public EodAlgoInitHelper(String executionName) {
		this.storage = new SignalsStorageImpl();
		this.broker = new BrokerImpl(new ThreadSafeStockStorage());
		this.settings = new AlgorithmSettingsImpl(TestHelper.getPeriod());
		this.init = new EodAlgorithmInit(executionName, storage, settings, broker);
	}

	public EodAlgoInitHelper(String executionName, SignalsStorage storage, BrokerImpl broker) {
		this.storage = storage;
		this.broker = broker;
		this.settings = new AlgorithmSettingsImpl(TestHelper.getPeriod());
		this.init = new EodAlgorithmInit(executionName, storage, settings, broker);
	}

	public EodAlgoInitHelper(String executionName, EodAlgoInitHelper other) {
		this.storage = other.storage;
		this.broker = other.broker;
		this.settings = new AlgorithmSettingsImpl(TestHelper.getPeriod());
		this.init = new EodAlgorithmInit(executionName, storage, settings, broker);
	}

	public EodAlgorithmInit getInit() {
		return init;
	}

	public BrokerImpl getBroker() {
		return broker;
	}

	public SignalsStorage getStorage() {
		return storage;
	}

	public AlgorithmSettingsImpl getSettings() {
		return settings;
	}

}
