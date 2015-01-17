package stsc.common.algorithms;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.signals.SignalContainer;
import stsc.common.signals.SignalsSerie;
import stsc.common.signals.SerieSignal;
import stsc.common.trading.Broker;

public abstract class EodAlgorithm {

	private final EodAlgorithmInit init;

	public EodAlgorithm(final EodAlgorithmInit init) throws BadAlgorithmException {
		this.init = init;
		init.registerEodSignalsType(registerSignalsClass(init));
	}

	protected final void addSignal(Date date, SerieSignal signal) throws BadSignalException {
		init.addSignal(date, signal);
	}

	protected final SignalContainer<? extends SerieSignal> getSignal(Date date) {
		return init.getSignal(init.getExecutionName(), date);
	}

	protected final SignalContainer<? extends SerieSignal> getSignal(int index) {
		return init.getSignal(init.getExecutionName(), index);
	}

	protected final SignalContainer<? extends SerieSignal> getSignal(String executionName, Date date) {
		return init.getSignal(executionName, date);
	}

	protected final SignalContainer<? extends SerieSignal> getSignal(String executionName, int index) {
		return init.getSignal(executionName, index);
	}

	protected final SignalContainer<? extends SerieSignal> getSignal(String stockName, String executionName, Date date) {
		return init.getSignal(stockName, executionName, date);
	}

	protected final SignalContainer<? extends SerieSignal> getSignal(String stockName, String executionName, int index) {
		return init.getSignal(stockName, executionName, index);
	}

	protected final int getCurrentIndex() {
		return init.getIndexSize();
	}

	protected Broker broker() {
		return init.getBroker();
	}

	public abstract Optional<SignalsSerie<SerieSignal>> registerSignalsClass(final EodAlgorithmInit init) throws BadAlgorithmException;

	public abstract void process(Date date, HashMap<String, Day> datafeed) throws BadSignalException;

}
