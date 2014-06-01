package stsc.general.statistic;

import stsc.general.statistic.cost.function.CostFunction;

public class StatisticsSelector<T> {

	private int selectLastElements;
	private final CostFunction<T> evaluationFunction;
	private final SortedStatistics<T> select;

	public StatisticsSelector(int selectLastElements, CostFunction<T> evaluationFunction) {
		this.selectLastElements = selectLastElements;
		this.evaluationFunction = evaluationFunction;
		this.select = new SortedStatistics<T>();
	}

	public synchronized void addStatistics(final Statistics statistics) {
		final T compareValue = evaluationFunction.calculate(statistics);
		select.add(compareValue, statistics);
		if (select.size() > selectLastElements) {
			select.deleteLast();
		}
	}

	public synchronized SortedStatistics<T> getSortedStatistics() {
		return select;
	}

}
