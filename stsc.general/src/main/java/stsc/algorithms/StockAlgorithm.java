package stsc.algorithms;

import java.util.Date;

import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.signals.Signal;
import stsc.signals.StockSignal;
import stsc.storage.SignalsStorage;

public abstract class StockAlgorithm {

	static public class Init {

		public String stockName;
		public String executionName;
		public SignalsStorage signalsStorage;
		public AlgorithmSettings settings;

		private final void registerStockSignalsType(SignalsSerie<StockSignal> serie) {
			signalsStorage.registerStockSignalsType(stockName, executionName, serie);
		}

		private final void addSignal(Date date, StockSignal signal) throws BadSignalException {
			signalsStorage.addStockSignal(stockName, executionName, date, signal);
		}

		private final Signal<? extends StockSignal> getSignal(final Date date) {
			return signalsStorage.getStockSignal(stockName, executionName, date);
		}

		private final Signal<? extends StockSignal> getSignal(final String executionName, final Date date) {
			return signalsStorage.getStockSignal(stockName, executionName, date);
		}

		private final Signal<? extends StockSignal> getSignal(final int index) {
			return signalsStorage.getStockSignal(stockName, executionName, index);
		}

		private final Signal<? extends StockSignal> getSignal(final String executionName, final int index) {
			return signalsStorage.getStockSignal(stockName, executionName, index);
		}

		private final int getIndexSize() {
			return signalsStorage.getIndexSize(stockName, executionName);
		}

		private final int getIndexSize(String stockName) {
			return signalsStorage.getIndexSize(stockName, executionName);
		}

		@Override
		public String toString() {
			return stockName + ": " + executionName + "\n" + settings;
		}

	}

	private final Init init;

	public StockAlgorithm(final Init initialize) throws BadAlgorithmException {
		init = initialize;
		init.registerStockSignalsType(registerSignalsClass());
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

	public abstract SignalsSerie<StockSignal> registerSignalsClass();

	public abstract void process(Day day) throws BadSignalException;

	@Override
	public String toString() {
		return init.toString();
	}

}
