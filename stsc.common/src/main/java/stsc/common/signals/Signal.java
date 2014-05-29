package stsc.common.signals;

import java.util.Date;

public final class Signal<SignalType> {
	final int index;
	final Date date;
	final SignalType signal;

	public Signal(final int index, final Date date, final SignalType signal) {
		this.index = index;
		this.date = date;
		this.signal = signal;
	}

	@SuppressWarnings("unchecked")
	public <T> T getSignal(Class<T> expectedClass) {
		if (expectedClass.isInstance(signal)) {
			return (T) signal;
		} else
			return null;
	}

	public SignalType getValue() {
		return signal;
	}

	@Override
	public String toString() {
		return signal.toString();
	}

	public int getIndex() {
		return index;
	}

	public Date getDate() {
		return date;
	}

}
