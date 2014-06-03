package stsc.general.simulator.multistarter.grid;

import java.util.Arrays;

import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.general.simulator.multistarter.grid.StrategyGridSearcher;
import stsc.general.statistic.SortedStatistics;
import stsc.general.statistic.StatisticsSelector;
import stsc.general.statistic.cost.function.WeightedSumCostFunction;
import stsc.general.testhelper.TestSimulatorSettings;
import stsc.storage.mocks.StockStorageMock;
import junit.framework.TestCase;

public class StrategyGridSearcherTest extends TestCase {
	public void testStrategyGridSearcher() throws Exception {
		final SimulatorSettingsGridList list = TestSimulatorSettings.getGridList(StockStorageMock.getStockStorage(),
				Arrays.asList(new String[] { "open" }), "31-01-2000");
		final StatisticsSelector<Double> selector = new StatisticsSelector<>(6500, new WeightedSumCostFunction());
		final StrategyGridSearcher searcher = new StrategyGridSearcher(list, selector, 20);
		final SortedStatistics<Double> sortedStatistics = searcher.getSelector().getSortedStatistics();
		assertEquals(6144, sortedStatistics.size());
	}
}
