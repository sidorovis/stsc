package stsc.general.statistic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import stsc.general.simulator.SimulatorSettings;
import stsc.general.statistic.StatisticsByCostSelector;
import stsc.general.statistic.cost.function.WeightedSumCostFunction;
import stsc.general.strategy.Strategy;
import stsc.general.testhelper.TestGridSimulatorSettings;
import stsc.general.testhelper.TestHelper;
import junit.framework.TestCase;

public class StatisticsSelectorTest extends TestCase {
	public void testStatisticsSelector() {
		final WeightedSumCostFunction compareMethod = new WeightedSumCostFunction();
		final StrategySelector statisticsSelector = new StatisticsByCostSelector(2, compareMethod);

		final List<Double> values = new ArrayList<>();
		values.add(compareMethod.calculate(TestHelper.getStatistics(100, 200)));
		values.add(compareMethod.calculate(TestHelper.getStatistics(200, 250)));
		values.add(compareMethod.calculate(TestHelper.getStatistics(150, 210)));

		Iterator<SimulatorSettings> testSettings = TestGridSimulatorSettings.getGridList().iterator();

		statisticsSelector.addStrategy(new Strategy(testSettings.next(), TestHelper.getStatistics(100, 200)));
		statisticsSelector.addStrategy(new Strategy(testSettings.next(), TestHelper.getStatistics(200, 250)));
		statisticsSelector.addStrategy(new Strategy(testSettings.next(), TestHelper.getStatistics(150, 210)));

		final List<Strategy> strategies = statisticsSelector.getStrategies();
		assertEquals(2, strategies.size());
		assertEquals(compareMethod.calculate(((Strategy) strategies.toArray()[0]).getStatistics()), values.get(2));
		assertEquals(compareMethod.calculate(((Strategy) strategies.toArray()[1]).getStatistics()), values.get(0));
	}
}
