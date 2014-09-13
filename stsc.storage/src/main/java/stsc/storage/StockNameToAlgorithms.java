package stsc.storage;

import java.util.HashMap;

import stsc.common.BadSignalException;
import stsc.common.Day;
import stsc.common.algorithms.StockAlgorithm;

class StockNameToAlgorithms {
	// stock name to execution map
	private HashMap<String, StockAlgorithms> stockToAlgorithm = new HashMap<>();

	StockNameToAlgorithms() {
	}

	public HashMap<String, StockAlgorithms> getStockToAlgorithm() {
		return stockToAlgorithm;
	}

	void addExecutionOnStock(String stockName, String executionName, StockAlgorithm algo) {
		StockAlgorithms se = stockToAlgorithm.get(stockName);
		if (se == null) {
			se = new StockAlgorithms();
			stockToAlgorithm.put(stockName, se);
		}
		se.add(executionName, algo);
	}

	void simulate(String stockName, final Day newDay) throws BadSignalException {
		final StockAlgorithms e = stockToAlgorithm.get(stockName);
		if (e != null)
			e.simulate(newDay);
	}

	int size() {
		return stockToAlgorithm.size();
	}

}