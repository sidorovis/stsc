package stsc.storage;

import java.util.HashMap;
import stsc.algorithms.EodAlgorithm;
import stsc.algorithms.StockAlgorithm;

public final class AlgorithmNamesStorage {

	private HashMap<String, Class<? extends StockAlgorithm>> stockNames = new HashMap<>();
	private HashMap<String, Class<? extends EodAlgorithm>> eodNames = new HashMap<>();

	public AlgorithmNamesStorage() {
	}

	public void addStockAlgorithm(Class<? extends StockAlgorithm> algorithmClass) {
		add(algorithmClass, stockNames);
	}

	public void addEodAlgorithm(Class<? extends EodAlgorithm> algorithmClass) {
		add(algorithmClass, eodNames);
	}

	private <T> void add(Class<? extends T> classType, HashMap<String, Class<? extends T>> algorithmsMap) {
		final String className = classType.getName();
		if (algorithmsMap.containsKey(className))
			algorithmsMap.put(className, classType);
	}

	public <T> Class<? extends EodAlgorithm> get(final String algorithmName) {
		return EodAlgorithm.class;
	}

}
