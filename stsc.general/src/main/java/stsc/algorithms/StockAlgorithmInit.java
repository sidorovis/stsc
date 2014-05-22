package stsc.algorithms;

import java.util.Date;

import stsc.signals.BadSignalException;
import stsc.signals.Signal;
import stsc.signals.StockSignal;
import stsc.storage.SignalsStorage;

public class StockAlgorithmInit {

	public String stockName;
	public String executionName;
	public SignalsStorage signalsStorage;
	public AlgorithmSettings settings;

	final void addSignal(Date date, StockSignal signal) throws BadSignalException {
		signalsStorage.addStockSignal(stockName, executionName, date, signal);
	}

	final Signal<? extends StockSignal> getSignal(final Date date) {
		return signalsStorage.getStockSignal(stockName, executionName, date);
	}

	final Signal<? extends StockSignal> getSignal(final String executionName, final Date date) {
		return signalsStorage.getStockSignal(stockName, executionName, date);
	}

	final Signal<? extends StockSignal> getSignal(final int index) {
		return signalsStorage.getStockSignal(stockName, executionName, index);
	}

	final Signal<? extends StockSignal> getSignal(final String executionName, final int index) {
		return signalsStorage.getStockSignal(stockName, executionName, index);
	}

	final int getIndexSize() {
		return signalsStorage.getIndexSize(stockName, executionName);
	}

	final int getIndexSize(String stockName) {
		return signalsStorage.getIndexSize(stockName, executionName);
	}

	@Override
	public String toString() {
		return stockName + ": " + executionName + "\n" + settings;
	}

}
