package stsc.signals;

import stsc.common.signals.SerieSignal;


public class IntegerSignal extends SerieSignal {

	final public Integer value;

	public IntegerSignal(final int value) {
		this.value = Integer.valueOf(value);
	}

	@Override
	public String toString() {
		return value.toString();
	}

}
