package stsc.algorithms;

import java.util.Date;

import stsc.common.Day;
import stsc.storage.BadSignalException;
import stsc.storage.SignalsStorage;

public abstract class StockAlgorithm {

	private String executionName;
	private SignalsStorage signalsStorage;
	protected AlgorithmSettings algorithmSettings;

	public final void setExecutionName(String executionName) {
		this.executionName = executionName;
	}

	public final void setSignalsStorage(SignalsStorage signalsStorage) {
		this.signalsStorage = signalsStorage;
		signalsStorage.registerStockSignalsType(executionName, registerSignalsClass());
	}

	public final void setSettings(final AlgorithmSettings settings) {
		this.algorithmSettings = settings;
	}

	protected final void addSignal(Date date, StockSignal signal) throws BadSignalException {
		signalsStorage.addStockSignal(executionName, date, signal);
	}

	protected final StockSignal getSignal(Date date) {
		return signalsStorage.getStockSignal(executionName, date);
	}

	public abstract Class<? extends StockSignal> registerSignalsClass();

	public abstract void process(String stockName, Day day) throws BadSignalException;

}
