package stsc.signals;

import java.util.ArrayList;
import java.util.List;

import stsc.common.signals.StockSignal;

public class ListOfDoubleSignal extends StockSignal {

	final private List<Double> values = new ArrayList<>();

	public ListOfDoubleSignal() {
	}

	public ListOfDoubleSignal add(final double element) {
		values.add(element);
		return this;
	}

	public ListOfDoubleSignal addDouble(final double element) {
		values.add(element);
		return this;
	}

	public List<Double> getValues() {
		return values;
	}

	@Override
	public String toString() {
		return values.toString();
	}

}
