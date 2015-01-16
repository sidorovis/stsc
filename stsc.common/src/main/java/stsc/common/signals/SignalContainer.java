package stsc.common.signals;

import java.util.Date;
import java.util.Optional;

public final class SignalContainer<SignalType extends SerieSignal> {
	final int index;
	final Date date;
	final Optional<SignalType> signal;

	public SignalContainer(final int index, final Date date, final SignalType signal) {
		this.index = index;
		this.date = date;
		this.signal = Optional.of(signal);
	}

	@SuppressWarnings("unchecked")
	public <T> Optional<T> getSignal(Class<T> expectedClass) {
		if (!signal.isPresent()) {
			return Optional.empty();
		}
		if (isType(expectedClass)) {
			return (Optional<T>) signal;
		} else
			return Optional.empty();
	}

	public <T> boolean isType(Class<T> expectedClass) {
		if (!signal.isPresent()) {
			return false;
		}
		return expectedClass.isInstance(signal.get());
	}

	public Optional<SignalType> getValue() {
		return signal;
	}

	/**
	 * Not safe method, use {@link #getSignal(Class)} instead.
	 */
	public <T> T getContent(Class<T> expectedClass) {
		return getSignal(expectedClass).get();
	}

	/**
	 * Not safe method, use {@link #getValue()} instead.
	 */
	public SignalType getContent() {
		return signal.get();
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
