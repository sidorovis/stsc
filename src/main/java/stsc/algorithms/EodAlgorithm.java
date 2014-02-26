package stsc.algorithms;

import java.util.Date;
import java.util.HashMap;

import stsc.common.Day;
import stsc.storage.BadSignalException;
import stsc.storage.SignalsStorage;
import stsc.trading.Broker;

public abstract class EodAlgorithm implements EodAlgorithmInterface {

	protected Broker broker;
	private String executionName;
	private SignalsStorage signalsStorage;

	@Override
	public final void setBroker(Broker broker) {
		this.broker = broker;
	}

	@Override
	public final void setExecutionName(String executionName) {
		this.executionName = executionName;
	}

	public final void setSignalsStorage(SignalsStorage signalsStorage) {
		this.signalsStorage = signalsStorage;
		signalsStorage.registerSignalsFromExecution(executionName, registerSignalsClass());
	}

	protected final void addSignal(Date date, EodExecutionSignal signal) throws BadSignalException {
		signalsStorage.addSignal(executionName, date, signal);
	}

	protected final EodExecutionSignal getSignal(Date date) {
		return signalsStorage.getSignal(executionName, date);
	}

	public abstract Class<? extends EodExecutionSignal> registerSignalsClass();
	public abstract void process(Date date, HashMap<String, Day> datafeed);

}
