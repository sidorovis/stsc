package stsc.general.simulator.multistarter.genetic;

import stsc.common.Settings;
import stsc.general.simulator.multistarter.StrategySearcherException;
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.StrategySelector;
import stsc.general.statistic.cost.function.CostWeightedSumFunction;
import stsc.general.testhelper.TestGeneticSimulatorSettings;
import junit.framework.TestCase;

public class StrategyGeneticSearcherTest extends TestCase {

	public void testStrategyGeneticSearcher() throws InterruptedException, StrategySearcherException {
		final CostWeightedSumFunction costFunction = new CostWeightedSumFunction();
		costFunction.addParameter("getWinProb", 1.2);
		costFunction.addParameter("getKelly", 0.6);
		costFunction.addParameter("getDdDurationAvGain", 0.4);
		costFunction.addParameter("getFreq", 0.3);
		costFunction.addParameter("getSharpeRatio", 0.2);
		costFunction.addParameter("getMaxLoss", -0.3);
		costFunction.addParameter("getAvLoss", -0.5);

		final StrategySelector selector = new StatisticsByCostSelector(100, costFunction);

		final SimulatorSettingsGeneticList geneticList = TestGeneticSimulatorSettings.getBigGeneticList();
		final int maxGeneticStepsAmount = 100;
		final int populationSize = 100;
		final StrategyGeneticSearcher sgs = new StrategyGeneticSearcher(geneticList, selector, 4, costFunction, maxGeneticStepsAmount, populationSize, 0.94,
				0.86);
		sgs.getSelector();
		assertEquals(100, selector.getStrategies().size());
		assertEquals(32.979967, selector.getStrategies().get(0).getStatistics().getAvGain(), Settings.doubleEpsilon);
	}
}
