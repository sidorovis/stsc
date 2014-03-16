package stsc.algorithms;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import stsc.storage.AlgorithmNamesStorage;
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

	public EodAlgorithm getInstance(Broker broker, SignalsStorage signals, AlgorithmSettings settings, AlgorithmNamesStorage namesStorage)
			throws BadAlgorithmException {
		try {
			EodAlgorithm.Init init = new EodAlgorithm.Init();
			init.executionName = executionName;
			init.signalsStorage = signals;
			init.broker = broker;
			init.settings = settings;
			init.namesStorage = namesStorage;

			final Class<?>[] constructorParameters = { EodAlgorithm.Init.class };
			final Constructor<? extends EodAlgorithm> constructor = algorithmType.getConstructor(constructorParameters);
			final Object[] params = { init };

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
