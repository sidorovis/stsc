package stsc.general.statistic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class SortedStatistics<T> {
	private final SortedMap<T, List<Statistics>> values;

	SortedStatistics() {
		this.values = new TreeMap<T, List<Statistics>>();
	}

	void add(T key, Statistics value) {
		if (values.containsKey(key)) {
			values.get(key).add(value);
		} else {
			final List<Statistics> newValue = new ArrayList<>();
			newValue.add(value);
			values.put(key, newValue);
		}
	}

	void deleteLast() {
		if (values.isEmpty()) {
			return;
		}
		final List<Statistics> statistics = values.get(values.firstKey());
		statistics.remove(statistics.size() - 1);
		if (statistics.isEmpty()) {
			values.remove(values.firstKey());
		}
	}

	public int size() {
		int sum = 0;
		for (Map.Entry<T, List<Statistics>> i : values.entrySet()) {
			sum += i.getValue().size();
		}
		return sum;
	}

	public SortedMap<T, List<Statistics>> getValues() {
		return values;
	}

}
