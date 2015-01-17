package stsc.signals.series;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import stsc.common.BadSignalException;
import stsc.common.signals.SerieSignal;
import stsc.common.signals.SignalContainer;
import stsc.common.signals.SignalsSerie;

public final class CommonSignalsSerie<SignalType extends SerieSignal> extends SignalsSerie<SignalType> {

	private final ArrayList<SignalContainer<? extends SignalType>> signalList = new ArrayList<>();
	private final HashMap<Date, SignalContainer<? extends SignalType>> signalMap = new HashMap<>();

	public CommonSignalsSerie(final Class<? extends SignalType> signalClass) {
		super(signalClass);
	}

	@Override
	public synchronized SignalContainer<? extends SignalType> getSignal(final Date date) {
		return signalMap.get(date);
	}

	@Override
	public synchronized SignalContainer<? extends SignalType> getSignal(final int index) {
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
		SignalContainer<SignalType> newSignal = new SignalContainer<SignalType>(newIndex, date, signal);
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
