package stsc.algorithms;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import stsc.storage.SignalsStorage;
import stsc.trading.Broker;

public class EodAlgorithmExecution {
	private final String executionName;
	private final String algorithmName;
	private final Class<? extends EodAlgorithm> algorithmType;

	public EodAlgorithmExecution(String executionName, String algorithmName) throws BadAlgorithmException {
		this.executionName = executionName;
		this.algorithmName = algorithmName;
		try {
			Class<?> classType = Class.forName(algorithmName);
			this.algorithmType = classType.asSubclass(EodAlgorithm.class);
		} catch (ClassNotFoundException e) {
			throw new BadAlgorithmException("Algorithm class '" + algorithmName + "' was not found: " + e.toString());
		}
	}

	public EodAlgorithmExecution(String executionName, Class<? extends EodAlgorithm> algorithmType) {
		this.executionName = executionName;
		this.algorithmName = algorithmType.getName();
		this.algorithmType = algorithmType;
	}

	public String getName() {
		return executionName;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public EodAlgorithm getInstance(final Broker broker, final SignalsStorage signalsStorage,
			final AlgorithmSettings settings) throws BadAlgorithmException {
		try {
			final Class<?>[] constructorParameters = { String.class, Broker.class, SignalsStorage.class,
					AlgorithmSettings.class };
			final Constructor<? extends EodAlgorithm> constructor = algorithmType.getConstructor(constructorParameters);
			final Object[] params = { executionName, broker, signalsStorage, settings };

			final EodAlgorithm algo = constructor.newInstance(params);
			return algo;
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
