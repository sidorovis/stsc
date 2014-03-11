package stsc.storage;

import java.util.Date;
import java.util.HashMap;

import stsc.algorithms.EodSignal;
import stsc.algorithms.StockSignal;

public class SignalsStorage {
	class ExecutionSignalsStorage<SignalType> {
		private final Class<? extends SignalType> signalClass;
		private final HashMap<Date, SignalType> signals = new HashMap<>();

		public ExecutionSignalsStorage(Class<? extends SignalType> signalClass) {
			this.signalClass = signalClass;
		}

		public HashMap<Date, SignalType> getSignals() {
			return signals;
		}

		public void addSignal(Date date, SignalType signal) throws BadSignalException {
			if (signal.getClass() == signalClass)
				signals.put(date, signal);
			else
				throw new BadSignalException("bad signal type was tried to be added expected("
						+ signalClass.getCanonicalName() + "), received(" + signal.getClass().getCanonicalName()
						+ ")");
		}
	}

	private HashMap<String, ExecutionSignalsStorage<StockSignal>> stockSignals = new HashMap<>();
	private HashMap<String, ExecutionSignalsStorage<EodSignal>> eodSignals = new HashMap<>();

	public void registerStockSignalsType(String executionName, Class<? extends StockSignal> signalsClass) {
		if (signalsClass != null)
			stockSignals.put(executionName, new ExecutionSignalsStorage<StockSignal>(signalsClass));
	}

	public void addStockSignal(String executionName, Date date, StockSignal signal) throws BadSignalException {
		stockSignals.get(executionName).addSignal(date, signal);
	}

	public StockSignal getStockSignal(String executionName, Date date) {
		ExecutionSignalsStorage<StockSignal> ess = stockSignals.get(executionName);
		if (ess != null)
			return ess.getSignals().get(date);
		return null;
	}
	
	
	public void registerEodSignalsType(String executionName, Class<? extends EodSignal> signalsClass) {
		if (signalsClass != null)
			eodSignals.put(executionName, new ExecutionSignalsStorage<EodSignal>(signalsClass));
	}

	public void addEodSignal(String executionName, Date date, EodSignal signal) throws BadSignalException {
		eodSignals.get(executionName).addSignal(date, signal);
	}

	public EodSignal getEodSignal(String executionName, Date date) {
		ExecutionSignalsStorage<EodSignal> ess = eodSignals.get(executionName);
		if (ess != null)
			return ess.getSignals().get(date);
		return null;
	}
}
