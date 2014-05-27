package stsc.storage;

import java.util.Date;

import stsc.signals.BadSignalException;
import stsc.signals.Signal;

public abstract class SignalsSerie<SignalType> {

	private final Class<? extends SignalType> signalClass;

	public SignalsSerie(final Class<? extends SignalType> signalClass) {
		super();
		this.signalClass = signalClass;
	}

	protected Class<? extends SignalType> getSignalClass() {
		return signalClass;
	}

	public abstract Signal<? extends SignalType> getSignal(Date date);

	public abstract Signal<? extends SignalType> getSignal(int index);

	public abstract void addSignal(Date date, SignalType signal) throws BadSignalException;

	public abstract int size();

	public abstract String toString();
}