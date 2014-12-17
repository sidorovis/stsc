package stsc.signals.eod;

import stsc.common.signals.EodSignal;

public class EodDoubleSignal extends EodSignal {
	private final Double value;

	public EodDoubleSignal(Double value) {
		super();
		this.value = value;
	}

	public Double getValue() {
		return value;
	}

}
