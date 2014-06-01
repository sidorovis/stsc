package stsc.general.statistic.cost.function;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import stsc.general.statistic.Statistics;

public class CfLexicographical implements CostFunction<Double> {

	final List<String> order = new ArrayList<>();
	private final Class<?>[] emptyInvoker = {};
	private final Object[] emptyValues = {};
	private final double multiplikator;

	public CfLexicographical() {
		this(10.0);
	}

	public CfLexicographical(double multiplikator) {
		this.multiplikator = multiplikator;

	}

	public void addNextValue(String value) {
		order.add(value);
	}

	@Override
	public Double calculate(Statistics statistics) {
		Double result = 0.0;
		for (String methodName : order) {
			try {
				final Method method = statistics.getClass().getMethod(methodName, emptyInvoker);
				Double value = (Double) method.invoke(statistics, emptyValues);
				result = result * multiplikator + value;
			} catch (NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			}
		}
		return result;
	}
}
