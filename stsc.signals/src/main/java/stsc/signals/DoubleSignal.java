package stsc.signals;

import stsc.common.signals.SerieSignal;

public class DoubleSignal extends SerieSignal {

	final private double value;

	public DoubleSignal(final double value) {
		this.value = Double.valueOf(value);
	}

	public double getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(value);
	}

}
