package stsc.storage;

import java.util.ArrayList;
import java.util.HashMap;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.StockAlgorithm;

class StockAlgorithms {
	// execution name to stock algorithms
	private final HashMap<String, StockAlgorithm> map = new HashMap<>();
	private final ArrayList<StockAlgorithm> orderedAlgorithms = new ArrayList<>();

	StockAlgorithms() {
	}

	public HashMap<String, StockAlgorithm> getMap() {
		return map;
	}

	void add(final String executionName, final StockAlgorithm algo) {
		if (map.containsKey(executionName))
			return;
		orderedAlgorithms.add(algo);
		map.put(executionName, algo);
	}

	void simulate(final Day newDay) throws BadSignalException {
		for (StockAlgorithm algo : orderedAlgorithms) {
			algo.process(newDay);
		}
	}

	int size() {
		return orderedAlgorithms.size();
	}

}