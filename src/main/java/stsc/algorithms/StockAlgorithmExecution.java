package stsc.algorithms;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import stsc.storage.SignalsStorage;

public class StockAlgorithmExecution {
	private final String executionName;
	private final String algorithmName;

	public StockAlgorithmExecution(String executionName, String algorithmName) {
		this.executionName = executionName;
		this.algorithmName = algorithmName;
	}

	public StockAlgorithmExecution(String executionName, Class<? extends EodAlgorithm> algorithmType) {
		this.executionName = executionName;
		this.algorithmName = algorithmType.getName();
	}

	public String getName() {
		return executionName;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public StockAlgorithmInterface getInstance(SignalsStorage signalsStorage) throws BadAlgorithmException {
		try {
			Class<?> classType = Class.forName(algorithmName);
			Constructor<?> constructor = classType.getConstructor();

			StockAlgorithmInterface algo = (StockAlgorithmInterface) constructor.newInstance();

			algo.setExecutionName(executionName);
			algo.setSignalsStorage(signalsStorage);

			return algo;
		} catch (ClassNotFoundException e) {
			throw new BadAlgorithmException("Algorithm class '" + algorithmName + "' was not found: " + e.toString());
		} catch (NoSuchMethodException e) {
			throw new BadAlgorithmException("Bad Algorithm '" + algorithmName + "', constructor was not found: "
					+ e.toString());
		} catch (SecurityException e) {
			throw new BadAlgorithmException("Bad Algorithm '" + algorithmName + "', constructor could not be called: "
					+ e.toString());
		} catch (InstantiationException e) {
			throw new BadAlgorithmException("Bad Algorithm '" + algorithmName + "', instantiation exception: "
					+ e.toString());
		} catch (IllegalAccessException e) {
			throw new BadAlgorithmException("Bad Algorithm '" + algorithmName
					+ "', instantiation impossible due to illegal access: " + e.toString());
		} catch (IllegalArgumentException e) {
			throw new BadAlgorithmException("Bad Algorithm '" + algorithmName + "', illegal arguments: " + e.toString());
		} catch (InvocationTargetException e) {
			throw new BadAlgorithmException("Bad Algorithm '" + algorithmName + "', invocation target exception: "
					+ e.toString());
		}
	}
}
