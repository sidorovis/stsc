package stsc.general.simulator.multistarter.genetic;

import java.text.DecimalFormat;

import stsc.common.TimeTracker;
import stsc.general.simulator.multistarter.StrategySearcherException;
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.StatisticsSelector;
import stsc.general.statistic.cost.function.WeightedSumCostFunction;
import stsc.general.testhelper.TestGeneticSimulatorSettings;
import junit.framework.TestCase;

public class StrategyGeneticSearcherTest extends TestCase {

	private void testHelper(int threadSize, double bestPart, double crossoverPart) throws InterruptedException, StrategySearcherException {
		double avTime = 0.0;
		double avMaxCost = 0.0;
		double avAvGain = 0.0;
		double avLastIndex = 0.0;
		double n = 10.0;
		for (int i = 0; i < n; ++i) {
			final TimeTracker tt = new TimeTracker();
			final WeightedSumCostFunction costFunction = new WeightedSumCostFunction();
			costFunction.addParameter("getWinProb", 1.2);
			costFunction.addParameter("getKelly", 0.6);
			costFunction.addParameter("getDdDurationAvGain", 0.4);
			costFunction.addParameter("getFreq", 0.3);
			costFunction.addParameter("getSharpeRatio", 0.2);
			costFunction.addParameter("getMaxLoss", -0.3);
			costFunction.addParameter("getAvLoss", -0.5);

			final StatisticsSelector selector = new StatisticsByCostSelector(100, costFunction);

			final SimulatorSettingsGeneticList geneticList = TestGeneticSimulatorSettings.getBigGeneticList();
			final int maxGeneticStepsAmount = 100;
			final int populationSize = 100;
			final StrategyGeneticSearcher sgs = new StrategyGeneticSearcher(selector, geneticList, threadSize, costFunction, maxGeneticStepsAmount,
					populationSize, bestPart, crossoverPart);
			sgs.getSelector();
			assertEquals(100, selector.getStatistics().size());
			tt.finish();
			avTime += tt.lengthInSeconds();
			avMaxCost += sgs.getMaxCostSum();
			avAvGain += sgs.getSelector().getStatistics().get(0).getAvGain();
			avLastIndex += sgs.getLastSelectionIndex();
		}
		final DecimalFormat decimalFormat = new DecimalFormat("#0.000");
		final String bestPartStr = decimalFormat.format(bestPart);
		final String crossoverPartStr = decimalFormat.format(crossoverPart);

		avTime /= n;
		avMaxCost /= n;
		avAvGain /= n;
		avLastIndex /= n;

		System.out.println(threadSize + " " + bestPartStr + " " + crossoverPartStr + " " + avLastIndex + " " + avMaxCost + " " + avAvGain + " " + avTime);
	}

	public void testStrategyGeneticSearcher() throws InterruptedException, StrategySearcherException {
		for (int t = 4; t <= 6; t += 1) {
			for (double i = 0.0; i <= 1.0; i += 0.2) {
				// for (double u = 0.0; u <= 1.0; u += 0.2) {
				// testHelper(t, i, u);
				testHelper(t, i, 0.4);
				// }
			}
		}
	}
}
