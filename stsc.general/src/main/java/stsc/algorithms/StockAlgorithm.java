package stsc.algorithms;

import java.util.Date;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.signals.Signal;
import stsc.signals.SignalsSerie;
import stsc.signals.StockSignal;

public abstract class StockAlgorithm {

	private final StockAlgorithmInit init;

	public StockAlgorithm(final StockAlgorithmInit initialize) throws BadAlgorithmException {
		this.init = initialize;
		signalSerieRegistration(initialize);
	}

	private void signalSerieRegistration(final StockAlgorithmInit initialize) throws BadAlgorithmException {
		init.signalsStorage.registerStockAlgorithmSerie(init.stockName, init.executionName, registerSignalsClass(initialize));
	}

	protected final void addSignal(Date date, StockSignal signal) throws BadSignalException {
		init.addSignal(date, signal);
	}

	protected final Signal<? extends StockSignal> getSignal(final Date date) {
		return init.getSignal(date);
	}

	protected final Signal<? extends StockSignal> getSignal(final String executionName, final Date date) {
		return init.getSignal(executionName, date);
	}

	protected final Signal<? extends StockSignal> getSignal(final int index) {
		return init.getSignal(index);
	}

	protected final Signal<? extends StockSignal> getSignal(final String executionName, final int index) {
		return init.getSignal(executionName, index);
	}

	protected final int getCurrentIndex() {
		return init.getIndexSize();
	}

	protected final int getIndexForStock(final String stockName) {
		return init.getIndexSize(stockName);
	}

	public abstract SignalsSerie<StockSignal> registerSignalsClass(final StockAlgorithmInit initialize) throws BadAlgorithmException;

	public abstract void process(Day day) throws BadSignalException;

	@Override
	public String toString() {
		return init.toString();
	}

}
