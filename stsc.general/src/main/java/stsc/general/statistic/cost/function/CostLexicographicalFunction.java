package stsc.general.statistic.cost.function;

import java.util.ArrayList;
import java.util.List;

import stsc.general.statistic.Statistics;

//@formatter:off
/**
 * CostLexicographicalFunction is a cost function that calculated by next rules:
 * there is a multiplikator and ordered set of statistics parameters 
 * M for multiplikator;
 * P[x] - for N parameters;
 * CF = (((P[1] * M) + P[2]) * M + P[3]) * M + ... P[N];
 */
// @formatter:on

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
