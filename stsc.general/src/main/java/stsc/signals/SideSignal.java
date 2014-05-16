package stsc.signals;

import stsc.trading.Side;

public class SideSignal extends StockSignal {

	private final Side side;
	private final Double value;

	public SideSignal(final Side side, Double value) {
		this.side = side;
		this.value = value;
	}

	public Side getSide() {
		return side;
	}

	public Double getValue() {
		return value;
	}
}
