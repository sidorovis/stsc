package stsc.general.statistic.cost.function;

import java.util.ArrayList;
import java.util.List;

import stsc.general.statistic.Statistics;

public class CostLexicographicalFunction implements CostFunction {

	final List<String> order = new ArrayList<>();
	private final double multiplikator;

	public CostLexicographicalFunction() {
		this(10.0);
	}

	public CostLexicographicalFunction(double multiplikator) {
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
