package stsc.algorithms;

public final class LimitSignalSerie<SignalType> extends SignalSerie<SignalType> {

	private final int limit;

	public LimitSignalSerie(final Class<? extends SignalType> signalClass, final int limit) {
		super(signalClass);
		this.limit = limit;
	}

	final int getLimit() {
		return limit;
	}

}
