package stsc.simulator.multistarter.grid;

import stsc.statistic.SortedStatistics;
import stsc.statistic.StatisticsInnerProductFunction;
import stsc.statistic.StatisticsSelector;
import stsc.testhelper.TestHelper;
import junit.framework.TestCase;

public class StrategyGridSearcherTest extends TestCase {
	public void testStrategyGridSearcher() throws Exception {
		final SimulatorSettingsGridIterator iterator = TestHelper.getSimulatorSettingsGridIterator();
		final StatisticsSelector<Double> selector = new StatisticsSelector<>(40000,
				new StatisticsInnerProductFunction());
		final StrategyGridSearcher searcher = new StrategyGridSearcher(iterator, selector);
		final SortedStatistics<Double> sortedStatistics = searcher.getSelector().getSelect();
		assertEquals(30720, sortedStatistics.size());
	}
}
