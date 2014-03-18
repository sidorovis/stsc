package stsc.algorithms;

import java.util.Date;

import stsc.common.Day;
import stsc.signals.BadSignalException;
import stsc.signals.StockSignal;
import stsc.storage.SignalsStorage;
import stsc.storage.SignalsStorage.Handler;

public abstract class StockAlgorithm {

	static public class Init {

		public String stockName;
		public String executionName;
		public SignalsStorage signalsStorage;
		public AlgorithmSettings settings;

		private final void registerStockSignalsType(Class<? extends StockSignal> classType) {
			signalsStorage.registerStockSignalsType(stockName, executionName, classType);
		}

		private final void addSignal(Date date, StockSignal signal) throws BadSignalException {
			signalsStorage.addStockSignal(stockName, executionName, date, signal);
		}

		private final Handler<? extends StockSignal> getSignal(final Date date) {
			return signalsStorage.getStockSignal(stockName, executionName, date);
		}

		private final Handler<? extends StockSignal> getSignal(final String executionName, final Date date) {
			return signalsStorage.getStockSignal(stockName, executionName, date);
		}

		private final Handler<? extends StockSignal> getSignal(final int index) {
			return signalsStorage.getStockSignal(stockName, executionName, index);
		}

		private final Handler<? extends StockSignal> getSignal(final String executionName, final int index) {
			return signalsStorage.getStockSignal(stockName, executionName, index);
		}

		private final int getIndexSize() {
			return signalsStorage.getIndexSize(stockName, executionName);
		}

		private final int getIndexSize(String stockName) {
			return signalsStorage.getIndexSize(stockName, executionName);
		}
	}

	private final Init init;

	public StockAlgorithm(final Init initialize) {
		init = initialize;
		init.registerStockSignalsType(registerSignalsClass());
	}

	protected final void addSignal(Date date, StockSignal signal) throws BadSignalException {
		init.addSignal(date, signal);
	}

	protected final Handler<? extends StockSignal> getSignal(final Date date) {
		return init.getSignal(date);
	}

	protected final Handler<? extends StockSignal> getSignal(final String executionName, final Date date) {
		return init.getSignal(executionName, date);
	}

	protected final Handler<? extends StockSignal> getSignal(final int index) {
		return init.getSignal(index);
	}

	protected final Handler<? extends StockSignal> getSignal(final String executionName, final int index) {
		return init.getSignal(executionName, index);
	}

	protected final int getCurrentIndex() {
		return init.getIndexSize();
	}

	protected final int getIndexForStock(final String stockName) {
		return init.getIndexSize(stockName);
	}

	public abstract Class<? extends StockSignal> registerSignalsClass();

	public abstract void process(Day day) throws BadSignalException;

}
