package stsc.algorithms;

import java.util.Date;
import java.util.HashMap;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.EodSignal;
import stsc.common.Signal;
import stsc.common.SignalsSerie;
import stsc.common.SignalsStorage;
import stsc.common.StockSignal;
import stsc.trading.Broker;

public abstract class EodAlgorithm {

	static public class Init {

		public String executionName;
		public SignalsStorage signalsStorage;
		public Broker broker;
		public AlgorithmSettings settings;

		protected final void registerEodSignalsType(SignalsSerie<EodSignal> serie) {
			signalsStorage.registerEodAlgorithmSerie(executionName, serie);
		}

		protected final void addSignal(Date date, EodSignal signal) throws BadSignalException {
			signalsStorage.addEodSignal(executionName, date, signal);
		}

		protected final Signal<? extends EodSignal> getSignal(final Date date) {
			return signalsStorage.getEodSignal(executionName, date);
		}

		protected final Signal<? extends EodSignal> getSignal(final String executionName, final Date date) {
			return signalsStorage.getEodSignal(executionName, date);
		}

		protected final Signal<? extends EodSignal> getSignal(final int index) {
			return signalsStorage.getEodSignal(executionName, index);
		}

		protected final Signal<? extends EodSignal> getSignal(final String executionName, final int index) {
			return signalsStorage.getEodSignal(executionName, index);
		}

		protected final int getSignalsSize() {
			return signalsStorage.getSignalsSize(executionName);
		}

		protected final Signal<? extends StockSignal> getStockSignal(String stockName, String executionName, Date date) {
			return signalsStorage.getStockSignal(stockName, executionName, date);
		}

		protected final Signal<? extends StockSignal> getStockSignal(String stockName, String executionName, int index) {
			return signalsStorage.getStockSignal(stockName, executionName, index);
		}

	}

	private final Init init;

	public EodAlgorithm(final Init init) throws BadAlgorithmException {
		this.init = init;
		init.registerEodSignalsType(registerSignalsClass(init));
	}

	protected final void addSignal(Date date, EodSignal signal) throws BadSignalException {
		init.addSignal(date, signal);
	}

	protected final EodSignal getSignal(Date date) {
		return init.getSignal(date).getValue();
	}

	protected final Signal<? extends StockSignal> getSignal(String stockName, String executionName, Date date) {
		return init.getStockSignal(stockName, executionName, date);
	}

	protected final Signal<? extends StockSignal> getSignal(String stockName, String executionName, int index) {
		return init.getStockSignal(stockName, executionName, index);
	}

	protected final Broker broker() {
		return init.broker;
	}

	public abstract SignalsSerie<EodSignal> registerSignalsClass(final Init init) throws BadAlgorithmException;

	public abstract void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException;

}
