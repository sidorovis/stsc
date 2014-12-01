package stsc.storage;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import stsc.common.algorithms.BadAlgorithmException;
import stsc.common.algorithms.EodAlgorithm;
import stsc.common.algorithms.StockAlgorithm;

public final class AlgorithmsStorage {

	private String containerPackageName = "stsc.algorithm";

	private Set<String> stockLabels = new HashSet<>();
	private Set<String> eodLabels = new HashSet<>();

	private HashMap<String, Class<? extends StockAlgorithm>> stockNames = new HashMap<>();
	private HashMap<String, Class<? extends EodAlgorithm>> eodNames = new HashMap<>();

	private static AlgorithmsStorage instance = null;

	public static AlgorithmsStorage getInstance() throws BadAlgorithmException {
		if (instance == null)
			instance = new AlgorithmsStorage();
		return instance;
	}

	public static AlgorithmsStorage getInstance(final String containerPackageName) throws BadAlgorithmException {
		if (instance == null)
			instance = new AlgorithmsStorage(containerPackageName);
		return instance;
	}

	private AlgorithmsStorage() throws BadAlgorithmException {
		loadAlgorithms();
	}

	private AlgorithmsStorage(final String containerPackageName) throws BadAlgorithmException {
		this.containerPackageName = containerPackageName;
		loadAlgorithms();
	}

	private void loadAlgorithms() throws BadAlgorithmException {
		try {
			for (ClassInfo e : ClassPath.from(ClassLoader.getSystemClassLoader()).getTopLevelClasses()) {
				final String eName = e.getName().toLowerCase();
				if (eName.contains("$") || eName.contains("test"))
					continue;
				else if (eName.startsWith(containerPackageName)) {
					final Class<?> classType = Class.forName(e.getName());
					if (classType.getSuperclass() == StockAlgorithm.class) {
						final Class<? extends StockAlgorithm> stockAlgorithm = classType.asSubclass(StockAlgorithm.class);
						stockLabels.add(e.getName());
						addStockAlgorithm(stockAlgorithm);
					} else if (classType.getSuperclass() == EodAlgorithm.class) {
						final Class<? extends EodAlgorithm> eodAlgorithm = classType.asSubclass(EodAlgorithm.class);
						eodLabels.add(e.getName());
						addEodAlgorithm(eodAlgorithm);
					}
				}
			}
		} catch (ClassNotFoundException e) {
			throw new BadAlgorithmException(e.getMessage());
		} catch (IOException e) {
			throw new BadAlgorithmException(e.getMessage());
		}
	}

	private void addStockAlgorithm(Class<? extends StockAlgorithm> algorithmClass) {
		add(algorithmClass, stockNames);
	}

	private void addEodAlgorithm(Class<? extends EodAlgorithm> algorithmClass) {
		add(algorithmClass, eodNames);
	}

	private final <T> void add(Class<? extends T> classType, HashMap<String, Class<? extends T>> algorithmsMap) {
		final String className = classType.getName().toLowerCase();
		if (!algorithmsMap.containsKey(className))
			algorithmsMap.put(className, classType);
	}

	public Class<? extends EodAlgorithm> getEod(final String algorithmName) throws BadAlgorithmException {
		return getAlgorithmClass(algorithmName, eodNames);
	}

	public Class<? extends StockAlgorithm> getStock(final String algorithmName) throws BadAlgorithmException {
		return getAlgorithmClass(algorithmName, stockNames);
	}

	private final <T> Class<? extends T> getAlgorithmClass(final String algorithmName, HashMap<String, Class<? extends T>> algorithmsMap)
			throws BadAlgorithmException {
		final String lowCase = algorithmName.toLowerCase();
		Class<? extends T> result = null;
		for (Map.Entry<String, Class<? extends T>> i : algorithmsMap.entrySet()) {
			if (i.getKey().contains(lowCase)) {
				if (result == null) {
					result = i.getValue();
				} else {
					throw new BadAlgorithmException("For '" + algorithmName + "' we could assume:" + result.getName() + " or "
							+ i.getValue().getName());
				}
			}
		}
		return result;
	}

	public final Set<String> getStockLabels() {
		return Collections.unmodifiableSet(stockLabels);
	}

	public final Set<String> getEodLabels() {
		return Collections.unmodifiableSet(eodLabels);
	}

}
