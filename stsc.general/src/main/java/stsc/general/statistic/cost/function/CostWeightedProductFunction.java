package stsc.general.statistic.cost.function;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import stsc.general.statistic.PublicMethod;
import stsc.general.statistic.Statistics;

public class CostWeightedProductFunction implements CostFunction {

	private final Map<String, Double> parameters = new HashMap<>();

	public CostWeightedProductFunction() {
		parameters.put("getAvGain", 1.0);
	}

	public void addParameter(String name, Double value) {
		parameters.put(name, value);
	}

	@Override
	public Double calculate(Statistics statistics) {
		Double sum = 0.0;
		for (Double d : parameters.values()) {
			sum += d;
		}
		Double result = 0.0;
		final Method[] methods = statistics.getClass().getMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(PublicMethod.class)) {
				final String methodName = method.getName();
				if (parameters.containsKey(methodName)) {
					final Double power = parameters.get(methodName) / sum;
					try {
						final Double statisticValue = (Double) method.invoke(statistics, new Object[] {});
						final Double signum = Math.signum(statisticValue);
						final Double pow = Math.pow(Math.abs(statisticValue), power);
						result += signum * pow;
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					}
				}
			}
		}
		return result;
	}

}
