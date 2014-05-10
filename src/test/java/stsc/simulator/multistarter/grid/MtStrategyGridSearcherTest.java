package stsc.simulator.multistarter.grid;

import java.util.Arrays;

import stsc.statistic.SortedStatistics;
import stsc.statistic.StatisticsInnerProductFunction;
import stsc.statistic.StatisticsSelector;
import stsc.testhelper.TestHelper;
import junit.framework.TestCase;

public class MtStrategyGridSearcherTest extends TestCase {
	public void testMtStrategyGridSearcher() throws InterruptedException {
		final SimulatorSettingsGridIterator iterator = TestHelper.getSimulatorSettingsGridIterator(
				Arrays.asList(new String[] { "open", "close" }), "31-01-2000");
		final StatisticsSelector<Double> selector = new StatisticsSelector<>(13000,
				new StatisticsInnerProductFunction());
		final MtStrategyGridSearcher searcher = new MtStrategyGridSearcher(iterator, selector, 20);
		final SortedStatistics<Double> sortedStatistics = searcher.getSelector().getSelect();
		assertEquals(12288, sortedStatistics.size());
	}
}
