package stsc.simulator.multistarter.grid;

import java.util.Arrays;

import stsc.statistic.SortedStatistics;
import stsc.statistic.StatisticsInnerProductFunction;
import stsc.statistic.StatisticsSelector;
import stsc.testhelper.TestHelper;
import junit.framework.TestCase;

public class StrategyGridSearcherTest extends TestCase {
	public void testStrategyGridSearcher() throws Exception {
		final SimulatorSettingsGridIterator iterator = TestHelper.getSimulatorSettingsGridIterator(
				Arrays.asList(new String[] { "open" }), "31-01-2000");
		final StatisticsSelector<Double> selector = new StatisticsSelector<>(6500, new StatisticsInnerProductFunction());
		final StrategyGridSearcher searcher = new StrategyGridSearcher(iterator, selector);
		final SortedStatistics<Double> sortedStatistics = searcher.getSelector().getSelect();
		assertEquals(6144, sortedStatistics.size());
	}
}
