package stsc.storage;

import java.util.Date;
import java.util.HashMap;

import stsc.algorithms.EodSignal;

public class SignalsStorage {
	class ExecutionSignalsStorage {
		private final Class<? extends EodSignal> signalClass;
		private HashMap<Date, EodSignal> signals = new HashMap<Date, EodSignal>();

		public ExecutionSignalsStorage(Class<? extends EodSignal> signalClass) {
			this.signalClass = signalClass;
		}

		public HashMap<Date, EodSignal> getSignals() {
			return signals;
		}

		public void addSignal(Date date, EodSignal signal) throws BadSignalException {
			if (signal.getClass() == signalClass)
				signals.put(date, signal);
			else
				throw new BadSignalException("bad signal type was tried to be added expected("
						+ signal.getClass().getCanonicalName() + "), received(" + signal.getClass().getCanonicalName()
						+ ")");
		}
	}

	private HashMap<String, ExecutionSignalsStorage> signals = new HashMap<>();

	public void registerSignalsFromExecution(String executionName, Class<? extends EodSignal> signalsClass) {
		if (signalsClass != null)
			signals.put(executionName, new ExecutionSignalsStorage(signalsClass));
	}

	public void addSignal(String executionName, Date date, EodSignal signal) throws BadSignalException {
		signals.get(executionName).addSignal(date, signal);
	}

	public EodSignal getSignal(String executionName, Date date) {
		ExecutionSignalsStorage ess = signals.get(executionName);
		if (ess != null)
			return signals.get(executionName).signals.get(date);
		return null;
	}
}
