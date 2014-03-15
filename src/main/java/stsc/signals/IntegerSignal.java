package stsc.signals;

public class IntegerSignal extends StockSignal{

	final public Integer value;

	public IntegerSignal(final int value) {
		this.value = new Integer(value);
	}

	@Override
	public String toString() {
		return value.toString();
	}

	
}
