package stsc.general.statistic.cost.function;

import stsc.general.statistic.Statistics;

public interface CostFunction<T> {
	public T calculate(Statistics statistics);
}
