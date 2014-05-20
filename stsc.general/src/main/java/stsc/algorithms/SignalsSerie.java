package stsc.algorithms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import stsc.signals.BadSignalException;
import stsc.signals.Signal;

public class SignalsSerie<SignalType> {

	private final Class<? extends SignalType> signalClass;

	private final ArrayList<Signal<? extends SignalType>> signalList = new ArrayList<>();
	private final HashMap<Date, Signal<? extends SignalType>> signalMap = new HashMap<>();

	public SignalsSerie(final Class<? extends SignalType> signalClass) {
		super();
		this.signalClass = signalClass;
	}

	public synchronized Signal<? extends SignalType> getSignal(final Date date) {
		return signalMap.get(date);
	}

	public synchronized Signal<? extends SignalType> getSignal(final int index) {
		return signalList.get(index);
	}

	public void addSignal(Date date, SignalType signal) throws BadSignalException {
		if (signal.getClass() == signalClass)
			checkedAddSignal(date, signal);
		else
			throw new BadSignalException("bad signal type, expected(" + signalClass.getCanonicalName() + "), received(" + signal.getClass().getCanonicalName()
					+ ")");
	}

	private synchronized void checkedAddSignal(Date date, SignalType signal) {
		final int newIndex = signalList.size();
		Signal<SignalType> newSignal = new Signal<SignalType>(newIndex, date, signal);
		signalList.add(newSignal);
		signalMap.put(date, newSignal);
	}

	public synchronized int size() {
		return signalList.size();
	}

	@Override
	public String toString() {
		return signalMap.toString();
	}

	final Class<? extends SignalType> getSignalType() {
		return signalClass;
	}

}
