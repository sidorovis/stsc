package stsc.statistic;

public interface StatisticsEvaluationFunction<T> {
	public T calculate(Statistics statistics);
}
