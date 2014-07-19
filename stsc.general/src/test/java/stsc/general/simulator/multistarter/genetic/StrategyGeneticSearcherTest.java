package stsc.general.simulator.multistarter.genetic;

import stsc.general.simulator.multistarter.StrategySearcherException;
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.StatisticsSelector;
import stsc.general.statistic.cost.function.WeightedSumCostFunction;
import stsc.general.testhelper.TestGeneticSimulatorSettings;
import junit.framework.TestCase;

public class StrategyGeneticSearcherTest extends TestCase {

	private void testHelper(double bestPart, double crossoverPart) throws InterruptedException, StrategySearcherException {
		final WeightedSumCostFunction costFunction = new WeightedSumCostFunction();
		costFunction.addParameter("getWinProb", 1.2);
		costFunction.addParameter("getKelly", 0.6);
		costFunction.addParameter("getDdDurationAvGain", 0.4);
		final StatisticsSelector selector = new StatisticsByCostSelector(100, costFunction);

		final SimulatorSettingsGeneticList geneticList = TestGeneticSimulatorSettings.getBigGeneticList();
		final StrategyGeneticSearcher sgs = new StrategyGeneticSearcher(selector, geneticList, 4, costFunction, 100, 100, bestPart, crossoverPart);
		sgs.getSelector();
		assertEquals(100, selector.getStatistics().size());
		System.out.println(bestPart + " " + crossoverPart + " " + sgs.getLastSelectionIndex() + " " + sgs.getMaxCostSum() + " "
				+ sgs.getSelector().getStatistics().get(0).getAvGain());
	}

	public void testStrategyGeneticSearcher() throws InterruptedException, StrategySearcherException {
		for (double i = 0.0; i <= 1.0; i += 0.2) {
			for (double u = 0.0; u <= 1.0; u += 0.2) {
				testHelper(i, u);
			}
		}
	}
}
