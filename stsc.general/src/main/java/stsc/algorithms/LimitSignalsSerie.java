package stsc.algorithms;

public final class LimitSignalsSerie<SignalType> extends SignalsSerie<SignalType> {

	private final static int DEFAULT_LIMIT = 1;
	private final int limit;

	public LimitSignalsSerie(final Class<? extends SignalType> signalClass, final int limit) {
		super(signalClass);
		this.limit = limit;
	}

	public LimitSignalsSerie(final Class<? extends SignalType> signalClass) {
		this(signalClass, DEFAULT_LIMIT);
	}

	final int getLimit() {
		return limit;
	}

}
