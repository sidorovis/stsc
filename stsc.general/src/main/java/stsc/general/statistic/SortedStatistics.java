package stsc.general.statistic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class SortedStatistics {
	private final SortedMap<Double, List<Statistics>> values;

	SortedStatistics() {
		this.values = new TreeMap<Double, List<Statistics>>();
	}

	void add(Double key, Statistics value) {
		if (values.containsKey(key)) {
			values.get(key).add(value);
		} else {
			final List<Statistics> newValue = new ArrayList<>();
			newValue.add(value);
			values.put(key, newValue);
		}
	}

	Statistics deleteLast() {
		if (values.isEmpty()) {
			return null;
		}
		final List<Statistics> statistics = values.get(values.firstKey());
		Statistics result = statistics.remove(statistics.size() - 1);
		if (statistics.isEmpty()) {
			values.remove(values.firstKey());
		}
		return result;
	}

	public int size() {
		int sum = 0;
		for (Map.Entry<Double, List<Statistics>> i : values.entrySet()) {
			sum += i.getValue().size();
		}
		return sum;
	}

	public SortedMap<Double, List<Statistics>> getValues() {
		return values;
	}

}
