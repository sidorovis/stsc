package stsc.general.statistic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import stsc.general.strategy.Strategy;

class SortedStrategies {
	private final Map<String, Strategy> storageByHashCode;
	private final SortedMap<Double, List<Strategy>> storageByRating;

	SortedStrategies() {
		this.storageByHashCode = new HashMap<String, Strategy>();
		this.storageByRating = new TreeMap<Double, List<Strategy>>();
	}

	boolean add(Double rating, Strategy value) {
		final String hashCode = value.getSettingsHashCode();
		if (storageByHashCode.containsKey(hashCode)) {
			return false;
		}
		// FINISH STORAGE
		if (storageByRating.containsKey(rating)) {
			final List<Strategy> ratingSet = storageByRating.get(rating);
			ratingSet.add(value);
			storageByHashCode.put(hashCode, value);
		} else {
			final List<Strategy> newValue = new ArrayList<>();
			newValue.add(value);
			storageByRating.put(rating, newValue);
		}
		return true;
	}

	Strategy deleteLast() {
		if (storageByRating.isEmpty()) {
			return null;
		}
		final List<Strategy> strategies = storageByRating.get(storageByRating.firstKey());
		final Strategy result = strategies.remove(strategies.size() - 1);
		storageByHashCode.remove(result.getSettingsHashCode());
		if (strategies.isEmpty()) {
			storageByRating.remove(storageByRating.firstKey());
		}
		return result;
	}

	public int size() {
		int sum = 0;
		for (Map.Entry<Double, List<Strategy>> i : storageByRating.entrySet()) {
			sum += i.getValue().size();
		}
		return sum;
	}

	public SortedMap<Double, List<Strategy>> getValues() {
		return storageByRating;
	}

}
