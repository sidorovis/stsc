package stsc.common.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

public class SortedByRating<T> {

	private final SortedMap<Double, List<T>> storageByRating;

	public SortedByRating() {
		this.storageByRating = new TreeMap<Double, List<T>>();
	}

	public boolean addElement(Double rating, T value) {
		if (storageByRating.containsKey(rating)) {
			final List<T> ratingSet = storageByRating.get(rating);
			ratingSet.add(value);
		} else {
			final List<T> newValue = new ArrayList<>();
			newValue.add(value);
			storageByRating.put(rating, newValue);
		}
		return true;
	}

	public boolean removeElement(Double rating, T value) {
		if (storageByRating.containsKey(rating)) {
			final List<T> ratingSet = storageByRating.get(rating);
			final boolean deleted = ratingSet.remove(value);
			if (deleted && ratingSet.isEmpty()) {
				storageByRating.remove(rating);
			}
			return deleted;
		}
		return false;
	}

	public T deleteLast() {
		if (storageByRating.isEmpty()) {
			return null;
		}
		final List<T> strategies = storageByRating.get(storageByRating.firstKey());
		final T result = strategies.remove(strategies.size() - 1);
		if (strategies.isEmpty()) {
			storageByRating.remove(storageByRating.firstKey());
		}
		return result;
	}

	public int size() {
		int sum = 0;
		for (Map.Entry<Double, List<T>> i : storageByRating.entrySet()) {
			sum += i.getValue().size();
		}
		return sum;
	}

	public SortedMap<Double, List<T>> getValues() {
		return storageByRating;
	}

	public List<T> getValuesAsList() {
		final List<T> result = new ArrayList<>();
		for (Entry<Double, List<T>> e : getValues().entrySet()) {
			result.addAll(e.getValue());
		}
		return result;
	}

	@Override
	public String toString() {
		return storageByRating.toString();
	}

}
