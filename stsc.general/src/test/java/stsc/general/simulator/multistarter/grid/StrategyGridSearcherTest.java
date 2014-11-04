package stsc.general.simulator.multistarter.grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import stsc.general.simulator.multistarter.StrategySearcher.IndicatorProgressListener;
import stsc.general.simulator.multistarter.StrategySearcherException;
import stsc.general.simulator.multistarter.grid.SimulatorSettingsGridList;
import stsc.general.simulator.multistarter.grid.StrategyGridSearcher;
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.StrategySelector;
import stsc.general.statistic.cost.function.CostWeightedSumFunction;
import stsc.general.testhelper.TestGridSimulatorSettings;
import stsc.storage.mocks.StockStorageMock;
import junit.framework.TestCase;

public class StrategyGridSearcherTest extends TestCase {

	public void testStrategyGridSearcher() throws Exception {
		final SimulatorSettingsGridList list = TestGridSimulatorSettings.getGridList(StockStorageMock.getStockStorage(),
				Arrays.asList(new String[] { "open" }), "31-01-2000");
		final StrategySelector selector = new StatisticsByCostSelector(6500, new CostWeightedSumFunction());
		final StrategyGridSearcher searcher = new StrategyGridSearcher(list, selector, 20);
		assertEquals(6144, searcher.getSelector().getStrategies().size());
	}

	public void testStrategyGridSearcherStop() throws StrategySearcherException {
		final SimulatorSettingsGridList list = TestGridSimulatorSettings.getGridList(StockStorageMock.getStockStorage(),
				Arrays.asList(new String[] { "open" }), "31-01-2000");
		final StrategySelector selector = new StatisticsByCostSelector(6500, new CostWeightedSumFunction());
		final StrategyGridSearcher searcher = new StrategyGridSearcher(list, selector, 20);
		searcher.stopSearch();
		assertTrue(6144 > searcher.getSelector().getStrategies().size());
	}

	public void testStrategyGridSearcherProcessingListener() throws StrategySearcherException {
		final SimulatorSettingsGridList list = TestGridSimulatorSettings.getGridList(StockStorageMock.getStockStorage(),
				Arrays.asList(new String[] { "open" }), "31-01-2000");

		final StrategySelector selector = new StatisticsByCostSelector(6500, new CostWeightedSumFunction());
		final StrategyGridSearcher searcher = new StrategyGridSearcher(list, selector, 1);

		final List<Double> elements = new ArrayList<>();
		searcher.addIndicatorProgress(new IndicatorProgressListener() {
			@Override
			public void processed(double percent) {
				elements.add(percent);
			}
		});
		assertEquals(6144, searcher.getSelector().getStrategies().size());
		assertEquals(1.0, elements.get(elements.size()-1));
	}
}
