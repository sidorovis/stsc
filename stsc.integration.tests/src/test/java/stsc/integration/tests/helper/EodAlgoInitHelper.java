package stsc.integration.tests.helper;

import java.text.ParseException;

import stsc.algorithms.AlgorithmSettingsImpl;
import stsc.common.FromToPeriod;
import stsc.common.algorithms.EodAlgorithmInit;
import stsc.common.storage.SignalsStorage;
import stsc.general.storage.SignalsStorageImpl;
import stsc.general.storage.ThreadSafeStockStorage;
import stsc.general.trading.BrokerImpl;

public class EodAlgoInitHelper {

	final private BrokerImpl broker;
	final private SignalsStorage storage;
	final private AlgorithmSettingsImpl settings;
	final private EodAlgorithmInit init;

	public EodAlgoInitHelper(String executionName) {
		this.storage = new SignalsStorageImpl();
		this.broker = new BrokerImpl(new ThreadSafeStockStorage());
		this.settings = new AlgorithmSettingsImpl(TestAlgorithmsHelper.getPeriod());
		this.init = new EodAlgorithmInit(executionName, storage, settings, broker);
	}

	public EodAlgoInitHelper(String executionName, SignalsStorage storage, BrokerImpl broker) {
		this.storage = storage;
		this.broker = broker;
		this.settings = new AlgorithmSettingsImpl(TestAlgorithmsHelper.getPeriod());
		this.init = new EodAlgorithmInit(executionName, storage, settings, broker);
	}

	public EodAlgoInitHelper(String executionName, EodAlgoInitHelper other) throws ParseException {
		this.storage = other.storage;
		this.broker = other.broker;
		this.settings = new AlgorithmSettingsImpl(new FromToPeriod("01-01-2000", "31-12-2009"));
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
