package stsc.algorithms;

import java.util.Date;

import stsc.common.Day;
import stsc.storage.BadSignalException;
import stsc.storage.SignalsStorage;

public abstract class StockAlgorithm {

	private final String executionName;
	private final SignalsStorage signalsStorage;
	protected final AlgorithmSettings settings;

	protected StockAlgorithm(String executionName, SignalsStorage signalsStorage, AlgorithmSettings algorithmSettings) {
		this.executionName = executionName;
		this.signalsStorage = signalsStorage;
		this.settings = algorithmSettings;
		signalsStorage.registerStockSignalsType(executionName, registerSignalsClass());
	}

	protected final void addSignal(Date date, StockSignal signal) throws BadSignalException {
		signalsStorage.addStockSignal(executionName, date, signal);
	}

	protected final StockSignal getSignal(Date date) {
		return signalsStorage.getStockSignal(executionName, date). getValue();
	}

	public abstract Class<? extends StockSignal> registerSignalsClass();

	public abstract void process(String stockName, Day day) throws BadSignalException;

}
