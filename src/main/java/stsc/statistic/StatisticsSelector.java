package stsc.statistic;

import java.util.SortedMap;
import java.util.TreeMap;

public class StatisticsSelector<T> {

	private int selectLastElements;
	private final StatisticsEvaluationFunction<T> evaluationFunction;
	private final SortedMap<T, Statistics> select;

	public StatisticsSelector(int selectLastElements, StatisticsEvaluationFunction<T> evaluationFunction) {
		this.selectLastElements = selectLastElements;
		this.evaluationFunction = evaluationFunction;
		this.select = new TreeMap<>();
	}

	public synchronized void addStatistics(final Statistics statistics) {
		final T compareValue = evaluationFunction.calculate(statistics);
		select.put(compareValue, statistics);
		if (select.size() > selectLastElements) {
			select.remove(select.get(select.lastKey()));
		}
	}

}
