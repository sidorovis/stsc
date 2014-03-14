package stsc.storage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import stsc.algorithms.EodSignal;
import stsc.algorithms.StockSignal;

/**
 * @mark Thread Safe
 * 
 */
public class SignalsStorage {

	static public class Handler<SignalType> {
		final int index;
		final Date date;
		final SignalType signal;

		public Handler(final int index, final Date date, final SignalType signal) {
			this.index = index;
			this.date = date;
			this.signal = signal;
		}

		@SuppressWarnings("unchecked")
		public <T> T getSignal(Class<T> expectedClass) {
			if (signal.getClass() == expectedClass) {
				return (T) signal;
			} else
				return null;
		}

		public SignalType getValue() {
			return signal;
		}
	}

	private class ExecutionSignalsStorage<SignalType> {

		private final Class<? extends SignalType> signalClass;

		private final ArrayList<Handler<? extends SignalType>> signalList = new ArrayList<>();
		private final HashMap<Date, Handler<? extends SignalType>> signalMap = new HashMap<>();

		public ExecutionSignalsStorage(Class<? extends SignalType> signalClass) {
			this.signalClass = signalClass;
		}

		public Handler<? extends SignalType> getSignal(final Date date) {
			synchronized (this) {
				return signalMap.get(date);
			}
		}

		public void addSignal(Date date, SignalType signal) throws BadSignalException {
			if (signal.getClass() == signalClass)
				checkedAddSignal(date, signal);
			else
				throw new BadSignalException("bad signal type, expected(" + signalClass.getCanonicalName()
						+ "), received(" + signal.getClass().getCanonicalName() + ")");
		}

		private void checkedAddSignal(Date date, SignalType signal) {
			synchronized (this) {
				final int newIndex = signalList.size();
				Handler<SignalType> newHandler = new Handler<SignalType>(newIndex, date, signal);
				signalList.add(newHandler);
				signalMap.put(date, newHandler);
			}
		}
	}

	private HashMap<String, ExecutionSignalsStorage<StockSignal>> stockSignals = new HashMap<>();
	private HashMap<String, ExecutionSignalsStorage<EodSignal>> eodSignals = new HashMap<>();

	public void registerStockSignalsType(String executionName, Class<? extends StockSignal> signalsClass) {
		if (signalsClass != null)
			synchronized (stockSignals) {
				stockSignals.put(executionName, new ExecutionSignalsStorage<StockSignal>(signalsClass));
			}
	}

	public void addStockSignal(String executionName, Date date, StockSignal signal) throws BadSignalException {
		synchronized (stockSignals) {
			stockSignals.get(executionName).addSignal(date, signal);
		}
	}

	public Handler<? extends StockSignal> getStockSignal(final String executionName, final Date date) {
		ExecutionSignalsStorage<StockSignal> ess = stockSignals.get(executionName);
		synchronized (stockSignals) {
			ess = stockSignals.get(executionName);
		}
		if (ess != null)
			return ess.getSignal(date);
		return null;
	}

	public void registerEodSignalsType(String executionName, Class<? extends EodSignal> signalsClass) {
		if (signalsClass != null)
			synchronized (eodSignals) {
				eodSignals.put(executionName, new ExecutionSignalsStorage<EodSignal>(signalsClass));
			}
	}

	public void addEodSignal(String executionName, Date date, EodSignal signal) throws BadSignalException {
		synchronized (eodSignals) {
			eodSignals.get(executionName).addSignal(date, signal);
		}
	}

	public Handler<? extends EodSignal> getEodSignal(String executionName, Date date) {
		ExecutionSignalsStorage<EodSignal> ess = null;
		synchronized (eodSignals) {
			ess = eodSignals.get(executionName);
		}
		if (ess != null)
			return ess.getSignal(date);
		return null;
	}
}
