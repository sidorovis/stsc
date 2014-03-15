package stsc.algorithms;

import java.util.Date;

import stsc.common.Day;
import stsc.signals.StockSignal;
import stsc.storage.BadSignalException;
import stsc.storage.SignalsStorage;
import stsc.storage.SignalsStorage.Handler;

public abstract class StockAlgorithm {

	private final String stockName;
	private final String executionName;
	private final SignalsStorage signalsStorage;
	protected final AlgorithmSettings settings;

	public StockAlgorithm(final String stockName, final String executionName, final SignalsStorage signalsStorage,
			final AlgorithmSettings algorithmSettings) {
		this.stockName = stockName;
		this.executionName = executionName;
		this.signalsStorage = signalsStorage;
		this.settings = algorithmSettings;
		signalsStorage.registerStockSignalsType(stockName, executionName, registerSignalsClass());
	}

	protected final void addSignal(Date date, StockSignal signal) throws BadSignalException {
		signalsStorage.addStockSignal(stockName, executionName, date, signal);
	}

	protected final Handler<? extends StockSignal> getSignal(final Date date) {
		return signalsStorage.getStockSignal(stockName, executionName, date);
	}

	protected final Handler<? extends StockSignal> getSignal(final String executionName, final Date date) {
		return signalsStorage.getStockSignal(stockName, executionName, date);
	}

	protected final Handler<? extends StockSignal> getSignal(final int index) {
		return signalsStorage.getStockSignal(stockName, executionName, index);
	}

	protected final Handler<? extends StockSignal> getSignal(final String executionName, final int index) {
		return signalsStorage.getStockSignal(stockName, executionName, index);
	}

	protected final int getCurrentIndex() {
		return signalsStorage.getCurrentStockIndex(stockName, executionName);
	}

	public abstract Class<? extends StockSignal> registerSignalsClass();

	public abstract void process(Day day) throws BadSignalException;

}
