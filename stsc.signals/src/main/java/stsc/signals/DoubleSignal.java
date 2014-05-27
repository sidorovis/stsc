package stsc.signals;


public class DoubleSignal extends StockSignal {

	final public Double value;

	public DoubleSignal(final double value) {
		this.value = Double.valueOf(value);
	}

	@Override
	public String toString() {
		return value.toString();
	}

}
