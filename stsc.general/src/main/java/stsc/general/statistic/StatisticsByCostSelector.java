package stsc.general.statistic;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import stsc.general.statistic.cost.function.CostFunction;

public class StatisticsByCostSelector extends StatisticsSelector {

	private final CostFunction evaluationFunction;
	private final SortedStatistics select;

	public StatisticsByCostSelector(int selectLastElements, CostFunction evaluationFunction) {
		super(selectLastElements);
		this.evaluationFunction = evaluationFunction;
		this.select = new SortedStatistics();
	}

	@Override
	public synchronized boolean addStatistics(final Statistics statistics) {
		final Double compareValue = evaluationFunction.calculate(statistics);
		select.add(compareValue, statistics);
		if (select.size() > size()) {
			final Statistics deletedElement = select.deleteLast();
			if (deletedElement == statistics) {
				return false;
			}
		}
		return true;
	}

	@Override
	public synchronized List<Statistics> getStatistics() {
		final List<Statistics> result = new LinkedList<>();
		for (Entry<Double, List<Statistics>> i : select.getValues().entrySet()) {
			for (Statistics statistics : i.getValue()) {
				result.add(statistics);
			}
		}
		return Collections.unmodifiableList(result);
	}
}
