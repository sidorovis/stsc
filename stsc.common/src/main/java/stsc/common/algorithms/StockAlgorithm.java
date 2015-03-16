package stsc.common.algorithms;

import java.util.Date;
import java.util.Optional;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.signals.SignalContainer;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;

public abstract class StockAlgorithm {

	private final StockAlgorithmInit init;

	public StockAlgorithm(final StockAlgorithmInit init) throws BadAlgorithmException {
		this.init = init;
		signalSerieRegistration(init);
	}

	private void signalSerieRegistration(final StockAlgorithmInit init) throws BadAlgorithmException {
		init.signalsStorage.registerStockAlgorithmSerie(init.getStockName(), init.getExecutionName(), registerSignalsClass(init));
	}

	protected final void addSignal(Date date, SerieSignal signal) throws BadSignalException {
		init.addSignal(date, signal);
	}

	protected final SignalContainer<? extends SerieSignal> getSignal(final Date date) {
		return init.getSignal(date);
	}

	protected final SignalContainer<? extends SerieSignal> getSignal(final int index) {
		return init.getSignal(index);
	}

	protected final SignalContainer<? extends SerieSignal> getSignal(final String executionName, final Date date) {
		return init.getSignal(executionName, date);
	}

	protected final SignalContainer<? extends SerieSignal> getSignal(final String executionName, final int index) {
		return init.getSignal(executionName, index);
	}

	protected final SignalContainer<? extends SerieSignal> getSignal(String stockName, String executionName, final int index) {
		return init.getSignal(stockName, executionName, index);
	}

	protected final int getIndexForCurrentStock() {
		return init.getIndexSize();
	}

	protected final int getIndexForStock(final String stockName) {
		return init.getIndexSize(stockName);
	}

	protected final int getIndexForStock(String stockName, String executionName) {
		return init.getIndexSize(stockName, executionName);
	}

	public abstract Optional<SignalsSerie<SerieSignal>> registerSignalsClass(final StockAlgorithmInit initialize)
			throws BadAlgorithmException;

	public abstract void process(Day day) throws BadSignalException;

	@Override
	public String toString() {
		return init.toString();
	}

}
