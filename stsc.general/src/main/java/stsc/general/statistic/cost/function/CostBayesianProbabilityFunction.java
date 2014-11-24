package stsc.general.statistic.cost.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import stsc.general.statistic.Statistics;

// @formatter:off
/**
 * Calculate Cost Function for {@link Statistics} using Bayesian Probability
 * methodology.
 * Parameters are divided by layers with different coefficients for example:
 * we could have layer with next coefficients (getPeriod -> 2.0, getAvGain -> 4.0)
 * which provide us with information that cost function for such layer will be:
 * max(Period * 2.0 and AvGain * 4.0);
 * Result of function is minimum between all layers.
 * If there is no layers result is Double.MAX_VALUE
 * layer with no fields lead to get -Double.MAX_VALUE as layer value.
 */
//@formatter:on
public class CostBayesianProbabilityFunction implements CostFunction {

	private final List<Map<String, Double>> parameters = new ArrayList<Map<String, Double>>();

	public CostBayesianProbabilityFunction() {
		super();
	}

	public Map<String, Double> addLayer() {
		final Map<String, Double> result = new HashMap<String, Double>();
		parameters.add(result);
		return result;
	}

	@Override
	public Double calculate(Statistics statistics) {
		Double min = Double.MAX_VALUE;
		for (Map<String, Double> layer : parameters) {
			Double max = -Double.MAX_VALUE;
			for (Entry<String, Double> e : layer.entrySet()) {
				final Double sValue = Statistics.invokeMethod(statistics, e.getKey());
				final Double pValue = sValue * e.getValue();
				if (max < pValue)
					max = pValue;
			}
			if (max < min)
				min = max;
		}
		return min;
	}

}
