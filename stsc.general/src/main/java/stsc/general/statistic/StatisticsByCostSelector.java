package stsc.general.statistic;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import stsc.general.statistic.cost.function.CostFunction;

public class StatisticsByCostSelector implements StatisticsSelector {

	private int selectLastElements;
	private final CostFunction evaluationFunction;
	private final SortedStatistics select;

	public StatisticsByCostSelector(int selectLastElements, CostFunction evaluationFunction) {
		this.selectLastElements = selectLastElements;
		this.evaluationFunction = evaluationFunction;
		this.select = new SortedStatistics();
	}

	@Override
	public synchronized void addStatistics(final Statistics statistics) {
		final Double compareValue = evaluationFunction.calculate(statistics);
		select.add(compareValue, statistics);
		if (select.size() > selectLastElements) {
			select.deleteLast();
		}
	}

	@Override
	public List<Statistics> getStatistics() {
		final List<Statistics> result = new LinkedList<>();
		for (Entry<Double, List<Statistics>> i : select.getValues().entrySet()) {
			for (Statistics statistics : i.getValue()) {
				result.add(statistics);
			}
		}
		return Collections.unmodifiableList(result);
	}
}
