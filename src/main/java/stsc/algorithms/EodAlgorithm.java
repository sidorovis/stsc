package stsc.algorithms;

import java.util.Date;
import java.util.HashMap;

import stsc.common.Day;
import stsc.signals.EodSignal;
import stsc.storage.BadSignalException;
import stsc.storage.SignalsStorage;
import stsc.trading.Broker;

public abstract class EodAlgorithm {

	private final String executionName;
	protected final Broker broker;
	private final SignalsStorage signalsStorage;
	protected final AlgorithmSettings settings;

	protected EodAlgorithm(String executionName, Broker broker, SignalsStorage signalsStorage,
			AlgorithmSettings algorithmSettings) {
		this.executionName = executionName;
		this.broker = broker;
		this.signalsStorage = signalsStorage;
		this.settings = algorithmSettings;
		signalsStorage.registerEodSignalsType(executionName, registerSignalsClass());
	}

	protected final void addSignal(Date date, EodSignal signal) throws BadSignalException {
		signalsStorage.addEodSignal(executionName, date, signal);
	}

	protected final EodSignal getSignal(Date date) {
		return signalsStorage.getEodSignal(executionName, date).getValue();
	}

	public abstract Class<? extends EodSignal> registerSignalsClass();

	public abstract void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException;

}
