package stsc.general.statistic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stsc.general.statistic.cost.function.CostFunction;
import stsc.general.strategy.TradingStrategy;

public class StatisticsWithDistanceSelector extends StatisticsByCostSelector {

	final private Map<String, Double> distanceParameters = new HashMap<>();

	public StatisticsWithDistanceSelector(int selectLastElements, CostFunction evaluationFunction) {
		super(selectLastElements, evaluationFunction);
	}

	public void addDistanceParameter(String key, Double value) {
		distanceParameters.put(key, value);
	}

	@Override
	public synchronized boolean addStrategy(final TradingStrategy strategy) {
		final SimpleSortedStrategies strategies = getSortedStrategies();
		// strategies
		// TODO
		return false;
	}

	@Override
	public synchronized List<TradingStrategy> getStrategies() {
		// TODO
		return null;
	}
}
