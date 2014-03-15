package stsc.signals;

public class BooleanSignal extends StockSignal {

	final public Boolean value;

	public BooleanSignal(final boolean value) {
		this.value = new Boolean(value);
	}

	@Override
	public String toString() {
		return value.toString();
	}

}
