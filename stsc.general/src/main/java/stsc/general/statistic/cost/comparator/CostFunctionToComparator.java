package stsc.general.statistic.cost.comparator;

import stsc.general.statistic.Statistics;
import stsc.general.statistic.cost.function.CostFunction;

public class CostFunctionToComparator implements StatisticsComparator {

	private final CostFunction costFunction;

	public CostFunctionToComparator(final CostFunction costFunction) {
		this.costFunction = costFunction;
	}

	public int compare(Statistics o1, Statistics o2) {
		return costFunction.calculate(o1).compareTo(costFunction.calculate(o2));
	}

}
