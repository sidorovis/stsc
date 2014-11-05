package stsc.general.statistic;

import org.junit.Test;

import stsc.general.statistic.cost.function.CostWeightedSumFunction;

public class StatisticsWithDistanceSelectorTest {

	@Test
	public void testStatisticsWithDistanceSelector() {
		final StatisticsWithDistanceSelector statistics = new StatisticsWithDistanceSelector(3, new CostWeightedSumFunction());

	}

}
