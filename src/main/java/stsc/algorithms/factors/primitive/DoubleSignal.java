package stsc.algorithms.factors.primitive;

import stsc.algorithms.StockSignal;

public class DoubleSignal extends StockSignal{

	final public Double value;

	public DoubleSignal(final double value) {
		this.value = new Double(value);
	}

}
