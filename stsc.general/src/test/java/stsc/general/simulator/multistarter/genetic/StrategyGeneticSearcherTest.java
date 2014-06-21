package stsc.general.simulator.multistarter.genetic;

import java.util.ArrayList;
import java.util.List;

import stsc.general.simulator.multistarter.StrategySearcherException;
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.StatisticsSelector;
import stsc.general.statistic.cost.function.WeightedSumCostFunction;
import stsc.general.testhelper.TestHelper;
import stsc.storage.mocks.StockStorageMock;
import junit.framework.TestCase;

public class StrategyGeneticSearcherTest extends TestCase {
	public void testStrategyGeneticSearcher() throws InterruptedException, StrategySearcherException {
		final StatisticsSelector selector = new StatisticsByCostSelector(100, new WeightedSumCostFunction());

		List<GeneticExecutionInitializer> stocks = new ArrayList<>();
		List<GeneticExecutionInitializer> eods = new ArrayList<>();

		final SimulatorSettingsGeneticList geneticList = new SimulatorSettingsGeneticList(stocks, eods, new StockStorageMock(), TestHelper.getPeriod());
		final StrategyGeneticSearcher sgs = new StrategyGeneticSearcher(selector, geneticList, 4);
		sgs.getSelector();

		assertEquals(0, selector.getStatistics().size());
	}
}
