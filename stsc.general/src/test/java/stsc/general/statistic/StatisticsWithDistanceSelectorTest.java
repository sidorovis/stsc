package stsc.general.statistic;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import stsc.general.statistic.cost.function.CostWeightedSumFunction;
import stsc.general.strategy.TradingStrategy;

public class StatisticsWithDistanceSelectorTest {

	TradingStrategy getStrategy(Double avGain) {
		final Map<String, Double> list = new HashMap<>();
		list.put("getAvGain", avGain);
		return new TradingStrategy(null, new Statistics(list));
	}

	@Test
	public void testStatisticsWithDistanceSelector() {
		final StatisticsWithDistanceSelector selector = new StatisticsWithDistanceSelector(3, 3, new CostWeightedSumFunction());
		selector.addDistanceParameter("getAvGain", 0.8);
		selector.addStrategy(getStrategy(1.0));
		selector.addStrategy(getStrategy(2.0));
		selector.addStrategy(getStrategy(3.1));
		selector.addStrategy(getStrategy(3.2));
		selector.addStrategy(getStrategy(3.3));
		selector.addStrategy(getStrategy(3.4));
		Assert.assertEquals(5, selector.getStrategies().size());

		selector.addStrategy(getStrategy(1.2));
		selector.addStrategy(getStrategy(2.1));

		Assert.assertEquals(6, selector.getStrategies().size());
		selector.addStrategy(getStrategy(8.0));
		selector.addStrategy(getStrategy(8.1));
		selector.addStrategy(getStrategy(8.2));
		selector.addStrategy(getStrategy(8.3));
		selector.addStrategy(getStrategy(9.2));
		selector.addStrategy(getStrategy(9.4));
		Assert.assertEquals(7, selector.getStrategies().size());
		selector.addStrategy(getStrategy(9.5));
		selector.addStrategy(getStrategy(9.6));
		Assert.assertEquals(9, selector.getStrategies().size());
		selector.addStrategy(getStrategy(9.45));
		Assert.assertEquals(9, selector.getStrategies().size());
	}
}
