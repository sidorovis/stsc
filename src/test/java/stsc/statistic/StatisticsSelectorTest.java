package stsc.statistic;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import stsc.testhelper.TestHelper;
import junit.framework.TestCase;

public class StatisticsSelectorTest extends TestCase {
	public void testStatisticsSelector() {
		final StatisticsInnerProductFunction compareMethod = new StatisticsInnerProductFunction();
		final StatisticsSelector<Double> statisticsSelector = new StatisticsSelector<>(2, compareMethod);

		final List<Double> values = new ArrayList<>();
		values.add(compareMethod.calculate(TestHelper.getStatistics(100, 200)));
		values.add(compareMethod.calculate(TestHelper.getStatistics(200, 250)));
		values.add(compareMethod.calculate(TestHelper.getStatistics(150, 210)));

		statisticsSelector.addStatistics(TestHelper.getStatistics(100, 200));
		statisticsSelector.addStatistics(TestHelper.getStatistics(200, 250));
		statisticsSelector.addStatistics(TestHelper.getStatistics(150, 210));

		final SortedMap<Double, Statistics> select = statisticsSelector.getSelect();
		assertEquals(2, select.size());
		assertEquals(select.firstKey(), values.get(2));
		assertEquals(select.lastKey(), values.get(0));
	}
}
