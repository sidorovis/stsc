package stsc.algorithms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import stsc.common.BadSignalException;
import stsc.common.Signal;
import stsc.common.SignalsSerie;

public final class CommonSignalsSerie<SignalType> extends SignalsSerie<SignalType> {

	private final ArrayList<Signal<? extends SignalType>> signalList = new ArrayList<>();
	private final HashMap<Date, Signal<? extends SignalType>> signalMap = new HashMap<>();

	public CommonSignalsSerie(final Class<? extends SignalType> signalClass) {
		super(signalClass);
	}

	@Override
	public synchronized Signal<? extends SignalType> getSignal(final Date date) {
		return signalMap.get(date);
	}

	@Override
	public synchronized Signal<? extends SignalType> getSignal(final int index) {
		return signalList.get(index);
	}

	@Override
	public void addSignal(Date date, SignalType signal) throws BadSignalException {
		if (signal.getClass() == getSignalClass())
			checkedAddSignal(date, signal);
		else
			throw new BadSignalException("bad signal type, expected(" + getSignalClass().getCanonicalName() + "), received("
					+ signal.getClass().getCanonicalName() + ")");
	}

	private synchronized void checkedAddSignal(Date date, SignalType signal) {
		final int newIndex = signalList.size();
		Signal<SignalType> newSignal = new Signal<SignalType>(newIndex, date, signal);
		signalList.add(newSignal);
		signalMap.put(date, newSignal);
	}

	@Override
	public synchronized int size() {
		return signalList.size();
	}

	@Override
	public String toString() {
		return signalMap.toString();
	}

}
