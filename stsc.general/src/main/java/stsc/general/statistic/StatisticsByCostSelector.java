package stsc.general.statistic;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import stsc.general.statistic.cost.function.CostFunction;
import stsc.general.strategy.Strategy;

public class StatisticsByCostSelector extends StrategySelector {

	private final CostFunction evaluationFunction;
	private final SortedStrategies select;

	public StatisticsByCostSelector(int selectLastElements, CostFunction evaluationFunction) {
		super(selectLastElements);
		this.evaluationFunction = evaluationFunction;
		this.select = new SortedStrategies();
	}

	@Override
	public synchronized boolean addStrategy(final Strategy strategy) {
		final Statistics statistics = strategy.getStatistics();
		final Double compareValue = evaluationFunction.calculate(statistics);
		select.add(compareValue, strategy);
		if (select.size() > size()) {
			final Strategy deletedElement = select.deleteLast();
			if (deletedElement == strategy) {
				return false;
			}
		}
		return true;
	}

	@Override
	public synchronized List<Strategy> getStrategies() {
		final List<Strategy> result = new LinkedList<>();
		for (Entry<Double, List<Strategy>> i : select.getValues().entrySet()) {
			for (Strategy strategy : i.getValue()) {
				result.add(strategy);
			}
		}
		return Collections.unmodifiableList(result);
	}
}
