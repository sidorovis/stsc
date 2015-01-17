package stsc.general.statistic;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;

import stsc.general.statistic.cost.function.CostFunction;
import stsc.general.strategy.TradingStrategy;

public class StatisticsByCostSelector extends BorderedStrategySelector {

	private final CostFunction costFunction;
	private final SortedByRatingStrategies select;

	public StatisticsByCostSelector(int selectLastElements, CostFunction evaluationFunction) {
		super(selectLastElements);
		this.costFunction = evaluationFunction;
		this.select = new SortedByRatingStrategies();
	}

	@Override
	public synchronized Optional<TradingStrategy> addStrategy(final TradingStrategy strategy) {
		final Statistics statistics = strategy.getStatistics();
		final Double compareValue = costFunction.calculate(statistics);
		select.addStrategy(compareValue, strategy);
		if (select.size() > size()) {
			return select.deleteLast();
		}
		return Optional.empty();
	}

	@Override
	public synchronized void removeStrategy(final TradingStrategy strategy) {
		select.removeStrategy(costFunction.calculate(strategy.getStatistics()), strategy);
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

	protected SortedByRatingStrategies getSortedStrategies() {
		return select;
	}

	@Override
	public String toString() {
		return "Size: " + select.size();
	}

}
