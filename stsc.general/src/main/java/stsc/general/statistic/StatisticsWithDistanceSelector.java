package stsc.general.statistic;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import stsc.common.Settings;
import stsc.common.collections.SortedByRating;
import stsc.general.statistic.cost.function.CostFunction;
import stsc.general.strategy.TradingStrategy;

public class StatisticsWithDistanceSelector implements StrategySelector {

	private final class ClusterKey {
		private final TradingStrategy headStrategy;

		public ClusterKey(TradingStrategy headStrategy) {
			this.headStrategy = headStrategy;
		}

		public TradingStrategy getStrategy() {
			return headStrategy;
		}

		@Override
		public String toString() {
			return String.valueOf(headStrategy.getAvGain());
		}

		@Override
		public boolean equals(Object other) {
			if (!ClusterKey.class.isInstance(other))
				return false;
			final Statistics ls = this.getStrategy().getStatistics();
			final Statistics rs = ((ClusterKey) other).getStrategy().getStatistics();
			Double resDiff = 0.0;
			for (Entry<String, Double> e : distanceParameters.entrySet()) {
				final Double lv = Statistics.invokeMethod(ls, e.getKey()) * e.getValue();
				final Double rv = Statistics.invokeMethod(rs, e.getKey()) * e.getValue();
				resDiff += Math.abs(lv - rv);
			}
			return resDiff < Settings.doubleEpsilon;
		}
	}

	private final class ClusterKeyComparator implements Comparator<ClusterKey> {

		@Override
		public int compare(ClusterKey left, ClusterKey right) {
			final Statistics ls = left.getStrategy().getStatistics();
			final Statistics rs = right.getStrategy().getStatistics();
			Double resDiff = 0.0;
			for (Entry<String, Double> e : distanceParameters.entrySet()) {
				final Double lv = Statistics.invokeMethod(ls, e.getKey()) * e.getValue();
				final Double rv = Statistics.invokeMethod(rs, e.getKey()) * e.getValue();
				resDiff += Math.abs(lv - rv);
			}
			if (resDiff <= 1.0) {
				return 0;
			}
			return (int) (rs.getAvGain() - ls.getAvGain());
		}
	}

	final int clustersAmount;
	final int elementsInCluster;
	final CostFunction costFunction;
	final private Map<String, Double> distanceParameters = new HashMap<>();

	final private Map<ClusterKey, StatisticsByCostSelector> clusters;
	final private SortedByRating<ClusterKey> clustersByRating = new SortedByRating<ClusterKey>();

	public StatisticsWithDistanceSelector(int clustersAmount, int elementsInCluster, CostFunction costFunction) {
		this.clustersAmount = clustersAmount;
		this.elementsInCluster = elementsInCluster;
		this.costFunction = costFunction;
		this.clusters = new TreeMap<ClusterKey, StatisticsByCostSelector>(new ClusterKeyComparator());
	}

	@Override
	public int size() {
		return clustersAmount * elementsInCluster;
	}

	public void addDistanceParameter(String key, Double value) {
		distanceParameters.put(key, value);
	}

	private Double rating(final TradingStrategy strategy) {
		return costFunction.calculate(strategy.getStatistics());
	}

	@Override
	public synchronized TradingStrategy addStrategy(final TradingStrategy strategy) {
		final ClusterKey clusterKey = new ClusterKey(strategy);
		final StatisticsByCostSelector sc = clusters.get(clusterKey);
		if (sc == null) {
			final StatisticsByCostSelector selector = new StatisticsByCostSelector(elementsInCluster, costFunction);
			final TradingStrategy ts = selector.addStrategy(strategy);
			if (strategy != ts) {
				clustersByRating.addElement(rating(strategy), clusterKey);
				clusters.put(clusterKey, selector);
				checkAndRemoveCluster();
				return ts;
			} else
				return strategy;
		} else {
			return addStrategyToCluster(sc, clusterKey, strategy);
		}
	}

	@Override
	public void removeStrategy(TradingStrategy strategy) {
		final ClusterKey clusterKey = new ClusterKey(strategy);
		final StatisticsByCostSelector sc = clusters.get(clusterKey);
		if (sc != null) {
			sc.removeStrategy(strategy);
		}
	}

	private void checkAndRemoveCluster() {
		if (clusters.size() > clustersAmount) {
			final ClusterKey deletedKey = clustersByRating.deleteLast();
			StatisticsByCostSelector cluster = clusters.remove(deletedKey);
			if (cluster != null) {
				for (TradingStrategy ts : cluster.getStrategies()) {
					clustersByRating.removeElement(rating(ts), new ClusterKey(ts));
				}
			}
		}
	}

	private TradingStrategy addStrategyToCluster(StatisticsByCostSelector sc, ClusterKey clusterKey, TradingStrategy strategy) {
		final TradingStrategy ts = sc.addStrategy(strategy);
		if (strategy != ts) {
			clustersByRating.addElement(rating(strategy), clusterKey);
			findClusterAndDelete(ts);
		}
		return ts;
	}

	private void findClusterAndDelete(TradingStrategy ts) {
		if (ts == null)
			return;
		clustersByRating.removeElement(rating(ts), new ClusterKey(ts));
		final StatisticsByCostSelector scToDelete = clusters.get(new ClusterKey(ts));
		if (scToDelete != null) {
			scToDelete.removeStrategy(ts);
		}
	}

	@Override
	public synchronized List<TradingStrategy> getStrategies() {
		final List<TradingStrategy> result = new ArrayList<>();
		for (Entry<ClusterKey, StatisticsByCostSelector> clusterValue : clusters.entrySet()) {
			result.addAll(clusterValue.getValue().getStrategies());
		}
		return result;
	}

}
