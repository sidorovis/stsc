package stsc.statistic;

public class StatisticsSelector<T> {

	private int selectLastElements;
	private final StatisticsEvaluationFunction<T> evaluationFunction;
	private final SortedStatistics<T> select;

	public StatisticsSelector(int selectLastElements, StatisticsEvaluationFunction<T> evaluationFunction) {
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

	public synchronized SortedStatistics<T> getSelect() {
		return select;
	}

}
