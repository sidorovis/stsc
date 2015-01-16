package stsc.signals.series;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import stsc.common.BadSignalException;
import stsc.common.signals.SerieSignal;
import stsc.common.signals.SignalContainer;
import stsc.common.signals.SignalsSerie;

public final class LimitSignalsSerie<SignalType extends SerieSignal> extends SignalsSerie<SignalType> {

	private final static int DEFAULT_LIMIT = 1;
	private final int limit;
	private int index;

	private final Queue<SignalContainer<? extends SignalType>> signalList = new LinkedList<>();
	private final HashMap<Date, SignalContainer<? extends SignalType>> signalMap = new HashMap<>();

	public LimitSignalsSerie(final Class<? extends SignalType> signalClass, final int limit) {
		super(signalClass);
		this.limit = limit;
		this.index = 0;
	}

	public LimitSignalsSerie(final Class<? extends SignalType> signalClass) {
		this(signalClass, DEFAULT_LIMIT);
	}

	final int getLimit() {
		return limit;
	}

	@Override
	public SignalContainer<? extends SignalType> getSignal(Date date) {
		return signalMap.get(date);
	}

	@Override
	public SignalContainer<? extends SignalType> getSignal(int index) {
		for (SignalContainer<? extends SignalType> i : signalList) {
			if (i.getIndex() == index)
				return i;
		}
		return null;
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
		final int newIndex = index++;
		SignalContainer<SignalType> newSignal = new SignalContainer<SignalType>(newIndex, date, signal);

		if (signalList.size() > limit) {
			Date signalDate = signalList.poll().getDate();
			signalMap.remove(signalDate);
		}
		signalList.add(newSignal);
		signalMap.put(date, newSignal);
	}

	@Override
	public int size() {
		return index;
	}

	@Override
	public String toString() {
		return signalMap.toString();
	}
}
