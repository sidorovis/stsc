package stsc.general.statistic.cost.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import stsc.general.statistic.Statistics;

public class BayesianProbabilityCostFunction implements CostFunction {

	private final List<Map<String, Double>> parameters = new ArrayList<Map<String, Double>>();

	public BayesianProbabilityCostFunction() {
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
			Double max = Double.MIN_VALUE;
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
