package stsc.storage;

import java.util.Date;
import java.util.HashMap;

public class SignalsStorage {
	class ExecutionSignalsStorage {
		private final Class<? extends ExecutionSignal> signalClass;
		private HashMap<Date, ExecutionSignal> signals = new HashMap<Date, ExecutionSignal>();

		public ExecutionSignalsStorage(Class<? extends ExecutionSignal> signalClass) {
			this.signalClass = signalClass;
		}

		public HashMap<Date, ExecutionSignal> getSignals() {
			return signals;
		}

		public void addSignal(Date date, ExecutionSignal signal) throws BadSignalException {
			if (signal.getClass() == signalClass)
				signals.put(date, signal);
			else
				throw new BadSignalException("bad signal type was tried to be added expected("
						+ signal.getClass().getCanonicalName() + "), received(" + signal.getClass().getCanonicalName()
						+ ")");
		}
	}

	private HashMap<String, ExecutionSignalsStorage> signals = new HashMap<>();

	public void registerSignalsFromExecution(String executionName, Class<? extends ExecutionSignal> signalsClass) {
		if (signalsClass != null)
			signals.put(executionName, new ExecutionSignalsStorage(signalsClass));
	}

	public void addSignal(String executionName, Date date, ExecutionSignal signal) throws BadSignalException {
		signals.get(executionName).addSignal(date, signal);
	}

	public ExecutionSignal getSignal(String executionName, Date date) {
		ExecutionSignalsStorage ess = signals.get(executionName);
		if (ess != null)
			return signals.get(executionName).signals.get(date);
		return null;
	}
}
