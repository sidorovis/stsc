package stsc.signals;

import java.util.ArrayList;
import java.util.List;

import stsc.common.signals.StockSignal;

public class ListOfDoubleSignal extends StockSignal {

	final public List<Double> values = new ArrayList<>();

	public ListOfDoubleSignal() {
	}

	public void addDouble(final double element) {
		values.add(element);
	}

	@Override
	public String toString() {
		return values.toString();
	}

}
