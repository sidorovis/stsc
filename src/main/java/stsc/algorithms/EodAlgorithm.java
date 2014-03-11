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
		signalsStorage.registerEodSignalsType(executionName, registerSignalsClass());
	}

	protected final void addSignal(Date date, EodSignal signal) throws BadSignalException {
		signalsStorage.addEodSignal(executionName, date, signal);
	}

	protected final EodSignal getSignal(Date date) {
		return signalsStorage.getEodSignal(executionName, date);
	}

	@Override
	public abstract Class<? extends EodSignal> registerSignalsClass();

	@Override
	public abstract void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException;

}
