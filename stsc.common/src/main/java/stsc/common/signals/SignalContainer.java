package stsc.common.signals;

import java.util.Date;
import java.util.Optional;

public final class SignalContainer<SignalType extends SerieSignal> {
	final int index;
	final Date date;
	final Optional<SignalType> signal;

	public static <T extends SerieSignal> SignalContainer<T> empty(Date date) {
		return new SignalContainer<T>(0, date);
	}

	public static <T extends SerieSignal> SignalContainer<T> empty(final int index) {
		return new SignalContainer<T>(index, new Date());
	}

	public SignalContainer(final int index, final Date date) {
		this.index = index;
		this.date = date;
		this.signal = Optional.empty();
	}

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
	
	public boolean isPresent() {
		return signal.isPresent();
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
