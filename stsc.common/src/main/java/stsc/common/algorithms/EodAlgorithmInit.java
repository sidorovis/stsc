package stsc.common.algorithms;

import java.util.Date;

import stsc.common.BadSignalException;
import stsc.common.signals.Signal;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.StockSignal;
import stsc.common.storage.SignalsStorage;
import stsc.common.trading.Broker;

public class EodAlgorithmInit {

	final String executionName;
	final SignalsStorage signalsStorage;
	final AlgorithmSettings settings;

	private final Broker broker;

	public EodAlgorithmInit(String executionName, SignalsStorage signalsStorage, AlgorithmSettings settings, Broker broker) {
		this.executionName = executionName;
		this.signalsStorage = signalsStorage;
		this.settings = settings;
		this.broker = broker;
	}

	protected final Signal<? extends StockSignal> getSignal(String executionName, Date date) {
		return signalsStorage.getEodSignal(executionName, date);
	}

	protected final Signal<? extends StockSignal> getSignal(String executionName, int index) {
		return signalsStorage.getEodSignal(executionName, index);
	}

	protected final Signal<? extends StockSignal> getSignal(String stockName, String executionName, Date date) {
		return signalsStorage.getStockSignal(stockName, executionName, date);
	}

	protected final Signal<? extends StockSignal> getSignal(String stockName, String executionName, int index) {
		return signalsStorage.getStockSignal(stockName, executionName, index);
	}

	protected final void registerEodSignalsType(SignalsSerie<StockSignal> serie) {
		signalsStorage.registerEodAlgorithmSerie(executionName, serie);
	}

	protected final void addSignal(Date date, StockSignal signal) throws BadSignalException {
		signalsStorage.addEodSignal(executionName, date, signal);
	}

	protected final int getIndexSize() {
		return signalsStorage.getIndexSize(executionName);
	}

	public final String getExecutionName() {
		return executionName;
	}

	public final AlgorithmSettings getSettings() {
		return settings;
	}

	public Broker getBroker() {
		return broker;
	}

}