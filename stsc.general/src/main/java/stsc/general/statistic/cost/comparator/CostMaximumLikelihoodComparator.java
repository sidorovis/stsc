package stsc.general.statistic.cost.comparator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import stsc.general.statistic.Statistics;

public class CostMaximumLikelihoodComparator implements CostStatisticsComparator {

	private final Map<String, Double> parameters = new HashMap<>();

	public CostMaximumLikelihoodComparator() {
		parameters.put("getAvGain", 100.0);
	}

	public CostMaximumLikelihoodComparator addParameter(String name, Double value) {
		parameters.put(name, value);
		return this;
	}

	@Override
	public int compare(Statistics s1, Statistics s2) {
		Double result1 = 0.0;
		Double result2 = 0.0;
		for (Entry<String, Double> i : parameters.entrySet()) {
			final Double w = i.getValue();
			Double v1 = Math.abs(w - Statistics.invokeMethod(s1, i.getKey()));
			Double v2 = Math.abs(w - Statistics.invokeMethod(s2, i.getKey()));
			if (Double.compare(v1, 0.0) != 0)
				result1 += Math.log(v1);
			if (Double.compare(v2, 0.0) != 0)
				result2 += Math.log(v2);
		}
		return Double.compare(result1, result2);
	}
}
