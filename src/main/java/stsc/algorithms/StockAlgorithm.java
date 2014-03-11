package stsc.algorithms;

import java.util.Date;

import stsc.common.Day;
import stsc.storage.BadSignalException;
import stsc.storage.SignalsStorage;

public abstract class StockAlgorithm implements StockAlgorithmInterface {

	private String executionName;
	private SignalsStorage signalsStorage;

	@Override
	public final void setExecutionName(String executionName) {
		this.executionName = executionName;
	}

	@Override
	public final void setSignalsStorage(SignalsStorage signalsStorage) {
		this.signalsStorage = signalsStorage;
		signalsStorage.registerStockSignalsType(executionName, registerSignalsClass());
	}

	protected final void addSignal(Date date, StockSignal signal) throws BadSignalException {
		signalsStorage.addStockSignal(executionName, date, signal);
	}

	protected final StockSignal getSignal(Date date) {
		return signalsStorage.getStockSignal(executionName, date);
	}

	@Override
	public abstract Class<? extends StockSignal> registerSignalsClass();

	@Override
	public abstract void process(Date date, String stockName, Day day);

}
