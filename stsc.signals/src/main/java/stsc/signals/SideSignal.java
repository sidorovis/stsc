package stsc.signals;

import stsc.common.Side;
import stsc.common.signals.StockSignal;

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
