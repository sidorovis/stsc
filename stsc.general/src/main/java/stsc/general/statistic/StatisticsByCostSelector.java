package stsc.general.statistic;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import stsc.general.statistic.cost.function.CostFunction;
import stsc.general.strategy.TradingStrategy;

public class StatisticsByCostSelector extends StrategySelector {

	private final CostFunction evaluationFunction;
	private final SortedStrategies select;

	public StatisticsByCostSelector(int selectLastElements, CostFunction evaluationFunction) {
		super(selectLastElements);
		this.evaluationFunction = evaluationFunction;
		this.select = new SortedStrategies();
	}

	@Override
	public synchronized boolean addStrategy(final TradingStrategy strategy) {
		final Statistics statistics = strategy.getStatistics();
		final Double compareValue = evaluationFunction.calculate(statistics);
		select.add(compareValue, strategy);
		if (select.size() > size()) {
			final TradingStrategy deletedElement = select.deleteLast();
			if (deletedElement == strategy) {
				return false;
			}
		}
		return true;
	}

	@Override
	public synchronized List<TradingStrategy> getStrategies() {
		final List<TradingStrategy> result = new LinkedList<>();
		for (Entry<Double, List<TradingStrategy>> i : select.getValues().entrySet()) {
			for (TradingStrategy strategy : i.getValue()) {
				result.add(strategy);
			}
		}
		return Collections.unmodifiableList(result);
	}
}
