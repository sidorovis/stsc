package stsc.signals;

import stsc.common.signals.StockSignal;


public class IntegerSignal extends StockSignal {

	final public Integer value;

	public IntegerSignal(final int value) {
		this.value = Integer.valueOf(value);
	}

	@Override
	public String toString() {
		return value.toString();
	}

}
