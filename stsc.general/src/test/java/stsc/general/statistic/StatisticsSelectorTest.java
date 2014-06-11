package stsc.general.statistic;

import java.util.ArrayList;
import java.util.List;
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.cost.function.WeightedSumCostFunction;
import stsc.general.testhelper.TestHelper;
import junit.framework.TestCase;

public class StatisticsSelectorTest extends TestCase {
	public void testStatisticsSelector() {
		final WeightedSumCostFunction compareMethod = new WeightedSumCostFunction();
		final StatisticsSelector statisticsSelector = new StatisticsByCostSelector(2, compareMethod);

		final List<Double> values = new ArrayList<>();
		values.add(compareMethod.calculate(TestHelper.getStatistics(100, 200)));
		values.add(compareMethod.calculate(TestHelper.getStatistics(200, 250)));
		values.add(compareMethod.calculate(TestHelper.getStatistics(150, 210)));

		statisticsSelector.addStatistics(TestHelper.getStatistics(100, 200));
		statisticsSelector.addStatistics(TestHelper.getStatistics(200, 250));
		statisticsSelector.addStatistics(TestHelper.getStatistics(150, 210));

		final List<Statistics> statistics = statisticsSelector.getStatistics();
		assertEquals(2, statistics.size());
		assertEquals(compareMethod.calculate((Statistics) statistics.toArray()[0]), values.get(2));
		assertEquals(compareMethod.calculate((Statistics) statistics.toArray()[1]), values.get(0));
	}
}
