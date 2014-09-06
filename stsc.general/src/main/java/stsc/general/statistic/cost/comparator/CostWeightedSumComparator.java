package stsc.general.statistic.cost.comparator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import stsc.general.statistic.Statistics;

public class CostWeightedSumComparator implements CostStatisticsComparator {

	private final Map<String, Double> parameters = new HashMap<>();

	public CostWeightedSumComparator() {
		parameters.put("getAvGain", 1.0);
	}

	public CostWeightedSumComparator addParameter(String name, Double value) {
		parameters.put(name, value);
		return this;
	}

	@Override
	public int compare(Statistics s1, Statistics s2) {
		Double sum = 0.0;
		for (Double d : parameters.values()) {
			sum += d;
		}
		Double result = 0.0;
		for (Entry<String, Double> i : parameters.entrySet()) {
			Double v1 = Statistics.invokeMethod(s1, i.getKey());
			Double v2 = Statistics.invokeMethod(s2, i.getKey());
			final Double w = i.getValue() / sum;
			result += Math.signum(v1 - v2) * Math.pow(Math.abs(v1 - v2), w);
		}
		if (result > 0.0)
			return -1;
		else if (result < 0.0)
			return 1;
		else
			return 0;
	}
}
