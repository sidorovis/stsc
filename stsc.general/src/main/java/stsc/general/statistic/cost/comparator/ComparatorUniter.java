package stsc.general.statistic.cost.comparator;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import stsc.general.statistic.Statistics;

public class ComparatorUniter implements StatisticsComparator {

	final private Map<StatisticsComparator, Double> parameters = new HashMap<StatisticsComparator, Double>();

	public ComparatorUniter() {
	}

	public ComparatorUniter addComparator(StatisticsComparator sc, Double d) {
		parameters.put(sc, d);
		return this;
	}

	public int compare(Statistics o1, Statistics o2) {
		Double sumResult = 0.0;
		for (Entry<StatisticsComparator, Double> v : parameters.entrySet()) {
			final Double value = Double.valueOf(v.getKey().compare(o1, o2));
			sumResult += value * v.getValue();
		}
		return sumResult.intValue();
	}
}
