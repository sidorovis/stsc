package stsc.distributed.hadoop.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import stsc.general.statistic.Statistics;

public class StatisticsWritable extends MapEasyWritable {

	public StatisticsWritable(final Statistics statistics) {
		saveStatistics(statistics);
	}

	protected StatisticsWritable() {
	}

	private void saveStatistics(Statistics statistics) {
		final Set<String> methods = Statistics.getStatisticsMethods();
		integers.put("size", methods.size());
		int index = 0;
		for (String methodName : methods) {
			final Double value = Statistics.invokeMethod(statistics, methodName);
			final String parName = generateParameterName(index);
			strings.put(parName, methodName);
			doubles.put(parName, value);
			index += 1;
		}
	}

	public Statistics getStatistics() {
		final Map<String, Double> list = loadStatistics();
		return new Statistics(list);
	}

	private Map<String, Double> loadStatistics() {
		final int size = integers.get("size");
		final Map<String, Double> values = new HashMap<>();
		for (int i = 0; i < size; ++i) {
			final String parName = generateParameterName(i);
			values.put(strings.get(parName), doubles.get(parName));
		}
		return values;
	}

	private String generateParameterName(final int index) {
		return "method." + String.valueOf(index);
	}
}
