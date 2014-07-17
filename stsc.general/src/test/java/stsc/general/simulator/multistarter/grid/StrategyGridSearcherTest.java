package stsc.general.simulator.multistarter.grid;

import java.util.Arrays;

import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.general.simulator.multistarter.grid.StrategyGridSearcher;
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.StatisticsSelector;
import stsc.general.statistic.cost.function.WeightedSumCostFunction;
import stsc.general.testhelper.TestGridSimulatorSettings;
import stsc.storage.mocks.StockStorageMock;
import junit.framework.TestCase;

public class StrategyGridSearcherTest extends TestCase {
	public void testStrategyGridSearcher() throws Exception {
		final SimulatorSettingsGridList list = TestGridSimulatorSettings.getGridList(StockStorageMock.getStockStorage(), Arrays.asList(new String[] { "open" }),
				"31-01-2000");
		final StatisticsSelector selector = new StatisticsByCostSelector(6500, new WeightedSumCostFunction());
		final StrategyGridSearcher searcher = new StrategyGridSearcher(list, selector, 20);
		assertEquals(6144, searcher.getSelector().getStatistics().size());
	}
}
