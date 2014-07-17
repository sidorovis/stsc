package stsc.general.simulator.multistarter.genetic;

import stsc.general.simulator.multistarter.StrategySearcherException;
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.StatisticsSelector;
import stsc.general.statistic.cost.function.WeightedSumCostFunction;
import stsc.general.testhelper.TestGeneticSimulatorSettings;
import junit.framework.TestCase;

public class StrategyGeneticSearcherTest extends TestCase {
	public void testStrategyGeneticSearcher() throws InterruptedException, StrategySearcherException {
		final StatisticsSelector selector = new StatisticsByCostSelector(100, new WeightedSumCostFunction());

		final SimulatorSettingsGeneticList geneticList = TestGeneticSimulatorSettings.getGeneticList();
		final StrategyGeneticSearcher sgs = new StrategyGeneticSearcher(selector, geneticList, 4, 100, 20);
		sgs.getSelector();

		assertEquals(100, selector.getStatistics().size());
	}
}
