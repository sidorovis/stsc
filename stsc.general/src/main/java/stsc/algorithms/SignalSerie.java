package stsc.algorithms;

public class SignalSerie<SignalType> {

	private final Class<? extends SignalType> signalClass;

	public SignalSerie(final Class<? extends SignalType> signalClass) {
		super();
		this.signalClass = signalClass;
	}

	final Class<? extends SignalType> getHandlerType() {
		return signalClass;
	}

}
