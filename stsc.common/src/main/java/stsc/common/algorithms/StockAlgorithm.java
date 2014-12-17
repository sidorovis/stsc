package stsc.common.algorithms;

import java.util.Date;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.signals.Signal;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.StockSignal;

public abstract class StockAlgorithm {

	private final StockAlgorithmInit init;

	public StockAlgorithm(final StockAlgorithmInit init) throws BadAlgorithmException {
		this.init = init;
		signalSerieRegistration(init);
	}

	private void signalSerieRegistration(final StockAlgorithmInit init) throws BadAlgorithmException {
		init.signalsStorage.registerStockAlgorithmSerie(init.getStockName(), init.getExecutionName(), registerSignalsClass(init));
	}

	protected final void addSignal(Date date, StockSignal signal) throws BadSignalException {
		init.addSignal(date, signal);
	}

	protected final Signal<? extends StockSignal> getSignal(final Date date) {
		return init.getSignal(date);
	}

	protected final Signal<? extends StockSignal> getSignal(final int index) {
		return init.getSignal(index);
	}

	protected final Signal<? extends StockSignal> getSignal(final String executionName, final Date date) {
		return init.getSignal(executionName, date);
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
