package stsc.general.statistic.cost.function;

import java.util.ArrayList;
import java.util.List;

import stsc.general.statistic.Statistics;

public class LexicographicalCostFunction implements CostFunction {

	final List<String> order = new ArrayList<>();
	private final double multiplikator;

	public LexicographicalCostFunction() {
		this(10.0);
	}

	public LexicographicalCostFunction(double multiplikator) {
		this.multiplikator = multiplikator;

	}

	public void addNextValue(String value) {
		order.add(value);
	}

	@Override
	public Double calculate(Statistics statistics) {
		Double result = 0.0;
		for (String methodName : order) {
			result = result * multiplikator + Statistics.invokeMethod(statistics, methodName);
		}
		return result;
	}
}
