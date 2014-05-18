package stsc.storage;

import java.util.HashMap;

import stsc.algorithms.StockAlgorithm;
import stsc.common.Day;
import stsc.signals.BadSignalException;

class StockNameToAlgorithms {
	// stock name to execution map
	private HashMap<String, StockAlgorithms> stockToAlgorithm = new HashMap<>();

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
		StockAlgorithms e = stockToAlgorithm.get(stockName);
		if (e != null)
			e.simulate(newDay);
	}

	int size() {
		return stockToAlgorithm.size();
	}

}